package com.example.sever.dto.response.GioHang;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;
@Data
@Builder
public class ProductInCart {
    private UUID idChiTiet;
    private String maChiTiet;
    private UUID idSpct;
    private UUID idSeri;
    private Integer soLuong;
    private Integer isSelected;
    private ProductCartResponse productInfo;
}
