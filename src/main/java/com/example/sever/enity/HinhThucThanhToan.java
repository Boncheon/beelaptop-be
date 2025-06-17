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
import org.hibernate.annotations.Nationalized;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "HinhThucThanhToan", schema = "dbo")
public class HinhThucThanhToan {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 20)
    @Column(name = "ID_HinhThucThanhToan", length = 20)
    private String idHinhthucthanhtoan;

    @Size(max = 100)
    @Nationalized
    @Column(name = "ten_hinh_thuc", length = 100)
    private String tenHinhThuc;

    @Size(max = 500)
    @Nationalized
    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @OneToMany(mappedBy = "idHinhThucThanhToan")
    private Set<HinhThucThanhToanChiTiet> hinhThucThanhToanChiTiets = new LinkedHashSet<>();

}