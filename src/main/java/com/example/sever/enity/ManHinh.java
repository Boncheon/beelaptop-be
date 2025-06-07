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
@Table(name = "ManHinh", schema = "dbo")
public class ManHinh {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @ColumnDefault("newid()")
    @Column(name = "ID_ManHinh")
    private UUID idManhinh;

    @Size(max = 50)
    @Nationalized
    @Column(name = "do_phan_giai", length = 50)
    private String doPhanGiai;

    @Column(name = "tan_so_quet")
    private Integer tanSoQuet;

    @Size(max = 100)
    @Nationalized
    @Column(name = "loai_tam_nen", length = 100)
    private String loaiTamNen;

    @Size(max = 500)
    @Nationalized
    @Column(name = "mo_ta", length = 500)
    private String moTa;

}