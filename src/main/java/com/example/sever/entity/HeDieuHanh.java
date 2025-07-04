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
@Table(name = "HeDieuHanh", schema = "dbo")
public class HeDieuHanh {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

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