package com.example.sever.dto.Pos;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosOrderItemDTO {

    private UUID orderCtId;
    private UUID seriId;
    private String maSeri;

    private UUID laptopCtId;
    private UUID laptopId;

    private String tenSanPham;   // Laptop.tenSanPham
    private String cauHinh;      // Cpu / Ram / Ssd / Vga / Màu...

    private BigDecimal giaBan;   // giá bán 1 cái
}