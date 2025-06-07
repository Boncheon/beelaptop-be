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
@Table(name = "KichThuoc", schema = "dbo")
public class KichThuoc {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @ColumnDefault("newid()")
    @Column(name = "ID_KichThuoc")
    private UUID idKichthuoc;

    @Column(name = "chieu_dai")
    private Double chieuDai;

    @Column(name = "chieu_rong")
    private Double chieuRong;

    @Column(name = "chieu_cao")
    private Double chieuCao;

    @Column(name = "khoi_luong")
    private Double khoiLuong;

    @Size(max = 500)
    @Nationalized
    @Column(name = "mo_ta", length = 500)
    private String moTa;

}