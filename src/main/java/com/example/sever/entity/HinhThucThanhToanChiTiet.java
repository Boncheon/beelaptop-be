package com.example.sever.entity;


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

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "HinhThucThanhToanChiTiet", schema = "dbo")
public class HinhThucThanhToanChiTiet {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 20)
    @Column(name = "id_thanh_toan_ct", length = 20)
    private String idHinhthucthanhtoanchitiet;

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

    @Column(name = "ghi_chu", precision = 18, scale = 2)
    private String ghiChu;


}