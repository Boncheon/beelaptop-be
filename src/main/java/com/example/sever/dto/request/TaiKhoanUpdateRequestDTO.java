package com.example.sever.dto.request;

import com.example.sever.dto.response.RoleDisplayReponse;
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
public class TaiKhoanUpdateRequestDTO {

     UUID id;
     String idTaiKhoan;
     RoleDisplayReponse role;
     String ten;
     String soDienThoai;
     String email;
     LocalDate ngaySinh;
     String gioiTinh;
     String matKhau;
     String anh;
     int trangThai;

}
