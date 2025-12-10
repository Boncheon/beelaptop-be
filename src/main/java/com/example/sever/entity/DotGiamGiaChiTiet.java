package com.example.sever.entity;

import com.example.sever.KieuGiamGia;
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

    // Mã hiển thị — không phải ID database
    @Column(name = "ma_dot_giam_gia_ct", nullable = false, unique = true, length = 50)
    private String idDotGiamGiaCT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dot_giam_gia", nullable = false)
    private DotGiamGia dotGiamGia;

    @Enumerated(EnumType.STRING)
    @Column(name = "kieu_giam_gia", nullable = false)
    private KieuGiamGia kieuGiamGia;

    @Column(name = "gia_tri_giam", nullable = false)
    private BigDecimal giaTriGiam;

}
