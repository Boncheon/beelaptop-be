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
public class OrderCTCustomerRequest {
    private UUID idLaptopChiTiet;
    private BigDecimal giaBan;
    private Integer soLuong;
}

