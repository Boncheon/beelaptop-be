package com.example.sever.dto.OrderDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailCustomerResponse {
    private UUID idOrder;
    private String maDonHang;
    private String tenKhachHang;
    private String sdtKhachHang;
    private String diaChiGiaoHang;
    private Instant ngayDat;
    private List<String> hinhThucThanhToan;
    private Integer trangThai;
    private String tenTrangThai;
    
    private List<OrderProductCustomerResponse> danhSachSanPham;
    
    private BigDecimal tongTienHang;
    private BigDecimal khuyenMai;
    private BigDecimal phiVanChuyen;
    private BigDecimal tongThanhToan;
}






















