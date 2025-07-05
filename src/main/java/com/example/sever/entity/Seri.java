package com.example.sever.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "Seri", schema = "dbo")
public class Seri {
    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();


    @Size(max = 20)
    @Column(name = "id_seri", length = 20)
    private String idSeri;

    @ManyToOne
    @JoinColumn(name = "id_phien_ban")
    private PhienBan phienBan;

    @Column(name = "gia_goc", precision = 18, scale = 2)
    private BigDecimal giaGoc;

    @Size(max = 255)
    @Nationalized
    @Column(name = "nguon_seri")
    private String nguonSeri;

    @Column(name = "trang_thai")
    private Integer trangThai;

//    @OneToMany(mappedBy = "idSeri")
//    private Set<BaoHanh> baoHanhs = new LinkedHashSet<>();
//
//    @OneToMany(mappedBy = "idSeri")
//    private Set<GioHangChiTiet> gioHangChiTiets = new LinkedHashSet<>();
//
//    @OneToMany(mappedBy = "idSeri")
//    private Set<OrderCT> orderCTS = new LinkedHashSet<>();

}