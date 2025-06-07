package com.example.sever.enity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "HoaDon", schema = "dbo")
public class HoaDon {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @ColumnDefault("newid()")
    @Column(name = "ID_HoaDon")
    private UUID idHoadon;

    @Column(name = "ngay_tao")
    private Instant ngayTao;

    @Column(name = "tong_tien", precision = 18, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "tong_tien_sau_giam", precision = 18, scale = 2)
    private BigDecimal tongTienSauGiam;

    @Size(max = 50)
    @Nationalized
    @Column(name = "loai_hoa_don", length = 50)
    private String loaiHoaDon;

    @Column(name = "phu_phi", precision = 18, scale = 2)
    private BigDecimal phuPhi;

    @Size(max = 50)
    @Nationalized
    @Column(name = "trang_thai", length = 50)
    private String trangThai;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ten_nguoi_nhan")
    private String tenNguoiNhan;

    @Size(max = 20)
    @Nationalized
    @Column(name = "sdt_nguoi_nhan", length = 20)
    private String sdtNguoiNhan;

    @Size(max = 500)
    @Nationalized
    @Column(name = "dia_chi_nguoi_nhan", length = 500)
    private String diaChiNguoiNhan;

}