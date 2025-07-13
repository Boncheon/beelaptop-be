package com.example.sever.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "DotGiamGia")

public class DotGiamGia {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private UUID id;

    @Column(name = "id_dot_giam_gia")
    private String idDotGiamGia;

    @Column(name = "ten_dot")
    private String tenDotGiamGia;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "ngay_bat_dau")
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @Column(name = "trang_thai")
    private Integer trangThai;

    public DotGiamGia(String idDotGiamGia, String tenDotGiamGia, String moTa, LocalDate ngayBatDau, LocalDate ngayKetThuc, Integer trangThai) {
        this.idDotGiamGia = idDotGiamGia;
        this.tenDotGiamGia = tenDotGiamGia;
        this.moTa = moTa;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
    }



}
