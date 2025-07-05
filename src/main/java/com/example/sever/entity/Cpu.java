package com.example.sever.entity;

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
@Table(name = "CPU", schema = "dbo")
public class Cpu {
    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Size(max = 20)
    @Column(name = "id_cpu", length = 20)
    private String idCpu;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ten")
    private String ten;

    @Size(max = 500)
    @Nationalized
    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @ColumnDefault("0")
    @Column(name = "trang_thai")
    private Integer trangThai;

//    @ColumnDefault("getdate()")
//    @Column(name = "ngay_tao")
//    private Instant ngayTao;


}