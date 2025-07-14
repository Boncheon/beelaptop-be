package com.example.sever.dto.response;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailResponse implements Serializable {
    String id;
    String idTaiKhoan;
    String ten;
    String email;
    String soDienThoai;
    String gioiTinh;
    LocalDate ngaySinh;
    String anh;
    String tenChucVu;
    Integer trangThai;
    Instant createdAt;
    Instant updatedAt;

    String quocGia;


     String tinhThanh;


  String quanHuyen;


    String phuongXa;

    String diaChiChiTiet;


}
