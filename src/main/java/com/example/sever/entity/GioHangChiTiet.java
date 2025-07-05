package com.example.sever.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "GioHangChiTiet", schema = "dbo")
public class GioHangChiTiet {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 20)
    @Column(name = "ID_GioHangChiTiet", length = 20)
    private String idGiohangchitiet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_gio_hang")
    private GioHang idGioHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_seri")
    private Seri idSeri;

    @Column(name = "is_selected")
    private Integer isSelected;

    @Column(name = "so_luong")
    private Integer soLuong;

}