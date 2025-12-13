package com.example.sever.dto.OrderDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderListCustomerResponse {
    private UUID idOrder;
    private String maDonHang;
    private Instant ngayTao;
    private List<String> hinhThucThanhToan;
    private Integer trangThai;
    private String tenKhachHang;
    private String sdtKhachHang;
    private BigDecimal tongTienThuHo;
    private BigDecimal giaTriChuaGiam;
    private BigDecimal giaTriGiamGia;

    private String loaiDon;
    private String ghiChu;
    private String diaChi; // Địa chỉ đầy đủ: địa chỉ chi tiết, xã, huyện, tỉnh
}











