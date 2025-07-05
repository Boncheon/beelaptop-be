package com.example.sever.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "BaoHanh", schema = "dbo")
public class BaoHanh {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 20)
    @Column(name = "id_bao_hanh", length = 20)
    private String idBaoHanh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_seri")
    private Seri idSeri;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order_ct")
    private OrderCT idOrderCt;

    @Column(name = "ngay_het_bh")
    private LocalDate ngayHetBh;

    @Size(max = 500)
    @Nationalized
    @Column(name = "mo_ta_loi", length = 500)
    private String moTaLoi;

    @Column(name = "trang_thai")
    private Integer trangThai;

}