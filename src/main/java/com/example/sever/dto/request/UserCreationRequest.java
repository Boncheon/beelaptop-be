package com.example.sever.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

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


    @NotBlank(message = "INVALID_SO_DIEN_THOAI")
    @Size(min = 10, max = 20, message = "INVALID_SO_DIEN_THOAI")
    String soDienThoai;


    @Size(min = 8, message = "INVALID_MAT_KHAU")
    String matKhau;

    @NotNull(message = "INVALID_NGAY_SINH")
    LocalDate ngaySinh;

    @NotBlank(message = "INVALID_GIOI_TINH")
    @Size(max = 10, message = "INVALID_GIOI_TINH")
    String gioiTinh;


    @Size(max = 100, message = "QUỐC GIA KHÔNG HỢP LỆ")
    String quocGia;


    @Size(max = 100, message = "TỈNH/THÀNH PHỐ KHÔNG HỢP LỆ")
    String tinhThanh;


    @Size(max = 100, message = "QUẬN/HUYỆN KHÔNG HỢP LỆ")
    String quanHuyen;


    @Size(max = 100, message = "PHƯỜNG/XÃ KHÔNG HỢP LỆ")
    String phuongXa;


    @Size(max = 255, message = "ĐỊA CHỈ CHI TIẾT KHÔNG HỢP LỆ")
    String diaChiChiTiet;

    MultipartFile anh;
}
