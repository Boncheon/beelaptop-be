package com.example.sever.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeKhachHangSoDonResponseDTO {
    private UUID idTaiKhoan;
    private String tenKhachHang;
    private String sdtKhachHang;
    private Long soDonDaMua;
    private BigDecimal tongChiTieu;
}
