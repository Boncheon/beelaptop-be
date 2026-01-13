package com.example.sever.dto.VnPayDTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VnPayPaymentRequestCustomer {
    @JsonAlias({"orderId", "idOrder"})
    private UUID idOrder;
    // ID đơn hàng đã tạo
    private BigDecimal amount; // Số tiền thanh toán
    private String orderInfo; // Thông tin đơn hàng
    private String orderType; // Loại đơn hàng
    private String bankCode; // Mã ngân hàng (optional)
}



















