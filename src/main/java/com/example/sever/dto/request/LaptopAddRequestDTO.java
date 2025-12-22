package com.example.sever.dto.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class LaptopAddRequestDTO {
    @NotBlank
    @Size(max = 255)
    private String tenSanPham;        // ten_san_pham

    @Size(max = 500)
    private String moTa;              // mo_ta

    // FK (bắt buộc)
    @NotNull
    private UUID idThuongHieu;   // id_thuonghieu
    @NotNull
    private UUID idManHinh;      // id_man_hinh
    @NotNull
    private UUID idKichThuoc;    // id_kich_thuoc
    @NotNull
    private UUID idHeDieuHanh;   // id_he_dieu_hanh
    // FK (tuỳ chọn)
    private UUID idPin;

    private Integer trangThai;

}
