package com.example.sever.dto.OrderDTO;

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
public class PaymentCustomerRequest {
    private UUID idHinhThucThanhToan;
    private BigDecimal soTien;
    private String ghiChu;
}

























