package com.example.sever.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamFullCreateDTO {
    private String nguoiTao;

    private LaptopAddRequestDTO laptop;
    private LapTopCTAddRequestDTO laptopChiTiet;
    private List<PhienBanAddRequestDTO> combinations;
}
