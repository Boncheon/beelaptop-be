package com.example.sever.entity;


import jakarta.persistence.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "PhienBan", schema = "dbo")
public class PhienBan {
    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Size(max = 20)
    @Column(name = "id_phien_ban", length = 20)
    private String idPhienBan;

    @ManyToOne
    @JoinColumn(name = "id_ram")
    private Ram idRam;

    @ManyToOne
    @JoinColumn(name = "id_ssd")
    private Rom idSsd;

    @ManyToOne
    @JoinColumn(name = "id_cpu")
    private Cpu idCpu;

    @ManyToOne
    @JoinColumn(name = "id_dohoa")
    private DoHoa idDohoa;

    @ManyToOne
    @JoinColumn(name = "id_mau_sac")
    private MauSac idMauSac;

    @Column(name = "gia_ban", precision = 18, scale = 2)
    private BigDecimal giaBan;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Size(max = 500)
    @Nationalized
    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "ngay_cap_nhat")
    private Instant ngayCapNhat;

//    @OneToMany(mappedBy = "idPhienBan")
//    private Set<PhienbanLaptopct> phienbanLaptopcts = new LinkedHashSet<>();
//
//    @OneToMany(mappedBy = "idPhienBan")
//    private Set<Seri> seris = new LinkedHashSet<>();

}