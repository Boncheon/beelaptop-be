package com.example.sever.dto.Pos;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PosCreateOrderRequest {

    private UUID idTaiKhoan;      // nếu chọn khách đã có tài khoản
    private UUID idDiaChi;        // có thể null khi bán tại quầy

    private String tenKhachHang;  // nếu là khách vãng lai vẫn có thể nhập tên/sđt
    private String sdtKhachHang;

    private String loaiDon;       // gợi ý: mặc định "TAI_QUAY"
    private String ghiChu;
}