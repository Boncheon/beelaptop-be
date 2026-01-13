package com.example.sever.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "HinhThucThanhToanChiTiet", schema = "dbo")
public class HinhThucThanhToanChiTiet {

    @Id
    @Column(name = "ID", nullable = false)
    private UUID id;

    // ✅ đây chính là id_thanh_toan_ct
    @Size(max = 20)
    @Column(name = "id_thanh_toan_ct", length = 20)
    private String idThanhToanCt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_order")
    private Order idOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_hinh_thuc_thanh_toan")
    private HinhThucThanhToan idHinhThucThanhToan;

    @Column(name = "so_tien", precision = 18, scale = 2)
    private BigDecimal soTienThanhToan;

    @Column(name = "khach_dua", precision = 18, scale = 2)
    private BigDecimal khachDua;

    @Column(name = "tien_tra_lai", precision = 18, scale = 2)
    private BigDecimal traLai;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    // ✅ auto gen ID + id_thanh_toan_ct để không bao giờ NULL
    @PrePersist
    public void prePersist() {
        if (this.id == null) this.id = UUID.randomUUID();

        if (this.idThanhToanCt == null || this.idThanhToanCt.isBlank()) {
            // <= 20 ký tự theo constraint @Size
            // Ví dụ: TT + 18 ký tự UUID -> 20
            this.idThanhToanCt = ("TT" + UUID.randomUUID().toString().replace("-", ""))
                    .substring(0, 20)
                    .toUpperCase();
        }
    }
}
