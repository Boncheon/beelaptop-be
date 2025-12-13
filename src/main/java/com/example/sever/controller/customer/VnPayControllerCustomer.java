package com.example.sever.controller.customer;

import com.example.sever.dto.VnPayDTO.VnPayCallbackResponseCustomer;
import com.example.sever.dto.VnPayDTO.VnPayPaymentRequestCustomer;
import com.example.sever.dto.VnPayDTO.VnPayPaymentResponseCustomer;
import com.example.sever.entity.Order;
import com.example.sever.repository.OrderRepository;
import com.example.sever.service.VnPayServiceCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/laptops/vnpay")
@CrossOrigin(origins = "*")
public class VnPayControllerCustomer {

    @Autowired
    private VnPayServiceCustomer vnPayServiceCustomer;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Tạo URL thanh toán VNPay
     * POST /api/v1/laptops/vnpay/create-payment
     */
    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(@RequestBody VnPayPaymentRequestCustomer request) {
        try {
            VnPayPaymentResponseCustomer response = vnPayServiceCustomer.createPaymentUrl(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    /**
     * Callback từ VNPay sau khi thanh toán
     * GET /api/v1/laptops/vnpay/payment-callback
     */
    @GetMapping("/payment-callback")
    public ResponseEntity<?> paymentCallback(@RequestParam Map<String, String> params) {
        try {
            VnPayCallbackResponseCustomer response = vnPayServiceCustomer.handlePaymentCallback(params);

            if (response.isSuccess()) {
                // Redirect đến trang thành công
                return ResponseEntity.status(302)
                        .header("Location", "http://localhost:3000/client/payment/payment-success?status=success&message=" +
                                java.net.URLEncoder.encode(response.getMessage(), java.nio.charset.StandardCharsets.UTF_8))
                        .build();
            } else {
                // Redirect đến trang thất bại
                return ResponseEntity.status(302)
                        .header("Location", "http://localhost:3000/client/payment/payment-success?status=failed&message=" +
                                java.net.URLEncoder.encode(response.getMessage(), java.nio.charset.StandardCharsets.UTF_8))
                        .build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(302)
                    .header("Location", "http://localhost:3000/client/payment/payment-success?status=error&message=" +
                            java.net.URLEncoder.encode("Lỗi hệ thống: " + e.getMessage(), java.nio.charset.StandardCharsets.UTF_8))
                    .build();
        }
    }

    /**
     * API để kiểm tra kết quả thanh toán (dùng cho frontend polling)
     * GET /api/v1/laptops/vnpay/check-payment-status
     */
    @GetMapping("/check-payment-status")
    public ResponseEntity<?> checkPaymentStatus(@RequestParam String vnpTxnRef) {
        try {
            // Parse orderId từ vnpTxnRef
            String orderIdStr = vnpTxnRef.substring(3);
            String uuidStr = orderIdStr.substring(0, 8) + "-" +
                           orderIdStr.substring(8, 12) + "-" +
                           orderIdStr.substring(12, 16) + "-" +
                           orderIdStr.substring(16, 20) + "-" +
                           orderIdStr.substring(20);
            UUID orderId = UUID.fromString(uuidStr);

            // Tìm đơn hàng và trả về trạng thái
            Order order = orderRepository.findById(orderId)
                    .orElse(null);

            if (order == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("orderId", order.getId());
            result.put("status", order.getTrangThai());
            result.put("message", order.getTrangThai() != null && order.getTrangThai() == 2 ? "Đã thanh toán" : "Chưa thanh toán");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi hệ thống: " + e.getMessage());
        }
    }
}

