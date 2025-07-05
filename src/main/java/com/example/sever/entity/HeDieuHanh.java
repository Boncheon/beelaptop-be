package com.example.sever.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "HeDieuHanh", schema = "dbo")
public class HeDieuHanh {
    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Size(max = 20)
    @Column(name = "ma", length = 20)
    private String ma;

    @Size(max = 100)
    @Nationalized
    @Column(name = "ten", length = 100)
    private String ten;

    @Size(max = 100)
    @Nationalized
    @Column(name = "phien_ban", length = 100)
    private String phienBan;


    @OneToMany(mappedBy = "idHeDieuHanh")
    private Set<LaptopChiTiet> laptopChiTiets = new LinkedHashSet<>();


}