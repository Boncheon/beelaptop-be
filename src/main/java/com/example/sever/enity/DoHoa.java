package com.example.sever.enity;

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
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @ColumnDefault("newid()")
    @Column(name = "ID_DoHoa")
    private UUID idDohoa;

    @Size(max = 255)
    @Nationalized
    @Column(name = "hangCard")
    private String hangCard;

    @Size(max = 255)
    @Nationalized
    @Column(name = "modelCard")
    private String modelCard;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ten_day_duCard")
    private String tenDayDucard;

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

    @Column(name = "trang_thai")
    private Boolean trangThai;

}