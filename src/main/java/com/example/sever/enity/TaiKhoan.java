package com.example.sever.enity;

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

    @ColumnDefault("newid()")
    @Column(name = "ID_TaiKhoan")
    private UUID idTaikhoan;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ten_dang_nhap")
    private String tenDangNhap;

    @Size(max = 255)
    @Nationalized
    @Column(name = "mat_khau")
    private String matKhau;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ten")
    private String ten;

    @Size(max = 20)
    @Nationalized
    @Column(name = "sdt", length = 20)
    private String sdt;

    @Size(max = 100)
    @Nationalized
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 500)
    @Nationalized
    @Column(name = "dia_chi", length = 500)
    private String diaChi;

    @Size(max = 500)
    @Nationalized
    @Column(name = "refresh_token", length = 500)
    private String refreshToken;

    @Size(max = 500)
    @Nationalized
    @Column(name = "anh", length = 500)
    private String anh;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_role")
    private Role idRole;

}