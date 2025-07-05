package com.example.sever.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "Anh", schema = "dbo")
public class Anh {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 20)
    @Column(name = "id_anh", length = 20)
    private String idAnh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_laptop_chi_tiet")
    private LaptopChiTiet idLaptopChiTiet;

    @Size(max = 500)
    @Nationalized
    @Column(name = "ImgURL", length = 500)
    private String imgURL;

}