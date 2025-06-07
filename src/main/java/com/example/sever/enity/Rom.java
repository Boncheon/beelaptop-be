package com.example.sever.enity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "ROM", schema = "dbo")
public class Rom {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @ColumnDefault("newid()")
    @Column(name = "ID_ROM")
    private UUID idRom;

    @Size(max = 50)
    @Nationalized
    @Column(name = "dung_luong_rom", length = 50)
    private String dungLuongRom;

    @Size(max = 100)
    @Nationalized
    @Column(name = "loai_o_cung", length = 100)
    private String loaiOCung;

    @Column(name = "toc_do_doc")
    private Integer tocDoDoc;

    @Column(name = "toc_do_ghi")
    private Integer tocDoGhi;

    @Size(max = 500)
    @Nationalized
    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "trang_thai")
    private Boolean trangThai;

}