package com.example.sever.dto.VnPayDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VnPayCallbackResponseCustomer {
    private String vnpTxnRef; // Mã tham chiếu giao dịch
    private String vnpResponseCode; // Mã phản hồi
    private String vnpTransactionStatus; // Trạng thái giao dịch
    private String vnpAmount; // Số tiền
    private String vnpBankCode; // Mã ngân hàng
    private String vnpTransactionNo; // Mã giao dịch tại VNPay
    private String message;
    private boolean success;
}



















