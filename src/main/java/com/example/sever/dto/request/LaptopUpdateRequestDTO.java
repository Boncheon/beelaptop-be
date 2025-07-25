package com.example.sever.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class LaptopUpdateRequestDTO {
    private String tenSanPham;
//    private UUID idDanhMuc;
//    private UUID idThuongHieu;
    private String moTa;
    private String nguoiTao;
    private Integer trangThai;
}
