package com.example.sever.entity;


import jakarta.persistence.*;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "GioHangCT", schema = "dbo")
public class GioHangChiTiet {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 20)
    @Column(name = "id_gio_hang_ct", length = 20)
    private String idGiohangchitiet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_gioHang")
    private GioHang idGioHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_spct")
    private LaptopChiTiet idSpct;

    @Column(name = "is_selected")
    private Integer isSelected;

    @Column(name = "so_luong")
    private Integer soLuong;

}