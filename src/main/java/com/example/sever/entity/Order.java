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

    import java.math.BigDecimal;
    import java.time.LocalDateTime;
    import java.util.LinkedHashSet;
    import java.util.Set;
    import java.util.UUID;

    @Getter
    @Setter
    @Entity
    @Table(name = "Order", schema = "dbo")
    public class Order {
        @Id
        @ColumnDefault("newid()")
        @Column(name = "ID", nullable = false)
        private UUID id;

        @Size(max = 20)
        @Column(name = "id_order", length = 20)
        private String idOrder;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "id_tai_khoan")
        private TaiKhoan idTaiKhoan;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "id_dia_chi")
        private DiaChi idDiaChi;

        @Size(max = 50)
        @Nationalized
        @Column(name = "ma_don_hang", length = 50)
        private String maDonHang;

        @Size(max = 100)
        @Nationalized
        @Column(name = "ten_khach_hang", length = 100)
        private String tenKhachHang;

        @Size(max = 20)
        @Nationalized
        @Column(name = "sdt_khach_hang", length = 20)
        private String sdtKhachHang;

        @Size(max = 50)
        @Nationalized
        @Column(name = "loai_don", length = 50)
        private String loaiDon;

        @Column(name = "tong_tien_thu_ho", precision = 18, scale = 2)
        private BigDecimal tongTienThuHo;

        @Column(name = "phi_van_chuyen", precision = 18, scale = 2)
        private BigDecimal phiVanChuyen;

        @Column(name = "phi_dich_vu_khac", precision = 18, scale = 2)
        private BigDecimal phiDichVuKhac;

        @Column(name = "gia_tri_chua_giam", precision = 18, scale = 2)
        private BigDecimal giaTriChuaGiam;

        @Column(name = "gia_tri_giam_gia", precision = 18, scale = 2)
        private BigDecimal giaTriGiamGia;

        @Column(name = "trang_thai")
        private Integer trangThai;

        @Size(max = 500)
        @Nationalized
        @Column(name = "ghi_chu", length = 500)
        private String ghiChu;

        @Column(name = "ngay_tao")
        private LocalDateTime ngayTao;

        @OneToMany(mappedBy = "idOrder")
        private Set<GiamGiaHoaDon> giamGiaHoaDons = new LinkedHashSet<>();

        @OneToMany(mappedBy = "idOrder")
        private Set<HinhThucThanhToanChiTiet> hinhThucThanhToanChiTiets = new LinkedHashSet<>();

        @OneToMany(mappedBy = "idOrder")
        private Set<OrderActionLog> orderActionLogs = new LinkedHashSet<>();

        @OneToMany(mappedBy = "idOrder")
        private Set<OrderCT> orderCTS = new LinkedHashSet<>();

    }