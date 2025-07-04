package com.example.sever.dto.req;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest implements Serializable {
    @NotBlank(message = "INVALID_TEN")
    @Size(min = 3, max = 100, message = "INVALID_TEN")
    String ten;

    @NotBlank(message = "INVALID_EMAIL")
    @Email(message = "INVALID_EMAIL")
    String email;

    @NotBlank(message = "INVALID_TEN_DANG_NHAP")
    @Size(min = 3, max = 100, message = "INVALID_TEN_DANG_NHAP")
    String tenDangNhap;

    @NotBlank(message = "INVALID_SO_DIEN_THOAI")
    @Size(min = 10, max = 20, message = "INVALID_SO_DIEN_THOAI")
    String soDienThoai;

    @NotBlank(message = "INVALID_MAT_KHAU")
    @Size(min = 8, message = "INVALID_MAT_KHAU")
    String matKhau;

    @NotNull(message = "INVALID_NGAY_SINH")
    LocalDate ngaySinh;

    @NotBlank(message = "INVALID_GIOI_TINH")
    @Size(max = 10, message = "INVALID_GIOI_TINH")
    String gioiTinh;
}
