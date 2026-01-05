package com.example.sever.service.impl;

import com.example.sever.dto.OrderActionLog.OrderTimelineResponse;
import com.example.sever.dto.OrderActionLog.UpdateOrderStatusRequest;
import com.example.sever.entity.Order;
import com.example.sever.entity.OrderActionLog;
import com.example.sever.entity.TaiKhoan;
import com.example.sever.exception.ResourceNotFoundException;
import com.example.sever.repository.OrderActionLogRepository;
import com.example.sever.repository.OrderRepository;
import com.example.sever.service.OrderTimelineService;
import com.example.sever.utils.OrderConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderTimelineServiceImpl implements OrderTimelineService {

    private final OrderRepository orderRepo;
    private final OrderActionLogRepository logRepo;

    @Override
    @Transactional(readOnly = true)
    public OrderTimelineResponse getTimeline(UUID orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order không tồn tại: " + orderId));

        // ✅ lấy toàn bộ log
        List<OrderActionLog> logs = logRepo.findByIdOrder_IdOrderByNgayTaoAsc(orderId);

        return buildTimeline(order, logs);
    }

    @Override
    @Transactional
    public OrderTimelineResponse updateStatus(UUID orderId, UpdateOrderStatusRequest request) {
        if (request == null || request.getNewStatus() == null) {
            throw new IllegalArgumentException("Thiếu newStatus");
        }

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order không tồn tại: " + orderId));

        // ✅ Đơn tại quầy không dùng updateStatus kiểu giao hàng
        if ("TAI_QUAY".equalsIgnoreCase(order.getLoaiDon())) {
            throw new IllegalStateException("Đơn tại quầy không dùng API updateStatus giao hàng.");
        }

        int oldStatus = nvl(order.getTrangThai(), OrderConstants.ST_WAIT_CONFIRM);
        int newStatus = request.getNewStatus();

        validateTransition(oldStatus, newStatus);

        order.setTrangThai(newStatus);
        orderRepo.save(order);

        TaiKhoan actor = getCurrentActor();

        // ✅ INSERT log mới mỗi lần đổi trạng thái
        OrderActionLog log = new OrderActionLog();
        log.setId(UUID.randomUUID());
        log.setIdOrder(order);
        log.setIdOrderacl(genACL());
        log.setHanhDong(newStatus);
        log.setIdTaiKhoan(actor);
        log.setMoTa(buildLogMessage(newStatus, request.getNote(), actor));
        log.setNgayTao(Instant.now());

        logRepo.save(log);

        List<OrderActionLog> logs = logRepo.findByIdOrder_IdOrderByNgayTaoAsc(orderId);
        return buildTimeline(order, logs);
    }

    // ===== helper =====

    private String genACL() {
        return "ACL" + System.currentTimeMillis();
    }

    private void validateTransition(int from, int to) {
        if (from == OrderConstants.ST_CANCEL || from == OrderConstants.ST_DONE) {
            throw new IllegalStateException("Đơn đã kết thúc, không thể đổi trạng thái");
        }
        if (to == OrderConstants.ST_CANCEL) return;

        boolean ok =
                (from == OrderConstants.ST_WAIT_CONFIRM && to == OrderConstants.ST_CONFIRMED) ||
                        (from == OrderConstants.ST_CONFIRMED    && to == OrderConstants.ST_PREPARING) ||
                        (from == OrderConstants.ST_PREPARING    && to == OrderConstants.ST_READY_SHIP) ||
                        (from == OrderConstants.ST_READY_SHIP   && to == OrderConstants.ST_SHIPPING) ||
                        (from == OrderConstants.ST_SHIPPING     && to == OrderConstants.ST_DONE);

        if (!ok) throw new IllegalStateException("Không cho phép chuyển trạng thái " + from + " -> " + to);
    }

    private OrderTimelineResponse buildTimeline(Order order, List<OrderActionLog> logs) {
        OrderTimelineResponse res = new OrderTimelineResponse();
        res.setOrderId(order.getId());
        res.setMaDonHang(order.getMaDonHang());

        String loai = order.getLoaiDon() != null ? order.getLoaiDon().trim().toUpperCase() : "";
        int st = order.getTrangThai() != null ? order.getTrangThai() : 0;

        // ====== 1) TẠI QUẦY: steps = 1/2/3 ======
        if ("TAI_QUAY".equals(loai)) {
            res.setTrangThai(st);
            res.setTenTrangThai(statusNameTaiQuay(st));
            res.setTrangThaiThanhToan(nvl(order.getTrangThaiThanhToan(), OrderConstants.PAY_UNPAID));
            res.setTenTrangThaiThanhToan(res.getTrangThaiThanhToan() == OrderConstants.PAY_PAID ? "Đã thanh toán" : "Chưa thanh toán");

            int[] codes = {1, 2, 3}; // tạo / hoàn tất / hủy
            List<OrderTimelineResponse.TimelineStepDTO> steps = new ArrayList<>();
            for (int code : codes) {
                String state;
                if (st == 3) {
                    state = (code == 3) ? "CANCELLED" : "UPCOMING";
                } else if (code < st) state = "DONE";
                else if (code == st) state = "CURRENT";
                else state = "UPCOMING";

                steps.add(new OrderTimelineResponse.TimelineStepDTO(code, statusNameTaiQuay(code), state));
            }
            res.setSteps(steps);

            // logs chỉ lấy 1/2/3
            List<OrderTimelineResponse.TimelineLogDTO> logDtos = new ArrayList<>();
            for (OrderActionLog l : logs) {
                if (l.getHanhDong() == null) continue;
                if (l.getHanhDong() != 1 && l.getHanhDong() != 2 && l.getHanhDong() != 3) continue;

                logDtos.add(toLogDTO(l, titleFromActionTaiQuay(l.getHanhDong())));
            }
            res.setLogs(logDtos);
            return res;
        }

        // ====== 2) GIAO HÀNG: ẩn bước xác nhận, chỉ show 3..6 + cancel ======
        if ("GIAO_HANG".equals(loai)) {

            // nếu DB đang lưu st=1/2 thì UI vẫn muốn coi như đang ở bước 3
            int stDisplay = st;
            if (stDisplay != OrderConstants.ST_CANCEL && stDisplay <= OrderConstants.ST_CONFIRMED) {
                stDisplay = OrderConstants.ST_PREPARING; // ép current UI từ bước 3
            }

            res.setTrangThai(stDisplay);
            res.setTenTrangThai(statusName(stDisplay));
            res.setTrangThaiThanhToan(nvl(order.getTrangThaiThanhToan(), OrderConstants.PAY_UNPAID));
            res.setTenTrangThaiThanhToan(
                    res.getTrangThaiThanhToan() == OrderConstants.PAY_PAID ? "Đã thanh toán" : "Chưa thanh toán"
            );

            int[] codes = {
                    OrderConstants.ST_WAIT_CONFIRM,
                    OrderConstants.ST_CONFIRMED,
                    OrderConstants.ST_PREPARING,
                    OrderConstants.ST_READY_SHIP,
                    OrderConstants.ST_SHIPPING,
                    OrderConstants.ST_DONE
            };

            List<OrderTimelineResponse.TimelineStepDTO> steps = new ArrayList<>();
            for (int code : codes) {
                String state;

                if (st == OrderConstants.ST_CANCEL) {
                    // nếu đã hủy: 1-2 vẫn coi là DONE (vì giao hàng mặc định đã xác nhận),
                    // các bước sau là UPCOMING
                    state = (code <= OrderConstants.ST_CONFIRMED) ? "DONE" : "UPCOMING";
                } else if (st <= OrderConstants.ST_CONFIRMED) {
                    // giao hàng đã xác nhận sẵn => 1-2 DONE, 3 CURRENT
                    if (code <= OrderConstants.ST_CONFIRMED) state = "DONE";
                    else if (code == OrderConstants.ST_PREPARING) state = "CURRENT";
                    else state = "UPCOMING";
                } else {
                    // bình thường theo trạng thái hiện tại
                    if (code < st) state = "DONE";
                    else if (code == st) state = "CURRENT";
                    else state = "UPCOMING";
                }

                steps.add(new OrderTimelineResponse.TimelineStepDTO(code, statusName(code), state));
            }

            if (st == OrderConstants.ST_CANCEL) {
                steps.add(new OrderTimelineResponse.TimelineStepDTO(OrderConstants.ST_CANCEL, "Hủy đơn", "CANCELLED"));
            }
            res.setSteps(steps);

            // logs: cho phép lấy cả 1..6 và cancel (nếu có)
            List<OrderTimelineResponse.TimelineLogDTO> logDtos = new ArrayList<>();
            for (OrderActionLog l : logs) {
                Integer a = l.getHanhDong();
                if (a == null) continue;

                if (a == OrderConstants.ST_WAIT_CONFIRM || a == OrderConstants.ST_CONFIRMED
                        || a == OrderConstants.ST_PREPARING || a == OrderConstants.ST_READY_SHIP
                        || a == OrderConstants.ST_SHIPPING || a == OrderConstants.ST_DONE
                        || a == OrderConstants.ST_CANCEL) {
                    logDtos.add(toLogDTO(l, titleFromAction(a)));
                }
            }
            res.setLogs(logDtos);

            return res;
        }

        // ====== 3) ONLINE (mặc định): steps 1..6 + cancel ======
        int stOnline = nvl(order.getTrangThai(), OrderConstants.ST_WAIT_CONFIRM);
        res.setTrangThai(stOnline);
        res.setTenTrangThai(statusName(stOnline));

        int pay = nvl(order.getTrangThaiThanhToan(), OrderConstants.PAY_UNPAID);
        res.setTrangThaiThanhToan(pay);
        res.setTenTrangThaiThanhToan(pay == OrderConstants.PAY_PAID ? "Đã thanh toán" : "Chưa thanh toán");

        List<OrderTimelineResponse.TimelineStepDTO> steps = new ArrayList<>();
        int[] codes = {
                OrderConstants.ST_WAIT_CONFIRM,
                OrderConstants.ST_CONFIRMED,
                OrderConstants.ST_PREPARING,
                OrderConstants.ST_READY_SHIP,
                OrderConstants.ST_SHIPPING,
                OrderConstants.ST_DONE
        };

        for (int code : codes) {
            String state;
            if (stOnline == OrderConstants.ST_CANCEL) state = "UPCOMING";
            else if (code < stOnline) state = "DONE";
            else if (code == stOnline) state = "CURRENT";
            else state = "UPCOMING";

            steps.add(new OrderTimelineResponse.TimelineStepDTO(code, statusName(code), state));
        }

        if (stOnline == OrderConstants.ST_CANCEL) {
            steps.add(new OrderTimelineResponse.TimelineStepDTO(OrderConstants.ST_CANCEL, "Hủy đơn", "CANCELLED"));
        }
        res.setSteps(steps);

        List<OrderTimelineResponse.TimelineLogDTO> logDtos = new ArrayList<>();
        for (OrderActionLog l : logs) {
            logDtos.add(toLogDTO(l, titleFromAction(l.getHanhDong())));
        }
        res.setLogs(logDtos);

        return res;
    }

    private OrderTimelineResponse.TimelineLogDTO toLogDTO(OrderActionLog l, String title) {
        String by = (l.getIdTaiKhoan() != null
                && l.getIdTaiKhoan().getTen() != null
                && !l.getIdTaiKhoan().getTen().isBlank())
                ? l.getIdTaiKhoan().getTen()
                : "Hệ thống";

        return new OrderTimelineResponse.TimelineLogDTO(
                l.getNgayTao() != null ? l.getNgayTao() : Instant.now(),
                title != null ? title : "Cập nhật đơn hàng",
                l.getMoTa(),
                by
        );
    }

    private String buildLogMessage(int newStatus, String note, TaiKhoan actor) {
        String base = switch (newStatus) {
            case OrderConstants.ST_WAIT_CONFIRM -> "Đơn hàng đang chờ xác nhận.";
            case OrderConstants.ST_CONFIRMED    -> "Đơn hàng đã được xác nhận.";
            case OrderConstants.ST_PREPARING    -> "Đơn hàng đang được chuẩn bị.";
            case OrderConstants.ST_READY_SHIP   -> "Đơn hàng đã sẵn sàng giao.";
            case OrderConstants.ST_SHIPPING     -> "Đơn hàng đang được giao.";
            case OrderConstants.ST_DONE         -> "Đơn hàng đã hoàn thành.";
            case OrderConstants.ST_CANCEL       -> "Đơn hàng đã bị hủy.";
            default -> "Cập nhật trạng thái đơn hàng.";
        };
        if (note != null && !note.trim().isEmpty()) base += " Ghi chú: " + note.trim();
        return base;
    }

    private String titleFromAction(Integer action) {
        if (action == null) return "Cập nhật đơn hàng";
        return switch (action) {
            case OrderConstants.ST_WAIT_CONFIRM -> "Chờ xác nhận";
            case OrderConstants.ST_CONFIRMED    -> "Đã xác nhận";
            case OrderConstants.ST_PREPARING    -> "Đang chuẩn bị hàng";
            case OrderConstants.ST_READY_SHIP   -> "Chuẩn bị giao hàng";
            case OrderConstants.ST_SHIPPING     -> "Đang giao hàng";
            case OrderConstants.ST_DONE         -> "Hoàn thành";
            case OrderConstants.ST_CANCEL       -> "Hủy đơn";
            default -> "Cập nhật đơn hàng";
        };
    }

    private String statusName(int st) {
        return switch (st) {
            case OrderConstants.ST_WAIT_CONFIRM -> "Chờ xác nhận";
            case OrderConstants.ST_CONFIRMED    -> "Đã xác nhận";
            case OrderConstants.ST_PREPARING    -> "Đang chuẩn bị hàng";
            case OrderConstants.ST_READY_SHIP   -> "Chuẩn bị giao hàng";
            case OrderConstants.ST_SHIPPING     -> "Đang giao hàng";
            case OrderConstants.ST_DONE         -> "Hoàn thành";
            case OrderConstants.ST_CANCEL       -> "Hủy đơn";
            default -> "Không xác định";
        };
    }

    private String statusNameTaiQuay(int st) {
        return switch (st) {
            case 1 -> "Tạo đơn";
            case 2 -> "Hoàn tất";
            case 3 -> "Hủy đơn";
            default -> "Không xác định";
        };
    }

    private String titleFromActionTaiQuay(int action) {
        return switch (action) {
            case 1 -> "Tạo đơn";
            case 2 -> "Hoàn tất";
            case 3 -> "Hủy đơn";
            default -> "Cập nhật đơn";
        };
    }

    private String statusNameGiaoHang(int st) {
        // giao hàng có thể ở 2 (confirmed) nhưng UI sẽ show các bước sau
        if (st == OrderConstants.ST_CONFIRMED) return "Đã xác nhận";
        return statusName(st);
    }

    private int nvl(Integer v, int def) {
        return v == null ? def : v;
    }

    private TaiKhoan getCurrentActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof TaiKhoan tk) return tk;
        return null;
    }
}
