package com.example.sever.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopLaptopBanChayResponseDTO {
    private UUID idLaptop;
    private String tenSanPham;
    private Long soLuongBan;
}

