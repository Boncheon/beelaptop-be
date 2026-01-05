package com.example.sever.dto.AccountDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DiaChiUpdateRequest {
    private String quocGia;
    private String tinhThanh;
    private String quanHuyen;
    private String phuongXa;
    private String diaChiChiTiet;
    private String hoTen;
    private String soDienThoai;

    private Integer provinceId;
    private Integer districtId;
    private String wardCode;
}
