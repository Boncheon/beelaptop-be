package com.example.sever.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopLaptopBanChayResponseDTO {
    private UUID idLaptop;
    private String tenSanPham;
    private String hinhAnh;  // URL hình ảnh sản phẩm
    private BigDecimal tongTienThuHo;
    private Long soLuongBan;
}
