package com.example.sever.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
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

    @Size(max = 50)
    @Nationalized
    @Column(name = "kieu_giam_gia", length = 50)
    private String kieuGiamGia;

    @Column(name = "gia_tri_giam", precision = 18, scale = 2)
    private BigDecimal giaTriGiam;

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
    private Integer trangThai;

    @OneToMany(mappedBy = "idPhieuGiamGia")
    private Set<GiamGiaHoaDon> giamGiaHoaDons = new LinkedHashSet<>();

}