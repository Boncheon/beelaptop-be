package com.example.sever.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "Seri", schema = "dbo")
public class Seri {
    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lap_top_ct")
    private LaptopChiTiet idLapTopCt;

    @Size(max = 20)
    @Column(name = "id_seri", length = 20)
    private String idSeri;


//    @Column(name = "gia_goc", precision = 18, scale = 2)
//    private BigDecimal giaGoc;

//    @Size(max = 255)
//    @Nationalized
//    @Column(name = "nguon_seri")
//    private String nguonSeri;
//
    @Column(name = "trang_thai")
    private Integer trangThai;


}