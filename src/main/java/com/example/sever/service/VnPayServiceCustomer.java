package com.example.sever.service;

import com.example.sever.dto.VnPayDTO.VnPayCallbackResponseCustomer;
import com.example.sever.dto.VnPayDTO.VnPayPaymentRequestCustomer;
import com.example.sever.dto.VnPayDTO.VnPayPaymentResponseCustomer;

import java.util.Map;

public interface VnPayServiceCustomer {

    VnPayPaymentResponseCustomer createPaymentUrl(VnPayPaymentRequestCustomer request);

    VnPayCallbackResponseCustomer handlePaymentCallback(Map<String, String> params);

    boolean verifySignature(Map<String, String> params, String vnpSecureHash);
}



















