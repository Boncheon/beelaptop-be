package com.example.sever.service.impl;

import com.example.sever.dto.OrderActionLog.OrderTimelineResponse;
import com.example.sever.dto.OrderActionLog.UpdateOrderStatusRequest;
import com.example.sever.entity.*;
import com.example.sever.exception.ResourceNotFoundException;
import com.example.sever.repository.*;
import com.example.sever.service.OrderTimelineService;
import com.example.sever.statusauto.OrderStatus;
import com.example.sever.statusauto.OrderType;
import com.example.sever.statusauto.PaymentStatus;
import com.example.sever.statusauto.SeriStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderTimelineServiceImpl implements OrderTimelineService {

    private final OrderRepository orderRepo;
    private final OrderActionLogRepository logRepo;

    private final HinhThucThanhToanChiTietRepository hinhThucThanhToanChiTietRepository;
    private final HinhThucThanhToanRepository hinhThucThanhToanRepository;
    private final OrderCTRepository orderCTRepo;
    private final SeriRepository seriRepo;
    private final GiamGiaHoaDonRepository giamGiaHoaDonRepo;
    private final PhieuGiamGiaRepository phieuGiamGiaRepo;


    @Override
    @Transactional(readOnly = true)
    public OrderTimelineResponse getTimeline(UUID orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order không tồn tại: " + orderId));

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

        // ✅ Đơn tại quầy không dùng updateStatus giao hàng
        if (OrderType.POS.dbValue().equalsIgnoreCase(order.getLoaiDon())) {
            throw new IllegalStateException("Đơn tại quầy không dùng API updateStatus giao hàng.");
        }

        int oldStatus = nvl(order.getTrangThai(), OrderStatus.PENDING_CONFIRM.code());
        int newStatus = request.getNewStatus();

        validateTransition(oldStatus, newStatus, order.getLoaiDon());

        if (newStatus == OrderStatus.CANCELED.code()) {

            // ✅ 1) Rule chung: SHIPPING trở lên thì KHÔNG AI hủy được (admin/staff cũng không)
            if (oldStatus >= OrderStatus.SHIPPING.code()) {
                throw new IllegalStateException("Đơn đang giao/đã giao, không thể hủy");
            }

            // ✅ 2) Rule quyền: chỉ ADMIN/STAFF mới được hủy khi đơn đã CONFIRMED trở lên
            // (nếu API này chỉ gọi từ admin panel thì đoạn này vẫn nên giữ để tránh lộ endpoint)
            if (!isAdminOrStaff() && oldStatus >= OrderStatus.CONFIRMED.code()) {
                throw new IllegalStateException("Đơn đã xác nhận, bạn không có quyền hủy.");
            }

            // ✅ 3) Rule thanh toán: đã PAID hoặc có phát sinh tiền thì không cho hủy
            int pay = nvl(order.getTrangThaiThanhToan(), PaymentStatus.UNPAID.code());
            BigDecimal paidAmount = hinhThucThanhToanChiTietRepository.sumSoTienByOrder(orderId);
            paidAmount = (paidAmount != null ? paidAmount : BigDecimal.ZERO);

            if (pay == PaymentStatus.PAID.code() || paidAmount.compareTo(BigDecimal.ZERO) > 0) {
                throw new IllegalStateException("Đơn đã thanh toán, không thể hủy (cần luồng refund/return).");
            }

            rollbackWhenCancel(order, request.getNote()); // trả seri + hoàn voucher
            List<OrderActionLog> logs = logRepo.findByIdOrder_IdOrderByNgayTaoAsc(orderId);
            return buildTimeline(order, logs);
        }



        int pay = nvl(order.getTrangThaiThanhToan(), PaymentStatus.UNPAID.code());

// ✅ Không cho hoàn thành nếu chưa thanh toán
        if (newStatus == OrderStatus.COMPLETED.code() && pay != PaymentStatus.PAID.code()) {
            if (!hasCOD(orderId)) {
                throw new IllegalStateException("Chưa thanh toán, không thể chuyển sang Hoàn thành.");
            }

            autoCollectCOD(order, request != null ? request.getNote() : null);

            pay = nvl(order.getTrangThaiThanhToan(), PaymentStatus.UNPAID.code());
            if (pay != PaymentStatus.PAID.code()) {
                throw new IllegalStateException("Thu COD thất bại, không thể hoàn tất.");
            }
        }

// ✅ ADD: Khi COMPLETED thì chốt seri PENDING -> SOLD
        if (newStatus == OrderStatus.COMPLETED.code()) {
            finalizeSeriWhenCompleted(orderId);
        }

        order.setTrangThai(newStatus);
        orderRepo.save(order);

        TaiKhoan actor = getCurrentActor();

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

    private void validateTransition(int from, int to, String loaiDon) {
        // đã kết thúc thì không cho đổi nữa
        if (from == OrderStatus.CANCELED.code() || from == OrderStatus.COMPLETED.code()) {
            throw new IllegalStateException("Đơn đã kết thúc, không thể đổi trạng thái");
        }


        // Hủy chỉ được khi CHƯA SHIPPING
        if (to == OrderStatus.CANCELED.code()) {
            if (from >= OrderStatus.SHIPPING.code()) {
                throw new IllegalStateException("Đơn đang giao/đã giao, không thể hủy");
            }
            return;
        }

        boolean isDelivery = OrderType.DELIVERY.dbValue().equalsIgnoreCase(
                loaiDon != null ? loaiDon.trim().toUpperCase() : ""
        );

        // flow chuẩn:
        // 1 -> 2 -> 3 -> 4 -> 5 -> 6
        boolean ok =
                (from == OrderStatus.PENDING_CONFIRM.code() && to == OrderStatus.CONFIRMED.code()) ||
                        (from == OrderStatus.CONFIRMED.code()       && to == OrderStatus.PREPARING.code()) ||
                        (from == OrderStatus.PREPARING.code()       && to == OrderStatus.SHIPPING.code()) ||
                        (from == OrderStatus.SHIPPING.code()        && to == OrderStatus.DELIVERED.code()) ||
                        (from == OrderStatus.DELIVERED.code()       && to == OrderStatus.COMPLETED.code()) ||

                        // ✅ riêng DELIVERY tạo từ POS: 0 -> 3
                        (isDelivery && from == OrderStatus.DRAFT.code() && to == OrderStatus.PREPARING.code());

        if (!ok) throw new IllegalStateException("Không cho phép chuyển trạng thái " + from + " -> " + to);
    }



    private OrderTimelineResponse buildTimeline(Order order, List<OrderActionLog> logs) {
        OrderTimelineResponse res = new OrderTimelineResponse();
        res.setOrderId(order.getId());
        res.setMaDonHang(order.getMaDonHang());
        res.setLoaiDon(order.getLoaiDon());

        String loai = order.getLoaiDon() != null ? order.getLoaiDon().trim().toUpperCase() : "";
        int st = order.getTrangThai() != null ? order.getTrangThai() : 0;

        // ====== 1) TẠI QUẦY ======
        // hỗ trợ cả data mới (0/6/7) và data cũ (1/2/3) nếu DB còn
        if (OrderType.POS.dbValue().equalsIgnoreCase(loai)) {

            int pay = nvl(order.getTrangThaiThanhToan(), PaymentStatus.UNPAID.code());
            res.setTrangThaiThanhToan(pay);
            res.setTenTrangThaiThanhToan(pay == PaymentStatus.PAID.code() ? "Đã thanh toán" : "Chưa thanh toán");

            boolean legacy = (st == 1 || st == 2 || st == 3);
            int[] codes = legacy ? new int[]{1,2,3} : new int[]{OrderStatus.DRAFT.code(), OrderStatus.COMPLETED.code(), OrderStatus.CANCELED.code()};

            res.setTrangThai(st);
            res.setTenTrangThai(legacy ? statusNameTaiQuayLegacy(st) : statusNameTaiQuay(st));

            List<OrderTimelineResponse.TimelineStepDTO> steps = new ArrayList<>();
            for (int code : codes) {
                String state;

                if (st == OrderStatus.CANCELED.code()) {
                    // minimal fix: tất cả UPCOMING, rồi add step CANCELLED ở dưới như bạn đang làm
                    state = "UPCOMING";
                } else {
                    if (code < st) state = "DONE";
                    else if (code == st) state = "CURRENT";
                    else state = "UPCOMING";
                }

                steps.add(new OrderTimelineResponse.TimelineStepDTO(code, statusName(code), state));
            }
            res.setSteps(steps);

            List<OrderTimelineResponse.TimelineLogDTO> logDtos = new ArrayList<>();
            for (OrderActionLog l : logs) {
                if (l.getHanhDong() == null) continue;

                // accept cả legacy (1/2/3) và new (0/6/7)
                if (legacy) {
                    if (l.getHanhDong() != 1 && l.getHanhDong() != 2 && l.getHanhDong() != 3) continue;
                    logDtos.add(toLogDTO(l, titleFromActionTaiQuayLegacy(l.getHanhDong())));
                } else {
                    if (l.getHanhDong() != OrderStatus.DRAFT.code()
                            && l.getHanhDong() != OrderStatus.COMPLETED.code()
                            && l.getHanhDong() != OrderStatus.CANCELED.code()) continue;
                    logDtos.add(toLogDTO(l, titleFromActionTaiQuay(l.getHanhDong())));
                }
            }
            res.setLogs(logDtos);
            return res;
        }

        // ====== 2) GIAO HÀNG: UI có thể muốn "bỏ qua 1-2", show từ 3 ======
        if (OrderType.DELIVERY.dbValue().equalsIgnoreCase(loai)) {

            res.setTrangThai(st);
            res.setTenTrangThai(statusName(st));

            int pay = nvl(order.getTrangThaiThanhToan(), PaymentStatus.UNPAID.code());
            res.setTrangThaiThanhToan(pay);
            res.setTenTrangThaiThanhToan(pay == PaymentStatus.PAID.code() ? "Đã thanh toán" : "Chưa thanh toán");

            int[] codes;
            if (st == OrderStatus.DRAFT.code()) {
                // ✅ đơn giao hàng tạo từ POS: 0 -> 3 -> 4 -> 5 -> 6 (+ cancel)
                codes = new int[]{
                        OrderStatus.DRAFT.code(),
                        OrderStatus.PREPARING.code(),
                        OrderStatus.SHIPPING.code(),
                        OrderStatus.DELIVERED.code(),
                        OrderStatus.COMPLETED.code()
                };
            } else {
                // ✅ đơn giao hàng/online bình thường: 1 -> 2 -> 3 -> 4 -> 5 -> 6
                codes = new int[]{
                        OrderStatus.PENDING_CONFIRM.code(),
                        OrderStatus.CONFIRMED.code(),
                        OrderStatus.PREPARING.code(),
                        OrderStatus.SHIPPING.code(),
                        OrderStatus.DELIVERED.code(),
                        OrderStatus.COMPLETED.code()
                };
            }

            List<OrderTimelineResponse.TimelineStepDTO> steps = new ArrayList<>();
            for (int code : codes) {
                String state;

                if (st == OrderStatus.CANCELED.code()) {
                    state = (code <= OrderStatus.CONFIRMED.code()) ? "DONE" : "UPCOMING";
                } else if (st <= OrderStatus.CONFIRMED.code()) {
                    if (code <= OrderStatus.CONFIRMED.code()) state = "DONE";
                    else if (code == OrderStatus.PREPARING.code()) state = "CURRENT";
                    else state = "UPCOMING";
                } else {
                    if (code < st) state = "DONE";
                    else if (code == st) state = "CURRENT";
                    else state = "UPCOMING";
                }

                steps.add(new OrderTimelineResponse.TimelineStepDTO(code, statusName(code), state));
            }

            if (st == OrderStatus.CANCELED.code()) {
                steps.add(new OrderTimelineResponse.TimelineStepDTO(OrderStatus.CANCELED.code(), "Hủy đơn", "CANCELLED"));
            }
            res.setSteps(steps);

            List<OrderTimelineResponse.TimelineLogDTO> logDtos = new ArrayList<>();
            for (OrderActionLog l : logs) {
                Integer a = l.getHanhDong();
                if (a == null) continue;

                if (a == OrderStatus.PENDING_CONFIRM.code()
                        || a == OrderStatus.CONFIRMED.code()
                        || a == OrderStatus.PREPARING.code()
                        || a == OrderStatus.SHIPPING.code()
                        || a == OrderStatus.DELIVERED.code()
                        || a == OrderStatus.COMPLETED.code()
                        || a == OrderStatus.CANCELED.code()) {
                    logDtos.add(toLogDTO(l, titleFromAction(a)));
                }
            }
            res.setLogs(logDtos);

            return res;
        }

        // ====== 3) ONLINE (mặc định): steps 1..6 + cancel ======
        int stOnline = nvl(order.getTrangThai(), OrderStatus.PENDING_CONFIRM.code());
        res.setTrangThai(stOnline);
        res.setTenTrangThai(statusName(stOnline));

        int pay = nvl(order.getTrangThaiThanhToan(), PaymentStatus.UNPAID.code());
        res.setTrangThaiThanhToan(pay);
        res.setTenTrangThaiThanhToan(pay == PaymentStatus.PAID.code() ? "Đã thanh toán" : "Chưa thanh toán");

        List<OrderTimelineResponse.TimelineStepDTO> steps = new ArrayList<>();
        int[] codes = {
                OrderStatus.PENDING_CONFIRM.code(),
                OrderStatus.CONFIRMED.code(),
                OrderStatus.PREPARING.code(),
                OrderStatus.SHIPPING.code(),
                OrderStatus.DELIVERED.code(),
                OrderStatus.COMPLETED.code()
        };

        for (int code : codes) {
            String state;
            if (stOnline == OrderStatus.CANCELED.code()) state = "UPCOMING";
            else if (code < stOnline) state = "DONE";
            else if (code == stOnline) state = "CURRENT";
            else state = "UPCOMING";

            steps.add(new OrderTimelineResponse.TimelineStepDTO(code, statusName(code), state));
        }

        if (stOnline == OrderStatus.CANCELED.code()) {
            steps.add(new OrderTimelineResponse.TimelineStepDTO(OrderStatus.CANCELED.code(), "Hủy đơn", "CANCELLED"));
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

    private void rollbackWhenCancel(Order order, String note) {
        int st = nvl(order.getTrangThai(), OrderStatus.PENDING_CONFIRM.code());

        // ✅ Rule: đang SHIPPING trở lên thì không ai hủy được
        if (st >= OrderStatus.SHIPPING.code()) {
            throw new IllegalStateException("Đơn đang giao/đã giao, không thể hủy");
        }

        // 1) set trạng thái hủy
        order.setTrangThai(OrderStatus.CANCELED.code());
        orderRepo.save(order);

        // 2) trả seri: PENDING -> ACTIVE (idempotent)
        List<OrderCT> items = orderCTRepo.findByIdOrder_Id(order.getId());
        for (OrderCT ct : items) {
            if (ct.getIdSeri() == null || ct.getIdSeri().getId() == null) continue;
            UUID seriId = ct.getIdSeri().getId();

            // ✅ chỉ trả khi đang PENDING
            seriRepo.updateTrangThaiSeriIfCurrent(
                    seriId,
                    SeriStatus.PENDING.code(),
                    SeriStatus.ACTIVE.code()
            );
        }

        // 3) hoàn voucher (đơn ONLINE/GIAO_HANG bạn đã trừ ngay lúc tạo)
        // lấy GGHD để biết voucher nào đã áp
        List<GiamGiaHoaDon> ggList = giamGiaHoaDonRepo.findByIdOrders_Id(order.getId());
        if (ggList != null && !ggList.isEmpty()) {
            PhieuGiamGia pgg = ggList.get(0).getIdPhieuGiamGia();
            if (pgg != null && pgg.getId() != null) {
                // ✅ cách 1: load + save (đơn giản)
                PhieuGiamGia v = phieuGiamGiaRepo.findById(pgg.getId()).orElse(null);
                if (v != null) {
                    Integer qty = v.getSoLuong();
                    v.setSoLuong(qty == null ? 1 : qty + 1);
                    // optional: bật lại nếu đang tắt vì 0
                    if (v.getTrangThai() != null && v.getTrangThai() == 0) v.setTrangThai(1);
                    phieuGiamGiaRepo.save(v);
                }
            }
        }

        // 4) ghi log cancel (hoặc để updateStatus ghi)
        TaiKhoan actor = getCurrentActor();
        OrderActionLog log = new OrderActionLog();
        log.setId(UUID.randomUUID());
        log.setIdOrder(order);
        log.setIdOrderacl("ACL" + System.currentTimeMillis());
        log.setHanhDong(OrderStatus.CANCELED.code());
        log.setIdTaiKhoan(actor);
        log.setMoTa(buildLogMessage(OrderStatus.CANCELED.code(), note, actor));
        log.setNgayTao(Instant.now());
        logRepo.save(log);
    }

    private String buildLogMessage(int newStatus, String note, TaiKhoan actor) {
        String base = switch (newStatus) {
            case 0 -> "Đơn hàng được tạo (nháp).";
            case 1 -> "Đơn hàng đang chờ xác nhận.";
            case 2 -> "Đơn hàng đã được xác nhận.";
            case 3 -> "Đơn hàng đang được chuẩn bị.";
            case 4 -> "Đơn hàng đang được giao.";
            case 5 -> "Đơn hàng đã giao thành công.";
            case 6 -> "Đơn hàng đã hoàn thành.";
            case 7 -> "Đơn hàng đã bị hủy.";
            default -> "Cập nhật trạng thái đơn hàng.";
        };
        if (note != null && !note.trim().isEmpty()) base += " Ghi chú: " + note.trim();
        return base;
    }

    private String titleFromAction(Integer action) {
        if (action == null) return "Cập nhật đơn hàng";
        return switch (action) {
            case 0 -> "Tạo đơn";
            case 1 -> "Chờ xác nhận";
            case 2 -> "Đã xác nhận";
            case 3 -> "Đang chuẩn bị hàng";
            case 4 -> "Đang giao hàng";
            case 5 -> "Đã giao hàng";
            case 6 -> "Hoàn thành";
            case 7 -> "Hủy đơn";
            default -> "Cập nhật đơn hàng";
        };
    }

    private String statusName(int st) {
        return switch (st) {
            case 0 -> "Tạo đơn";
            case 1 -> "Chờ xác nhận";
            case 2 -> "Đã xác nhận";
            case 3 -> "Đang chuẩn bị hàng";
            case 4 -> "Đang giao hàng";
            case 5 -> "Đã giao hàng";
            case 6 -> "Hoàn thành";
            case 7 -> "Hủy đơn";
            default -> "Không xác định";
        };
    }

    // POS theo enum mới
    private String statusNameTaiQuay(int st) {
        return switch (st) {
            case 0 -> "Tạo đơn";
            case 6 -> "Hoàn tất";
            case 7 -> "Hủy đơn";
            default -> "Không xác định";
        };
    }

    private String titleFromActionTaiQuay(int action) {
        return switch (action) {
            case 0 -> "Tạo đơn";
            case 6 -> "Hoàn tất";
            case 7 -> "Hủy đơn";
            default -> "Cập nhật đơn";
        };
    }

    // POS legacy (nếu DB còn lưu 1/2/3)
    private String statusNameTaiQuayLegacy(int st) {
        return switch (st) {
            case 1 -> "Tạo đơn";
            case 2 -> "Hoàn tất";
            case 3 -> "Hủy đơn";
            default -> "Không xác định";
        };
    }

    private String titleFromActionTaiQuayLegacy(int action) {
        return switch (action) {
            case 1 -> "Tạo đơn";
            case 2 -> "Hoàn tất";
            case 3 -> "Hủy đơn";
            default -> "Cập nhật đơn";
        };
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

    private boolean hasCOD(UUID orderId) {
        List<String> names = hinhThucThanhToanChiTietRepository.findTenHinhThucThanhToanByIdOrder(orderId);
        if (names == null) return false;
        return names.stream().anyMatch(n -> n != null && n.trim().equalsIgnoreCase("COD"));
    }
    private void autoCollectCOD(Order order, String note) {
        BigDecimal mustPay = order.getTongTienThuHo() != null ? order.getTongTienThuHo() : BigDecimal.ZERO;

        BigDecimal alreadyPaid = hinhThucThanhToanChiTietRepository.sumSoTienByOrder(order.getId());
        alreadyPaid = alreadyPaid != null ? alreadyPaid : BigDecimal.ZERO;

        BigDecimal remaining = mustPay.subtract(alreadyPaid);

        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            order.setTrangThaiThanhToan(PaymentStatus.PAID.code());
            orderRepo.save(order);
            return;
        }

        // ✅ tìm dòng COD 0đ đã tồn tại để update
        var codZeroOpt = hinhThucThanhToanChiTietRepository
                .findFirstByIdOrder_IdAndIdHinhThucThanhToan_TenHinhThucIgnoreCaseAndSoTienThanhToan(
                        order.getId(), "COD", BigDecimal.ZERO
                );

        if (codZeroOpt.isPresent()) {
            HinhThucThanhToanChiTiet codLine = codZeroOpt.get();
            codLine.setSoTienThanhToan(remaining);

            String n = (note != null ? note.trim() : "");
            codLine.setGhiChu(n.isEmpty()
                    ? "Đã thu COD khi hoàn tất"
                    : ("Đã thu COD khi hoàn tất. " + n)
            );

            hinhThucThanhToanChiTietRepository.save(codLine);
        } else {
            // fallback: nếu vì lý do nào đó không có dòng COD 0đ thì mới tạo mới
            HinhThucThanhToan codMethod = hinhThucThanhToanRepository.findByTenHinhThucIgnoreCase("COD")
                    .orElseGet(() -> hinhThucThanhToanRepository.findFirstByTenHinhThucContainingIgnoreCase("COD")
                            .orElseThrow(() -> new IllegalStateException("Chưa có COD trong DB")));

            HinhThucThanhToanChiTiet p = new HinhThucThanhToanChiTiet();
            p.setId(UUID.randomUUID());
            p.setIdOrder(order);
            p.setIdHinhThucThanhToan(codMethod);
            p.setSoTienThanhToan(remaining);

            String n = (note != null ? note.trim() : "");
            p.setGhiChu(n.isEmpty() ? "Auto thu COD khi hoàn tất" : ("Auto thu COD khi hoàn tất. " + n));

            p.setIdThanhToanCt(("TT" + UUID.randomUUID().toString().replace("-", "")).substring(0, 20).toUpperCase());
            hinhThucThanhToanChiTietRepository.save(p);
        }

        order.setTrangThaiThanhToan(PaymentStatus.PAID.code());
        orderRepo.save(order);
    }

    private void finalizeSeriWhenCompleted(UUID orderId) {
        List<OrderCT> items = orderCTRepo.findByIdOrder_Id(orderId);
        for (OrderCT ct : items) {
            if (ct.getIdSeri() == null || ct.getIdSeri().getId() == null) continue;

            UUID seriId = ct.getIdSeri().getId();

            // cố gắng chốt PENDING -> SOLD
            int updated = seriRepo.updateTrangThaiSeriIfCurrent(
                    seriId,
                    SeriStatus.PENDING.code(),
                    SeriStatus.SOLD.code()
            );

            if (updated == 1) continue;

            // idempotent: nếu đã SOLD rồi thì thôi (trường hợp VNPay/MoMo đã chốt trước)
            Seri current = seriRepo.findById(seriId).orElse(null);
            if (current != null && Objects.equals(current.getTrangThai(), SeriStatus.SOLD.code())) {
                continue;
            }

            // còn lại là bất thường
            String code = (ct.getIdSeri() != null ? ct.getIdSeri().getIdSeri() : seriId.toString());
            throw new IllegalStateException("Không thể chốt Seri '" + code + "' sang SOLD (không ở PENDING/SOLD).");
        }
    }

    private boolean isAdminOrStaff() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;
        return auth.getAuthorities().stream().anyMatch(a -> {
            String r = a.getAuthority();
            return "ROLE_ADMIN".equalsIgnoreCase(r) || "ROLE_STAFF".equalsIgnoreCase(r) || "ROLE_EMPLOYEE".equalsIgnoreCase(r);
        });
    }


}
