package com.example.sever.dto.Pos;


import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderListDTO {

    private UUID id;

    private String maDonHang;

    private String maNhanVien;


    private String tenKhachHang;
    private String sdtKhachHang;

    private BigDecimal tongTien;

    private String loaiDon;           // ONLINE / TAI_QUAY

    private Integer trangThaiDon;     // trạng thái đơn
    private Integer trangThaiThanhToan;
    Integer trangThaiDonForTaiQuay;
    private Instant ngayTao;
    private Instant ngayCapNhat;
}
