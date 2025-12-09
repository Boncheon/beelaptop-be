package com.example.sever.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "vnpay")   // map từ vnpay.* trong properties
@Data
public class VnPayConfig {

    /** Mã website do VNPay cấp (vnp_TmnCode) */
    private String tmnCode;

    /** Chuỗi bí mật dùng để ký HMAC (vnp_HashSecret) */
    private String secretKey;

    /** URL thanh toán VNPay (sandbox) */
    private String payUrl;

    /** URL VNPay redirect về BACKEND sau khi thanh toán  */
    private String returnUrl;

    /** Kiểu đơn hàng gửi cho VNPay: ví dụ "other" */
    private String orderType;

    /** Ngôn ngữ: "vn" hoặc "en" */
    private String locale;
}
