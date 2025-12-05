package com.example.sever.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO dùng để trả về dữ liệu chi tiết của laptop cho client
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaptopResponseDTO {

    private java.util.UUID id;
    private String idLaptop;
    private String tenSanPham;
    private String moTa;
    private String tenThuongHieu;
    private java.time.Instant ngayTao;
    private java.time.Instant ngaySua;
}