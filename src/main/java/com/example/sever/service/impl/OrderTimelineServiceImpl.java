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

        // ✅ 1 log duy nhất
        List<OrderActionLog> logs = logRepo.findByIdOrder_Id(orderId)
                .map(List::of)
                .orElseGet(List::of);

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

        int oldStatus = nvl(order.getTrangThai(), OrderConstants.ST_WAIT_CONFIRM);
        int newStatus = request.getNewStatus();

        validateTransition(oldStatus, newStatus);

        order.setTrangThai(newStatus);
        orderRepo.save(order);

        TaiKhoan actor = getCurrentActor();

        // ✅ UP SERT: 1 order chỉ có 1 log
        OrderActionLog log = logRepo.findByIdOrder_Id(orderId).orElseGet(() -> {
            OrderActionLog l = new OrderActionLog();
            l.setId(UUID.randomUUID());
            l.setIdOrder(order);
            l.setIdOrderacl(genACL());
            return l;
        });

        log.setHanhDong(newStatus);
        log.setIdTaiKhoan(actor); // null = hệ thống
        log.setMoTa(buildLogMessage(newStatus, request.getNote(), actor));
        log.setNgayTao(Instant.now()); // cập nhật time

        logRepo.save(log);

        List<OrderActionLog> logs = List.of(log);
        return buildTimeline(order, logs);
    }

    // ===== helper =====

    private String genACL() {
        Random rand = new Random();
        StringBuilder acl = new StringBuilder("ACL");
        for (int i = 0; i < 6; i++) acl.append(rand.nextInt(10));
        return acl.toString();
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

        int st = nvl(order.getTrangThai(), OrderConstants.ST_WAIT_CONFIRM);
        res.setTrangThai(st);
        res.setTenTrangThai(statusName(st));

        int pay = nvl(order.getTrangThaiThanhToan(), OrderConstants.PAY_UNPAID);
        res.setTrangThaiThanhToan(pay);
        res.setTenTrangThaiThanhToan(pay == OrderConstants.PAY_PAID ? "Đã thanh toán" : "Chưa thanh toán");

        // steps 1..6 (cancel đặc biệt)
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
            if (st == OrderConstants.ST_CANCEL) state = "UPCOMING";
            else if (code < st) state = "DONE";
            else if (code == st) state = "CURRENT";
            else state = "UPCOMING";

            steps.add(new OrderTimelineResponse.TimelineStepDTO(code, statusName(code), state));
        }

        if (st == OrderConstants.ST_CANCEL) {
            steps.add(new OrderTimelineResponse.TimelineStepDTO(OrderConstants.ST_CANCEL, "Hủy đơn", "CANCELLED"));
        }
        res.setSteps(steps);

        // logs (1 phần tử hoặc rỗng)
        List<OrderTimelineResponse.TimelineLogDTO> logDtos = new ArrayList<>();
        for (OrderActionLog l : logs) {
            String by = (l.getIdTaiKhoan() != null && l.getIdTaiKhoan().getTen() != null && !l.getIdTaiKhoan().getTen().isBlank())
                    ? l.getIdTaiKhoan().getTen()
                    : "Hệ thống";

            logDtos.add(new OrderTimelineResponse.TimelineLogDTO(
                    l.getNgayTao() != null ? l.getNgayTao() : Instant.now(),
                    titleFromAction(l.getHanhDong()),
                    l.getMoTa(),
                    by
            ));
        }
        res.setLogs(logDtos);

        return res;
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
