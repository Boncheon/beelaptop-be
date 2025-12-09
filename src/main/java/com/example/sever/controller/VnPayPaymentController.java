package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.Pos.PosAddPaymentRequest;
import com.example.sever.entity.HinhThucThanhToan;
import com.example.sever.repository.HinhThucThanhToanRepository;
import com.example.sever.service.MomoService;
import com.example.sever.service.PosOrderService;
import com.example.sever.service.VnPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/pos")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
public class VnPayPaymentController {

    private final VnPayService vnPayService;
    private final MomoService momoService;
    private final PosOrderService posOrderService;
    private final HinhThucThanhToanRepository hinhThucThanhToanRepository;

    public record PaymentInitResponse(String payUrl) {}

    // =========================================================
    // VNPay
    // =========================================================

    @PostMapping("/orders/{orderId}/pay/vnpay")
    @PreAuthorize("hasAnyRole('ADMIN','NHAN_VIEN')")
    public ResponseEntity<ApiResponse<PaymentInitResponse>> startVnpay(
            @PathVariable UUID orderId,
            HttpServletRequest request
    ) {
        var detail = posOrderService.getDetail(orderId);
        BigDecimal mustPay = detail.getTongTienThuHo();
        if (mustPay == null || mustPay.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Đơn hàng không có số tiền cần thanh toán.");
        }

        String clientIp = request.getRemoteAddr();
        String payUrl = vnPayService.createPaymentUrl(orderId, mustPay.longValue(), clientIp);

        ApiResponse<PaymentInitResponse> res = ApiResponse.<PaymentInitResponse>builder()
                .code(200)
                .message("Tạo link thanh toán VNPay thành công")
                .data(new PaymentInitResponse(payUrl))
                .build();

        return ResponseEntity.ok(res);
    }

    /**
     * URL VNPay redirect user sau khi thanh toán.
     * Không yêu cầu token, chỉ verify chữ ký & cập nhật đơn.
     * Sau đó redirect sang FE: /pos/payment-success hoặc /pos/payment-failed
     */
    @GetMapping("/payment/vnpay-return")
    public ResponseEntity<Void> handleVnpayReturn(HttpServletRequest request) {

        // Lấy secure hash từ VNPay
        String vnpSecureHash = request.getParameter("vnp_SecureHash");

        // Lọc chỉ những param bắt đầu bằng "vnp_" và LOẠI SecureHash, SecureHashType
        Map<String, String> vnpParams = new HashMap<>();
        request.getParameterMap().forEach((k, v) -> {
            if (v != null && v.length > 0 && k.startsWith("vnp_")) {
                if (!"vnp_SecureHash".equals(k) && !"vnp_SecureHashType".equals(k)) {
                    vnpParams.put(k, v[0]);
                }
            }
        });

        log.info("VNPay return params (for sign) : {}", vnpParams);

        // verify chữ ký
        boolean valid = vnPayService.validateResponse(vnpParams, vnpSecureHash);
        if (!valid) {
            log.warn("VNPay callback signature INVALID");
            return redirectToFE("/pos/payment-failed?reason=" + encodeReason("invalid-signature") + "&method=vnpay");
        }

        String responseCode = vnpParams.get("vnp_ResponseCode");        // 00 = success
        String txnStatus    = vnpParams.getOrDefault("vnp_TransactionStatus", "00");

        if (!"00".equals(responseCode) || !"00".equals(txnStatus)) {
            log.warn("VNPay payment failed. responseCode={}, txnStatus={}", responseCode, txnStatus);
            return redirectToFE("/pos/payment-failed?reason=" + encodeReason("payment-failed") + "&method=vnpay");
        }

        try {
            String orderInfo = vnpParams.get("vnp_OrderInfo"); // POS|{UUID}
            if (orderInfo == null || !orderInfo.startsWith("POS|")) {
                return redirectToFE("/pos/payment-failed?reason=" + encodeReason("order-info") + "&method=vnpay");
            }
            String orderIdStr = orderInfo.substring(4);
            UUID orderId = UUID.fromString(orderIdStr);

            String amountStr = vnpParams.get("vnp_Amount"); // amount * 100
            long amount = Long.parseLong(amountStr) / 100L;

            HinhThucThanhToan method = hinhThucThanhToanRepository
                    .findFirstByTenHinhThucContainingIgnoreCase("VNPay")
                    .orElseThrow(() -> new IllegalStateException(
                            "Chưa cấu hình hình thức thanh toán VNPay trong hệ thống"));

            PosAddPaymentRequest payReq = new PosAddPaymentRequest();
            payReq.setIdHinhThucThanhToan(method.getId());
            payReq.setKhachDua(BigDecimal.valueOf(amount));

            // ✅ Ghi nhận thanh toán + hoàn tất đơn
            posOrderService.addPayment(orderId, payReq);
            posOrderService.complete(orderId);

            return redirectToFE("/pos/payment-success?orderId=" + orderId + "&method=vnpay");

        } catch (Exception e) {
            log.error("VNPay return xử lý lỗi", e);
            return redirectToFE("/pos/payment-failed?reason=" + encodeReason("exception") + "&method=vnpay");
        }
    }

    // =========================================================
    // MoMo
    // =========================================================

    /**
     * FE gọi để tạo link thanh toán MoMo (payWithMethod).
     * Trong MomoService.createPayment nhớ set extraData = orderId (UUID dạng string)
     * để /momo-return đọc ra được.
     */
    @PostMapping("/orders/{orderId}/pay/momo")
    @PreAuthorize("hasAnyRole('ADMIN','NHAN_VIEN')")
    public ResponseEntity<ApiResponse<PaymentInitResponse>> startMomo(
            @PathVariable UUID orderId
    ) {
        var detail = posOrderService.getDetail(orderId);
        BigDecimal mustPay = detail.getTongTienThuHo();
        if (mustPay == null || mustPay.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Đơn hàng không có số tiền cần thanh toán.");
        }

        String payUrl = momoService.createPayment(orderId, mustPay.longValue());

        ApiResponse<PaymentInitResponse> res = ApiResponse.<PaymentInitResponse>builder()
                .code(200)
                .message("Tạo link thanh toán MoMo thành công")
                .data(new PaymentInitResponse(payUrl))
                .build();

        return ResponseEntity.ok(res);
    }

    /**
     * MoMo redirect user về URL này sau khi khách bấm thanh toán.
     * Ở DEV ipnUrl thường là localhost → có thể không nhận được IPN,
     * nên ta xử lý luôn ở đây:
     *  - nếu resultCode = 0 → addPayment + completeOrder.
     * Sau đó redirect sang FE.
     */
    @GetMapping("/payment/momo-return")
    public ResponseEntity<Void> handleMomoReturn(HttpServletRequest request) {
        String resultCode = request.getParameter("resultCode"); // "0" = thành công
        String message    = Optional.ofNullable(request.getParameter("message")).orElse("");
        String extraData  = request.getParameter("extraData");  // UUID order (set ở createPayment)
        String amountStr  = request.getParameter("amount");     // số tiền

        log.info("MoMo RETURN - resultCode={}, message={}, extraData={}, amount={}",
                resultCode, message, extraData, amountStr);

        boolean success = "0".equals(resultCode);
        if (success && extraData != null && !extraData.isBlank()) {
            try {
                UUID orderId = UUID.fromString(extraData.trim());
                long amount  = Long.parseLong(amountStr);

                HinhThucThanhToan method = hinhThucThanhToanRepository
                        .findFirstByTenHinhThucContainingIgnoreCase("MoMo")
                        .orElseThrow(() -> new IllegalStateException(
                                "Chưa cấu hình hình thức thanh toán MoMo trong hệ thống"));

                PosAddPaymentRequest payReq = new PosAddPaymentRequest();
                payReq.setIdHinhThucThanhToan(method.getId());
                payReq.setKhachDua(BigDecimal.valueOf(amount));

                posOrderService.addPayment(orderId, payReq);
                posOrderService.complete(orderId);

                log.info("MoMo RETURN: Đã ghi nhận thanh toán cho order {}", orderId);

                return redirectToFE("/pos/payment-success?orderId=" + orderId + "&method=momo");
            } catch (Exception e) {
                log.error("MoMo RETURN xử lý lỗi", e);
                return redirectToFE("/pos/payment-failed?reason=" + encodeReason("exception") + "&method=momo");
            }
        }

        // thất bại
        return redirectToFE("/pos/payment-failed?reason=" + encodeReason(message) + "&method=momo");
    }

    /**
     * IPN từ MoMo (server -> server).
     * Sau này deploy thật, chỉ cần trỏ ipnUrl về /payment/momo-ipn là xong.
     */
    @PostMapping("/payment/momo-ipn")
    public ResponseEntity<String> handleMomoIpn(@RequestBody Map<String, Object> body) {
        log.info("MoMo IPN: {}", body);

        try {
            String partnerCode = (String) body.get("partnerCode");
            String accessKey   = (String) body.get("accessKey");
            String orderIdStr  = (String) body.get("orderId");
            String orderInfo   = (String) body.get("orderInfo");
            String amountStr   = String.valueOf(body.get("amount"));
            String resultCodeStr = String.valueOf(body.get("resultCode"));
            String message     = (String) body.get("message");
            String requestId   = String.valueOf(body.get("requestId"));
            String orderType   = String.valueOf(body.get("orderType"));
            String transId     = String.valueOf(body.get("transId"));
            String payType     = String.valueOf(body.get("payType"));
            String extraData   = (String) body.getOrDefault("extraData", "");
            String signature   = (String) body.get("signature");
            String responseTime = String.valueOf(body.get("responseTime"));

            String rawHash = "accessKey=" + accessKey +
                    "&amount=" + amountStr +
                    "&extraData=" + extraData +
                    "&message=" + message +
                    "&orderId=" + orderIdStr +
                    "&orderInfo=" + orderInfo +
                    "&orderType=" + orderType +
                    "&partnerCode=" + partnerCode +
                    "&payType=" + payType +
                    "&requestId=" + requestId +
                    "&responseTime=" + responseTime +
                    "&resultCode=" + resultCodeStr +
                    "&transId=" + transId;

            boolean valid = momoService.verifySignature(signature, rawHash);
            if (!valid) {
                log.warn("MoMo IPN signature INVALID");
                return ResponseEntity.ok("{\"resultCode\": -1, \"message\": \"invalid signature\"}");
            }

            int resultCode = Integer.parseInt(resultCodeStr);
            if (resultCode == 0) {
                if (extraData == null || extraData.isBlank()) {
                    log.warn("MoMo IPN: extraData rỗng, không biết đơn nào");
                    return ResponseEntity.ok("{\"resultCode\": 0, \"message\": \"ok\"}");
                }

                UUID orderId = UUID.fromString(extraData.trim());
                long amount  = Long.parseLong(amountStr);

                HinhThucThanhToan method = hinhThucThanhToanRepository
                        .findFirstByTenHinhThucContainingIgnoreCase("MoMo")
                        .orElseThrow(() -> new IllegalStateException(
                                "Chưa cấu hình hình thức thanh toán MoMo trong hệ thống"));

                PosAddPaymentRequest payReq = new PosAddPaymentRequest();
                payReq.setIdHinhThucThanhToan(method.getId());
                payReq.setKhachDua(BigDecimal.valueOf(amount));

                posOrderService.addPayment(orderId, payReq);
                posOrderService.complete(orderId);

                log.info("MoMo IPN: Đã ghi nhận thanh toán cho order {}", orderId);
            } else {
                log.warn("MoMo IPN: Giao dịch thất bại. resultCode={}, message={}",
                        resultCode, message);
            }

            return ResponseEntity.ok("{\"resultCode\": 0, \"message\": \"ok\"}");
        } catch (Exception e) {
            log.error("MoMo IPN xử lý lỗi", e);
            return ResponseEntity.ok("{\"resultCode\": 0, \"message\": \"ok\"}");
        }
    }

    // =========================================================
    // COMMON
    // =========================================================

    private ResponseEntity<Void> redirectToFE(String path) {
        String feBase = "http://localhost:3000";   // FE đang chạy port 3000
        String url = feBase + path;

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.LOCATION, url);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    private String encodeReason(String reason) {
        try {
            return URLEncoder.encode(reason, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "unknown";
        }
    }
}
