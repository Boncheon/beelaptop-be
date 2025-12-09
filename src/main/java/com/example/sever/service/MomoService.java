package com.example.sever.service;

import com.example.sever.config.MomoConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MomoService {

    private final MomoConfig momoConfig;

    /**
     * Tạo giao dịch MoMo với requestType = payWithMethod
     * → màn hình chọn: Ví MoMo / ATM / VISA / Master…
     */
    public String createPayment(UUID orderId, long amount) {
        try {
            String partnerCode = momoConfig.getPartnerCode();
            String accessKey   = momoConfig.getAccessKey();
            String secretKey   = momoConfig.getSecretKey();

            // Mã giao dịch gửi sang MoMo phải unique
            String requestId   = partnerCode + System.currentTimeMillis();
            String momoOrderId = requestId; // giống sample payWithMethod

            // Thông tin hiển thị trên cổng MoMo
            String orderInfo   = "Thanh toán POS order " + orderId;
            String redirectUrl = momoConfig.getReturnUrl();
            String ipnUrl      = momoConfig.getNotifyUrl();
            String requestType = "payWithMethod";
            String lang        = "vi";

            // Lưu UUID đơn hàng thật vào extraData để callback dùng
            String extraData   = orderId.toString();

            // ---- RAW SIGNATURE (y hệt tài liệu MoMo – payWithMethod) ----
            String rawHash = "accessKey=" + accessKey +
                    "&amount=" + amount +
                    "&extraData=" + extraData +
                    "&ipnUrl=" + ipnUrl +
                    "&orderId=" + momoOrderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + partnerCode +
                    "&redirectUrl=" + redirectUrl +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;

            String signature = hmacSHA256(secretKey, rawHash);

            // ---- BODY gửi sang MoMo ----
            Map<String, Object> body = new HashMap<>();
            body.put("partnerCode", partnerCode);
            body.put("partnerName", "BeeLaptop POS");
            body.put("storeId", "BeeLaptopStore");
            body.put("requestId", requestId);
            body.put("amount", String.valueOf(amount));
            body.put("orderId", momoOrderId);
            body.put("orderInfo", orderInfo);
            body.put("redirectUrl", redirectUrl);
            body.put("ipnUrl", ipnUrl);
            body.put("lang", lang);
            body.put("requestType", requestType);
            body.put("autoCapture", true);
            body.put("extraData", extraData);
            body.put("orderGroupId", "");
            body.put("signature", signature);

            log.info("MoMo createPayment (payWithMethod) - rawHash={}", rawHash);
            log.info("MoMo createPayment (payWithMethod) - body={}", body);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> responseEntity =
                    restTemplate.postForEntity(momoConfig.getEndpoint(), httpEntity, Map.class);

            Map<String, Object> resBody = responseEntity.getBody();
            log.info("MoMo createPayment (payWithMethod) - response={}", resBody);

            if (resBody == null) {
                throw new IllegalStateException("Không nhận được response từ MoMo");
            }

            Object resultCodeObj = resBody.get("resultCode");
            if (!(resultCodeObj instanceof Number) ||
                    ((Number) resultCodeObj).intValue() != 0) {
                String msg = String.valueOf(resBody.get("message"));
                throw new IllegalStateException("MoMo tạo giao dịch thất bại: " + msg);
            }

            String payUrl = (String) resBody.get("payUrl");
            if (payUrl == null || payUrl.isBlank()) {
                payUrl = (String) resBody.get("deeplink");
            }
            if (payUrl == null || payUrl.isBlank()) {
                throw new IllegalStateException("Không tìm thấy payUrl trong response MoMo");
            }

            return payUrl;
        } catch (Exception e) {
            log.error("MoMo createPayment error", e);
            throw new RuntimeException("Không thể tạo giao dịch MoMo: " + e.getMessage(), e);
        }
    }

    // ==== verify cho IPN/return ====

    public boolean verifySignature(String signature, String rawHash) {
        if (signature == null || signature.isEmpty()) return false;
        String local = hmacSHA256(momoConfig.getSecretKey(), rawHash);
        return local.equals(signature);
    }

    private String hmacSHA256(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey =
                    new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(secretKey);
            byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Could not sign MoMo data", e);
        }
    }
}
