package com.example.sever.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "PhienBan_LapTopCT", schema = "dbo")
public class PhienbanLaptopct {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 20)
    @Column(name = "id_phien_ban_laptopct", length = 20)
    private String idPhienBanLaptopct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phien_ban")
    private PhienBan idPhienBan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_laptop_chi_tiet")
    private LaptopChiTiet idLaptopChiTiet;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Size(max = 500)
    @Nationalized
    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "ngay_tao")
    private Instant ngayTao;

    @Column(name = "ngay_cap_nhat")
    private Instant ngayCapNhat;

    @Size(max = 100)
    @Nationalized
    @Column(name = "nguoi_tao", length = 100)
    private String nguoiTao;

}