package com.example.sever.enity;

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

import java.math.BigDecimal;
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
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ten_san_pham")
    private String tenSanPham;

    @Column(name = "gia_nhap", precision = 18, scale = 2)
    private BigDecimal giaNhap;

    @Column(name = "ngay_tao")
    private Instant ngayTao;

    @Column(name = "ngay_sua")
    private Instant ngaySua;

    @Size(max = 500)
    @Nationalized
    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Size(max = 50)
    @Column(name = "trang_thai", length = 50)
    private String trangThai;

    @ManyToOne
    @JoinColumn(name = "ID_danh_muc")
    private DanhMuc idDanhMuc;

    @ManyToOne
    @JoinColumn(name = "ID_thuong_hieu")
    private ThuongHieu idThuongHieu;

    @OneToMany(mappedBy = "idLaptop")
    private Set<LaptopChiTiet> laptopChiTiets = new LinkedHashSet<>();

}