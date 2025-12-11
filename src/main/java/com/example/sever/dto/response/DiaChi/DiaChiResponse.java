package com.example.sever.dto.response.DiaChi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;
@AllArgsConstructor
@Getter
@Setter
public class DiaChiResponse {
    private UUID id;
    private String idDiaChi;

    private UUID idTaiKhoan;

    private String quocGia;
    private String tinhThanh;
    private String quanHuyen;
    private String phuongXa;
    private String diaChiChiTiet;

    private Boolean macDinh;
    private String hoTen;
    private String soDienThoai;
}
