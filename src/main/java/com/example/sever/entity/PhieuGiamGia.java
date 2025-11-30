package com.example.sever.entity;

import com.example.sever.KieuGiamGia;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PhieuGiamGia", schema = "dbo")
public class PhieuGiamGia {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 20)
    @Column(name = "ID_PhieuGiamGia", length = 20)
    private String idPhieugiamgia;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ten")
    private String ten;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Nationalized
    @Column(name = "kieu_giam_gia", length = 50)
    @Enumerated(EnumType.STRING)
    private KieuGiamGia kieuGiamGia;

    @Column(name = "gia_tri_giam", precision = 18, scale = 2)
    private BigDecimal giaTriGiam;

    @Column(name = "ngay_bat_dau")
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @Column(name = "gia_tri_min", precision = 18, scale = 2)
    private BigDecimal giaTriMin;

    @Column(name = "gia_tri_max", precision = 18, scale = 2)
    private BigDecimal giaTriMax;

    @Size(max = 500)
    @Nationalized
    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "idPhieuGiamGia")
    private Set<GiamGiaHoaDon> giamGiaHoaDons = new LinkedHashSet<>();

    public PhieuGiamGia(String idPhieugiamgia, String ten, Integer soLuong, String kieuGiamGia, BigDecimal giaTriGiam, LocalDate ngayBatDau, LocalDate ngayKetThuc, BigDecimal giaTriMin, BigDecimal giaTriMax, String moTa, Integer trangThai) {
        this.idPhieugiamgia = idPhieugiamgia;
        this.ten = ten;
        this.soLuong = soLuong;
        this.kieuGiamGia = KieuGiamGia.valueOf(kieuGiamGia);
        this.giaTriGiam = giaTriGiam;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.giaTriMin = giaTriMin;
        this.giaTriMax = giaTriMax;
        this.moTa = moTa;
        this.trangThai = trangThai;
    }



}