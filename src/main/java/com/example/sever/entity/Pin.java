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
@Table(name = "Pin", schema = "dbo")
public class Pin {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 20)
    @Column(name = "id_pin", length = 20)
    private String idPin;

    @Size(max = 50)
    @Nationalized
    @Column(name = "dung_luong", length = 50)
    private String dungLuong;

    @OneToMany(mappedBy = "idPin")
    private Set<LaptopChiTiet> laptopChiTiets = new LinkedHashSet<>();

}