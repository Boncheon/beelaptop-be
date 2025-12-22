package com.example.sever.dto.Pos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomoCreatePaymentRequest {
    private String partnerCode;
    private String accessKey;
    private String requestId;
    private String amount;
    private String orderId;
    private String orderInfo;
    private String returnUrl;  // MoMo v2 dùng returnUrl / notifyUrl
    private String notifyUrl;
    private String extraData;
    private String requestType; // captureMoMoWallet
    private String signature;
    private String lang;
}