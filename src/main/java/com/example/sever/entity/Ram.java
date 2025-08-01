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
@Table(name = "RAM", schema = "dbo")
public class Ram {
    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Size(max = 20)
    @Column(name = "id_loai_ram", length = 20)
    private String idLoaiRam;

    @Size(max = 50)
    @Nationalized
    @Column(name = "dung_luong_ram", length = 50)
    private String dungLuongRam;

    @Column(name = "bus")
    private Integer bus;

    @Size(max = 500)
    @Nationalized
    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @ColumnDefault("0")
    @Column(name = "trang_thai")
    private Integer trangThai;

//    @OneToMany(mappedBy = "idRam")
//    private Set<PhienBan> phienBans = new LinkedHashSet<>();

}