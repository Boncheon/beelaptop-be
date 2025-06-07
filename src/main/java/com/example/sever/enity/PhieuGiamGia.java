package com.example.sever.enity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "PhieuGiamGia", schema = "dbo")
public class PhieuGiamGia {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "Ma", nullable = false)
    private UUID id;

    @Size(max = 255)
    @Nationalized
    @Column(name = "Ten")
    private String ten;

    @Column(name = "So_luong")
    private Integer soLuong;

    @Size(max = 50)
    @Nationalized
    @Column(name = "kieu_giam_gia", length = 50)
    private String kieuGiamGia;

    @Column(name = "gia_tri", precision = 18, scale = 2)
    private BigDecimal giaTri;

    @Column(name = "ngay_bat_dau")
    private Instant ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private Instant ngayKetThuc;

    @Column(name = "gia_tri_min", precision = 18, scale = 2)
    private BigDecimal giaTriMin;

    @Column(name = "gia_tri_max", precision = 18, scale = 2)
    private BigDecimal giaTriMax;

    @Size(max = 500)
    @Nationalized
    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "trang_thai")
    private Boolean trangThai;

}