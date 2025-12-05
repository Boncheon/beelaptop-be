package com.example.sever.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class LaptopUpdateRequestDTO {
    @NotBlank
    @Size(max = 255)
    private String tenSanPham;

    @Size(max = 500)
    private String moTa;

    private UUID idThuongHieu;
    private UUID idManHinh;
    private UUID idKichThuoc;
    private UUID idHeDieuHanh;
    private UUID idPin;
}
