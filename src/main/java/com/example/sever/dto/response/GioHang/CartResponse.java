package com.example.sever.dto.response.GioHang;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
@Data
public class CartResponse {
    private UUID idGioHang;
    private String maGioHang;
    private Instant ngayTao;
    private List<ProductInCart> sanPhams;
    private Integer tongSoLuong;
}
