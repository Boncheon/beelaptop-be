package com.example.sever.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "GioHang", schema = "dbo")
public class GioHang {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 20)
    @Column(name = "id_gio_hang", length = 20)
    private String idGioHang;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tai_khoan")
    private TaiKhoan idTaiKhoan;

    @ColumnDefault("getdate()")
    @Column(name = "Ngay_tao", columnDefinition = "DATETIME")
    private LocalDateTime ngayTao;

    @OneToMany(mappedBy = "idGioHang")
    private Set<GioHangChiTiet> gioHangChiTiets = new LinkedHashSet<>();

}