package com.example.sever.dto.DotGiamGiaDTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class DotGiamGiaChiTietResponse {


    private String idDotGiamGia;
    private String tenDot;
    private String moTa;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private int trangThai;

    private String kieuGiamGia;

    private BigDecimal giaTriGiam;


}
