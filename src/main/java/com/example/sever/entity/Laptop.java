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
@Table(name = "Laptop", schema = "dbo")
public class Laptop {
    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Size(max = 20)
    @Column(name = "id_laptop", length = 20)
    private String idLaptop;

    @ManyToOne
    @JoinColumn(name = "id_thuonghieu")
    private ThuongHieu idThuonghieu;

    @ManyToOne
    @JoinColumn(name = "id_man_hinh")
    private ManHinh idManHinh;

    @ManyToOne
    @JoinColumn(name = "id_pin")
    private Pin idPin;

    @ManyToOne
    @JoinColumn(name = "id_kich_thuoc")
    private KichThuoc idKichThuoc;

    @ManyToOne
    @JoinColumn(name = "id_dieu_hanh")
    private HeDieuHanh idHeDieuHanh;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ten_san_pham")
    private String tenSanPham;

    @Size(max = 500)
    @Nationalized
    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Column(name = "ngay_tao")
    private Instant ngayTao;

    @Column(name = "ngay_sua")
    private Instant ngaySua;


//    @OneToMany(mappedBy = "idLapTop")
//    private Set<LaptopChiTiet> laptopChiTiets = new LinkedHashSet<>();

}