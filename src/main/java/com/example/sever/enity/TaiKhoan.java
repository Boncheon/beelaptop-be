package com.example.sever.enity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "TaiKhoan", schema = "dbo")
public class TaiKhoan {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 20)
    @Column(name = "id_tai_khoan", length = 20)
    private String idTaiKhoan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_role")
    private Role idRole;

    @Size(max = 100)
    @Nationalized
    @Column(name = "Ten", length = 100)
    private String ten;

    @Size(max = 20)
    @Nationalized
    @Column(name = "So_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "Ngay_sinh")
    private LocalDate ngaySinh;

    @Size(max = 100)
    @Nationalized
    @Column(name = "Email", length = 100)
    private String email;

    @Size(max = 10)
    @Nationalized
    @Column(name = "Gioi_tinh", length = 10)
    private String gioiTinh;

    @Size(max = 255)
    @Nationalized
    @Column(name = "Mat_khau")
    private String matKhau;

    @Size(max = 500)
    @Nationalized
    @Column(name = "Anh", length = 500)
    private String anh;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "idTaiKhoan")
    private Set<DiaChi> diaChis = new LinkedHashSet<>();

    @OneToOne(mappedBy = "idTaiKhoan")
    private GioHang gioHang;

    @OneToMany(mappedBy = "idTaiKhoan")
    private Set<OrderActionLog> orderActionLogs = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idTaiKhoan")
    private Set<Order> orders = new LinkedHashSet<>();

}