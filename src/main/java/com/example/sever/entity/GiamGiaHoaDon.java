package com.example.sever.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "GiamGiaHoaDon", schema = "dbo")
public class GiamGiaHoaDon {
    @Id
    @GeneratedValue
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false, columnDefinition = "uniqueidentifier default newid()")
    private UUID id;

    @Size(max = 20)
    @Column(name = "ID_GiamGiaHoaDon", length = 20)
    private String idGiamgiahoadon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_orders")
    private Order idOrders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_phieu_giam_gia")
    private PhieuGiamGia idPhieuGiamGia;

    @Column(name = "so_tien_giam", precision = 18, scale = 2)
    private BigDecimal soTienGiam;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ten_phieu")
    private String tenPhieu;









}