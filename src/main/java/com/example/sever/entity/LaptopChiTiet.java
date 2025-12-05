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

@Entity
@Table(name = "LaptopChiTiet", schema = "dbo")
@Getter
@Setter
public class LaptopChiTiet {

    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();
    @Column(name = "id_laptop_chi_tiet")
    private String idLaptopCT;
    // FK Laptop base
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lap_top")
    private Laptop idLaptop;

    // ============= CÁC THÀNH PHẦN CẤU HÌNH =============
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ram")
    private Ram idRam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ssd")
    private Rom idSsd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cpu")
    private Cpu idCpu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dohoa")
    private DoHoa idDohoa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mau_sac")
    private MauSac idMauSac;

    // ============= THÔNG TIN CHI TIẾT =============
    @Column(name = "gia_ban")
    private java.math.BigDecimal giaBan;

    @Nationalized
    @Size(max = 500)
    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Column(name = "ngay_tao")
    private Instant ngayTao;

    @Column(name = "ngay_cap_nhat")
    private Instant ngayCapNhat;

    @Nationalized
    @Size(max = 100)
    @Column(name = "nguoi_tao")
    private String nguoiTao;

    @Nationalized
    @Size(max = 255)
    @Column(name = "ghi_chu")
    private String ghiChu;
}
