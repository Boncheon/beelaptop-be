package com.example.sever.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "OrderCT", schema = "dbo")
public class OrderCT {

    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    @Size(max = 20)
    @Column(name = "id_order_ct", length = 20, unique = true, nullable = false)
    private String idOrderCt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order")
    private Order idOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_seri")
    private Seri idSeri;

    @Column(name = "gia_ban", precision = 18, scale = 2)
    private BigDecimal giaBan;

    // ✅ Auto sinh ID + id_order_ct trước khi insert
    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (idOrderCt == null || idOrderCt.isBlank()) {
            // Bạn có thể đổi format nếu thích
            this.idOrderCt = "ODCT" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }
    }
}
