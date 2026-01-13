package com.example.sever.dto.OrderDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCustomerResponse {
    private UUID idOrder;
    private String maDonHang;
    private BigDecimal tongTien;
    private BigDecimal soTienGiam;
    private String phieuGiamGia;
    private BigDecimal tongPhaiTra;
    private Integer trangThai;
    private String message;
    private Integer trangThaiThanhToan;
    private String tenTrangThai;          // map từ OrderStatus
    private String tenTrangThaiThanhToan; // map từ PaymentStatus
    private String loaiDon;               // ONLINE / GIAO_HANG / TAI_QUAY
}

























