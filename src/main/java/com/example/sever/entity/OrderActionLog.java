package com.example.sever.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "OrderActionLog", schema = "dbo")
public class OrderActionLog {

    @Id
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 20)
    @Column(name = "id_orderACL", length = 20)
    private String idOrderacl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order")
    private Order idOrder;

    @Column(name = "ngay_tao")
    private Instant ngayTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tai_khoan")
    private TaiKhoan idTaiKhoan;

    @Column(name = "hanh_dong")
    private Integer hanhDong;

    @Size(max = 500)
    @Nationalized
    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (ngayTao == null) ngayTao = Instant.now();
    }

    // ❌ BỎ @PreUpdate: log là lịch sử, không được tự đổi "ngay_tao"
}
