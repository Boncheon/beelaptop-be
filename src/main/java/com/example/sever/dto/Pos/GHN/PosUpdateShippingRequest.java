package com.example.sever.dto.Pos.GHN;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class PosUpdateShippingRequest {
    private Boolean giaoHang;          // true/false
    private BigDecimal phiVanChuyen;   // phí ship

    private String hoTen;
    private String soDienThoai;

    private String diaChiChiTiet;
    private String quocGia;
    private String tinhThanh;
    private String quanHuyen;
    private String phuongXa;

    private Integer provinceId;
    private Integer districtId;
    private String wardCode;

    // nếu chọn từ địa chỉ có sẵn
    private UUID idDiaChi;
    private Boolean saveAddress;   // true: lưu địa chỉ mới vào bảng DiaChi
    private Boolean setAsDefault;  // true: set địa chỉ mới thành mặc định

}
