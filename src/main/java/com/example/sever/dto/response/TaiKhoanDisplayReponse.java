package com.example.sever.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoanDisplayReponse {
     UUID id;
     String idTaiKhoan;
     RoleDisplayReponse role;
     String ten;
     String soDienThoai;
     LocalDate ngaySinh;
     String email;
     String gioiTinh;
     String matKhau;
     String anh;
     int trangThai;
}
