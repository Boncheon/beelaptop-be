package com.example.sever.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "momo")
@Data
public class MomoConfig {

    /** partnerCode trong tài liệu MoMo */
    private String partnerCode;

    /** accessKey trong tài liệu MoMo */
    private String accessKey;

    /** secretKey để ký HMAC-SHA256 */
    private String secretKey;

    /** Endpoint tạo giao dịch (https://test-payment.momo.vn/v2/gateway/api/create) */
    private String endpoint;

    /** URL MoMo redirect user về sau khi thanh toán (frontend → BE) */
    private String returnUrl;

    /** URL MoMo gọi IPN server-to-server */
    private String notifyUrl;
}
