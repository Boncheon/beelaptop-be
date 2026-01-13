package com.example.sever.dto.OrderDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCustomerRequest {
    private UUID idTaiKhoan;
    private UUID idDiaChi;
    private String tenKhachHang;
    private String sdtKhachHang;
    private String loaiDon;
    private BigDecimal phiVanChuyen;
    private BigDecimal phiDichVuKhac;
    private String ghiChu;
    private UUID idPhieuGiamGia;
    private String diaChiDayDu;
    private Boolean isVnPay;
    private List<OrderCTCustomerRequest> listOrderCT;
    private List<PaymentCustomerRequest> listHinhThucThanhToan;

    private Boolean useInsurance;

}





