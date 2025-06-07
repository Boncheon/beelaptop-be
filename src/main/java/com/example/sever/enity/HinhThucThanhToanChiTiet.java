package com.example.sever.enity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(name = "so_tien_thanh_toan", precision = 18, scale = 2)
    private BigDecimal soTienThanhToan;

}