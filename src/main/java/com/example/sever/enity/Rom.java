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

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "Rom", schema = "dbo")
public class Rom {
    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Size(max = 20)
    @Column(name = "id_ssd", length = 20)
    private String idSsd;

    @Size(max = 50)
    @Nationalized
    @Column(name = "dung_luong_ssd", length = 50)
    private String dungLuongSsd;

    @Size(max = 100)
    @Nationalized
    @Column(name = "loai_ssd", length = 100)
    private String loaiSsd;

    @Column(name = "toc_do_doc")
    private Integer tocDoDoc;

    @Column(name = "toc_do_ghi")
    private Integer tocDoGhi;

    @Size(max = 500)
    @Nationalized
    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @ColumnDefault("0")
    @Column(name = "trang_thai")
    private Integer trangThai;

//    @ColumnDefault("getdate()")
//    @Column(name = "ngay_tao")
//    private Instant ngayTao;

    @OneToMany(mappedBy = "idSsd")
    private Set<PhienBan> phienBans = new LinkedHashSet<>();

}