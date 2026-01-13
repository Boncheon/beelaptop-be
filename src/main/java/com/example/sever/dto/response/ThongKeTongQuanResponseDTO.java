package com.example.sever.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ThongKeTongQuanResponseDTO {
    private BigDecimal tongDoanhThu;
    private Long tongDonHang;
    private BigDecimal tangTruong; // phần trăm
}