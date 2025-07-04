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
@Table(name = "DanhMuc", schema = "dbo")
public class DanhMuc {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 255)
    @Nationalized
    @Column(name = "Ten")
    private String ten;

    @OneToMany(mappedBy = "idDanhMuc")
    private Set<Laptop> laptops = new LinkedHashSet<>();

}