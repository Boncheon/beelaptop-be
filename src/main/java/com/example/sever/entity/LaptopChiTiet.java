package com.example.sever.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "LaptopChiTiet", schema = "dbo")
public class LaptopChiTiet {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 20)
    @Column(name = "id_laptop_chi_tiet", length = 20)
    private String idLaptopChiTiet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lap_top")
    private Laptop idLapTop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_man_hinh")
    private ManHinh idManHinh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pin")
    private Pin idPin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kich_thuoc")
    private KichThuoc idKichThuoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_he_dieu_hanh")
    private HeDieuHanh idHeDieuHanh;

    @Size(max = 500)
    @Nationalized
    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "ngay_tao")
    private Instant ngayTao;

    @Column(name = "ngay_cap_nhat")
    private Instant ngayCapNhat;

    @Size(max = 100)
    @Nationalized
    @Column(name = "nguoi_tao", length = 100)
    private String nguoiTao;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "idLaptopChiTiet")
    private Set<Anh> anhs = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idLaptopChiTiet")
    private Set<PhienbanLaptopct> phienbanLaptopcts = new LinkedHashSet<>();

}