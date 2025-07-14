package com.example.sever.dto.request;

import com.example.sever.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoanAddRequestDTO {

     String idTaiKhoan;
     Role role;
     String ten;
     String soDienThoai;
     String email;
     LocalDate ngaySinh;
     String gioiTinh;
     String matKhau;
     String anh;
     int trangThai;

}
