package com.example.sever.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "GiamGiaHoaDon", schema = "dbo")
public class GiamGiaHoaDon {

    @Id
    @ColumnDefault("newid()")
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 20)
    @Column(name = "id_giam_gia_hoa_don", length = 20)
    private String idGiamgiahoadon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_order")
    private Order idOrders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_phieu_giam_gia")
    private PhieuGiamGia idPhieuGiamGia;

    @Column(name = "so_tien_truoc_giam", precision = 18, scale = 2)
    private BigDecimal soTienTruocGiam;

    @Column(name = "tien_sau_giam", precision = 18, scale = 2)
    private BigDecimal soTienSauGiam;

    // ⚠ Nếu trong code bạn có dùng getSoTienGiam() ở mapper,
    // thì trong project thực tế bạn đang có thêm field soTienGiam.
    // Ở đây mình giữ nguyên theo snippet bạn gửi.
}
