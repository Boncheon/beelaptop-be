package com.example.sever.dto.VnPayDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VnPayPaymentResponseCustomer {
    private String paymentUrl; // URL thanh toán VNPay
    private String vnpTxnRef; // Mã tham chiếu giao dịch
    private String message;
}



















