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
@Table(name = "CPU", schema = "dbo")
public class Cpu {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @ColumnDefault("newid()")
    @Column(name = "ID_CPU")
    private UUID idCpu;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ten")
    private String ten;

    @Size(max = 500)
    @Nationalized
    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "trang_thai")
    private Boolean trangThai;

}