package com.example.sever.enity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "KichThuoc", schema = "dbo")
public class KichThuoc {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 20)
    @Column(name = "id_kich_thuoc", length = 20)
    private String idKichThuoc;

    @Column(name = "chieu_dai")
    private Double chieuDai;

    @Column(name = "chieu_rong")
    private Double chieuRong;

    @Column(name = "chieu_cao")
    private Double chieuCao;

    @Column(name = "khoi_luong")
    private Double khoiLuong;

    @OneToMany(mappedBy = "idKichThuoc")
    private Set<LaptopChiTiet> laptopChiTiets = new LinkedHashSet<>();

}