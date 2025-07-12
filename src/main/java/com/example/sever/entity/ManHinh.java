package com.example.sever.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.util.LinkedHashSet;
import java.util.Set;
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

    @Size(max = 20)
    @Column(name = "ma", length = 20)
    private String ma;

    @Size(max = 50)
    @Nationalized
    @Column(name = "do_phan_giai", length = 50)
    private String doPhanGiai;

    @Column(name = "tan_so_quet")
    private Integer tanSoQuet;

    @Column(name = "kich_thuoc")
    private Double kichThuoc;

    @OneToMany(mappedBy = "idManHinh")
    private Set<LaptopChiTiet> laptopChiTiets = new LinkedHashSet<>();

}