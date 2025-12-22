package com.example.sever.dto.Pos;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PosSelectCustomerRequest {

    private UUID idTaiKhoan;      // nếu chọn khách đã có tài khoản
    private UUID idDiaChi;        // có thể null

    private String tenKhachHang;  // override sang Order.tenKhachHang
    private String sdtKhachHang;  // override sang Order.sdtKhachHang
}