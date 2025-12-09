package com.example.sever.dto.DotGiamGia;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DotGiamGiaDTO {

    private String idDotGiamGia;
    private String tenDotGiamGia;
    private String moTa;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private Integer trangThai;


}

