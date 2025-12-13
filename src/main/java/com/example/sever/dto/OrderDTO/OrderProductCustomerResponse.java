package com.example.sever.dto.OrderDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductCustomerResponse {
    private UUID idOrderCT;
    private UUID idSeri;
    private UUID idLaptopChiTiet;
    private String tenSanPham;
    private String anhSanPham;
    private BigDecimal giaBan;
    private Integer soLuong; // Luôn = 1 (1 seri = 1 sản phẩm)
    private BigDecimal thanhTien; // Thành tiền = giaBan (không nhân số lượng)
}








