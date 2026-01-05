package com.example.sever.dto.AccountDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class DiaChiCreateRequest {
    private String idDiaChi;
    private UUID idTaiKhoan;
    private String quocGia;
    private String tinhThanh;
    private String quanHuyen;
    private String phuongXa;
    private String diaChiChiTiet;
    private String hoTen;
    private String soDienThoai;

    private Integer provinceId;         // ID tỉnh theo GHN


    private Integer districtId;         // ID quận/huyện theo GHN (bắt buộc)


    private String wardCode;
}
