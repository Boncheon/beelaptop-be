package com.example.sever.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "DotGiamGia_ChiTiet")

public class DotGiamGiaChiTiet {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private UUID id;

    @Column(name = "id_dot_giam_gia_ct")
    private String idDotGiamGiaCT;


    @ManyToOne
    @JoinColumn(name = "id_dot_giam_gia")
    private DotGiamGia dotGiamGia;

    @Column(name = "kieu_giam_gia")
    private String kieuGiamGia;

    @Column(name = "gia_tri_giam")
    private BigDecimal giaTriGiam;



}
