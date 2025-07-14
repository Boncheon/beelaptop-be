package com.example.sever.dto.PhieuGiamGiaDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhieuGiamGiaDto {



    private String idPhieugiamgia;


    private String ten;


    private Integer soLuong;

    private String kieuGiamGia;


    private BigDecimal giaTriGiam;


    private LocalDate ngayBatDau;


    private LocalDate ngayKetThuc;


    private BigDecimal giaTriMin;


    private BigDecimal giaTriMax;


    private String moTa;


    private Integer trangThai;




}
