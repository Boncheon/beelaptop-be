package com.example.sever.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "TaiKhoan", schema = "dbo")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaiKhoan extends AbstractEntity implements UserDetails {

    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    UUID id;

    @Size(max = 20)
    @Column(name = "id_tai_khoan", length = 20)
    String idTaiKhoan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_role")
    Role idRole;

    @Size(max = 100)
    @Nationalized
    @Column(name = "Ten_dang_nhap", unique = true)
    String tenDangNhap;

    @Size(max = 100)
    @Nationalized
    @Column(name = "Ten", unique = true)
    String ten;

    @Size(max = 20)
    @Nationalized
    @Column(name = "So_dien_thoai", length = 20)
    String soDienThoai;

    @Column(name = "Ngay_sinh")
    LocalDate ngaySinh;

    @Size(max = 100)
    @Nationalized
    @Column(name = "Email", length = 100, unique = true)
    String email;

    @Size(max = 10)
    @Nationalized
    @Column(name = "Gioi_tinh", length = 10)
    String gioiTinh;

    @JsonIgnore
    @Size(max = 255)
    @Nationalized
    @Column(name = "Mat_khau")
    String matKhau;

    @Size(max = 500)
    @Nationalized
    @Column(name = "Anh", length = 500)
    String anh;

    @Column(name = "trang_thai")
    Integer trangThai;

    @JsonIgnore
    @Column(name = "Refresh_token", length = 500)
    String refreshToken;

    @JsonIgnore
    @Column(name = "reset_token")
    String resetToken;

    @Column(name = "reset_token_expiry")
    Date resetTokenExpiry;


    @OneToMany(mappedBy = "idTaiKhoan")
    Set<DiaChi> diaChis = new LinkedHashSet<>();

    @OneToOne(mappedBy = "idTaiKhoan")
    GioHang gioHang;

    @OneToMany(mappedBy = "idTaiKhoan")
    Set<OrderActionLog> orderActionLogs = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idTaiKhoan")
    Set<Order> orders = new LinkedHashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + idRole.getTenChucVu()));
    }



    @Override
    public String getUsername() {
        return tenDangNhap;
    }

    @Override
    public String getPassword() {
        return matKhau;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return trangThai != null && trangThai == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return trangThai != null && trangThai == 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaiKhoan taiKhoan = (TaiKhoan) o;
        return Objects.equals(id, taiKhoan.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}