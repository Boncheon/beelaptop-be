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
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "GiamGiaHoaDon", schema = "dbo")
public class GiamGiaHoaDon {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Column(name = "so_tien_giam", precision = 18, scale = 2)
    private BigDecimal soTienGiam;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ten_phieu")
    private String tenPhieu;

}