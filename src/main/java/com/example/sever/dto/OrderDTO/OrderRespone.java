package com.example.sever.dto.OrderDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRespone {

    private String maDonHang;
    private BigDecimal tongTien;
    private BigDecimal soTienGiam;
    private String phieuGiamGia;
    private BigDecimal tongPhaiTra;

}
