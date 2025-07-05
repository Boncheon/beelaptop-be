package com.example.sever.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
    String ten;
    String email;
    String tenDangNhap;
    String soDienThoai;
    String gioiTinh;
    LocalDate ngaySinh;
    String anh;
    String tenChucVu;
    Instant createdAt;
    Instant updatedAt;
}
