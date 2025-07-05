package com.example.sever.entity;

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
@Table(name = "DoHoa", schema = "dbo")
public class DoHoa {
    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Size(max = 20)
    @Column(name = "id_dohoa", length = 20)
    private String idDohoa;

    @Size(max = 255)
    @Nationalized
    @Column(name = "hangcard_oboard")
    private String hangcardOboard;

    @Size(max = 255)
    @Nationalized
    @Column(name = "modelcard_oboard")
    private String modelcardOboard;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ten_day_du")
    private String tenDayDu;

    @Size(max = 100)
    @Nationalized
    @Column(name = "loai_card", length = 100)
    private String loaiCard;

    @Size(max = 50)
    @Nationalized
    @Column(name = "bo_nho_ram", length = 50)
    private String boNhoRam;

    @Size(max = 500)
    @Nationalized
    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @ColumnDefault("0")
    @Column(name = "trang_thai")
    private Integer trangThai;

}