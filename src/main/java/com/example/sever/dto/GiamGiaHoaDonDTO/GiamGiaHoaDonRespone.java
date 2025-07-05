package com.example.sever.dto.GiamGiaHoaDonDTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class GiamGiaHoaDonRespone {

    private String idGiamgiahoadon;

    private String tenPhieu;

    private String idPhieugiamgia;

    private BigDecimal giaTriGiam;

    private String moTa;

    private Integer soLuong;

    private LocalDate ngayBatDau;

    private LocalDate ngayKetThuc;



}
