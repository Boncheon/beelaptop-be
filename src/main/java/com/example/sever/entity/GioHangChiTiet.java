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

    @Column(name = "id_gio_hang_ct", length = 20, nullable = false, unique = true)
    private String idGiohangchitiet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_gioHang", nullable = false)
    private GioHang idGioHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_spct", nullable = false)
    private LaptopChiTiet idSpct;

    @Column(name = "is_selected")
    private Integer isSelected;

    @Column(name = "so_luong")
    private Integer soLuong;

    @PrePersist
    public void prePersist() {
        if (idGiohangchitiet == null || idGiohangchitiet.isBlank()) {
            // CT + 12 = 14 ký tự (<= 20 OK)
            idGiohangchitiet = "CT" + UUID.randomUUID().toString().replace("-", "")
                    .substring(0, 12).toUpperCase();
        }
        if (isSelected == null) isSelected = 1;
        if (soLuong == null) soLuong = 1;
    }

}