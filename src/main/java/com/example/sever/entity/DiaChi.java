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

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "DiaChi", schema = "dbo")
public class DiaChi {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 20)
    @Column(name = "id_dia_chi", length = 20)
    private String idDiaChi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tai_khoan")
    private TaiKhoan idTaiKhoan;

    @Size(max = 100)
    @Nationalized
    @Column(name = "quoc_gia", length = 100)
    private String quocGia;

    @Size(max = 100)
    @Nationalized
    @Column(name = "tinh_thanh", length = 100)
    private String tinhThanh;

    @Size(max = 100)
    @Nationalized
    @Column(name = "quan_huyen", length = 100)
    private String quanHuyen;

    @Size(max = 100)
    @Nationalized
    @Column(name = "phuong_xa", length = 100)
    private String phuongXa;

    @Size(max = 255)
    @Nationalized
    @Column(name = "dia_chi_chi_tiet")
    private String diaChiChiTiet;

    @OneToMany(mappedBy = "idDiaChi")
    private Set<Order> orders = new LinkedHashSet<>();

}