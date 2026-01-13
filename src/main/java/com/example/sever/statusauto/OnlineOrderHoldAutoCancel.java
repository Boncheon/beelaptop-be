package com.example.sever.statusauto;

import com.example.sever.entity.Order;
import com.example.sever.entity.OrderCT;
import com.example.sever.repository.HinhThucThanhToanChiTietRepository;
import com.example.sever.repository.OrderCTRepository;
import com.example.sever.repository.OrderRepository;
import com.example.sever.repository.SeriRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Auto-cancel ONLINE orders to avoid giữ hàng vô thời hạn.
 *
 * 1) Prepaid (VNPay/MoMo/Bank...): nếu vẫn UNPAID quá lâu => huỷ + trả seri.
 * 2) COD: chỉ huỷ khi còn ở PENDING_CONFIRM quá lâu (shop chưa xác nhận) => huỷ + trả seri.
 */
@Component
@RequiredArgsConstructor
public class OnlineOrderHoldAutoCancel {

    private final OrderRepository orderRepository;
    private final OrderCTRepository orderCTRepository;
    private final SeriRepository seriRepository;
    private final HinhThucThanhToanChiTietRepository paymentDetailRepository;

    // ===== TTL CONFIG =====
    // VNPay/MoMo/Bank: giữ theo phút (ngắn)
    @Value("${app.order.online.hold.vnpay-minutes:15}")
    private long vnpayHoldMinutes;

    // COD: giữ theo giờ (dài hơn) nhưng chỉ áp dụng khi vẫn PENDING_CONFIRM
    @Value("${app.order.online.hold.cod-hours:24}")
    private long codHoldHours;

    // ===== ENUM CODES (theo enum bạn gửi) =====
    private static final int PAYMENT_UNPAID = 0;

    private static final int ORDER_PENDING_CONFIRM = 1;
    private static final int ORDER_SHIPPING        = 4;
    private static final int ORDER_COMPLETED       = 6;
    private static final int ORDER_CANCELED        = 7;

    private static final int SERI_ACTIVE  = 1;
    private static final int SERI_PENDING = 2;

    private static final String ORDER_TYPE_POS = "TAI_QUAY";

    @Value("${app.order.online.hold.enabled:true}")
    private boolean enabled;
    @Scheduled(fixedDelayString = "${app.order.online.hold.job-ms:300000}")
    @Transactional
    public void autoCancelExpiredOrders() {
        if (!enabled) return;
        Instant now = Instant.now();

        // ===== (A) Prepaid: quá hạn phút =====
        Instant cutoffPrepaid = now.minusSeconds(vnpayHoldMinutes * 60);

        List<Order> unpaidOnlineCandidates = orderRepository.findExpiredUnpaidOnlineOrders(
                cutoffPrepaid,
                PAYMENT_UNPAID,       // <-- thêm tham số này
                ORDER_SHIPPING,
                ORDER_CANCELED,
                ORDER_COMPLETED,
                ORDER_TYPE_POS
        );

        for (Order order : unpaidOnlineCandidates) {
            // Nếu là COD thì bỏ qua ở bước prepaid
            if (isCodOrder(order)) {
                continue;
            }
            cancelAndRelease(order, "AUTO_CANCEL_UNPAID_PREPAID_TIMEOUT");
        }

        // ===== (B) COD: quá hạn giờ (chỉ khi còn PENDING_CONFIRM) =====
        Instant cutoffCod = now.minusSeconds(codHoldHours * 3600);

        List<Order> codCandidates = orderRepository.findExpiredCodPendingConfirmOrders(
                cutoffCod,
                ORDER_TYPE_POS,
                ORDER_PENDING_CONFIRM
        );

        for (Order order : codCandidates) {
            if (!isCodOrder(order)) {
                continue; // phòng trường hợp order chưa có method hoặc method khác
            }
            cancelAndRelease(order, "AUTO_CANCEL_COD_PENDING_CONFIRM_TIMEOUT");
        }
    }

    private boolean isCodOrder(Order order) {
        List<String> methods = paymentDetailRepository.findTenHinhThucThanhToanByIdOrder(order.getId());
        if (methods == null || methods.isEmpty()) return false;

        for (String m : methods) {
            if (m == null) continue;
            if ("COD".equalsIgnoreCase(m.trim())) return true;
        }
        return false;
    }

    private void cancelAndRelease(Order order, String reason) {
        // idempotent: nếu ai đó vừa xử lý trước đó
        if (order.getTrangThai() != null &&
                (order.getTrangThai() >= ORDER_SHIPPING
                        || Objects.equals(order.getTrangThai(), ORDER_CANCELED)
                        || Objects.equals(order.getTrangThai(), ORDER_COMPLETED))) {
            return;
        }

        // 1) trả seri về ACTIVE (PENDING -> ACTIVE)
        List<OrderCT> items = orderCTRepository.findByIdOrder_Id(order.getId());
        for (OrderCT ct : items) {
            UUID seriId = (ct.getIdSeri() != null) ? ct.getIdSeri().getId() : null;
            if (seriId == null) continue;

            // atomic update, tránh race-condition
            seriRepository.updateTrangThaiSeriIfCurrent(seriId, SERI_PENDING, SERI_ACTIVE);
        }

        // 2) huỷ đơn
        order.setTrangThai(ORDER_CANCELED);
        order.setTrangThaiThanhToan(PAYMENT_UNPAID);

        // 3) ghi chú reason (nhẹ nhàng, không làm bẩn ghi chú nếu null)
        String note = "[" + reason.toUpperCase(Locale.ROOT) + "]";
        if (order.getGhiChu() == null || order.getGhiChu().isBlank()) {
            order.setGhiChu(note);
        } else if (!order.getGhiChu().contains(note)) {
            order.setGhiChu(order.getGhiChu() + " " + note);
        }

        orderRepository.save(order);
    }
}
