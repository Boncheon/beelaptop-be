package com.example.sever.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "OrderCT", schema = "dbo")
public class OrderCT {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 20)
    @Column(name = "id_order_ct", length = 20)
    private String idOrderCt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order")
    private Order idOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_seri")
    private Seri idSeri;

    @Column(name = "gia_ban", precision = 18, scale = 2)
    private BigDecimal giaBan;

    @OneToMany(mappedBy = "idOrderCt")
    private Set<BaoHanh> baoHanhs = new LinkedHashSet<>();

}