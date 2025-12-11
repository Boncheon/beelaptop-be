package com.example.sever.dto.AccountDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileCustomerRequest {
    @Size(min = 3, max = 100, message = "Tên phải từ 3 đến 100 ký tự")
    private String ten;

    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email tối đa 100 ký tự")
    private String email;

    @Size(min = 10, max = 20, message = "Số điện thoại phải từ 10 đến 20 ký tự")
    private String soDienThoai;

    private LocalDate ngaySinh;

    @Size(max = 10, message = "Giới tính tối đa 10 ký tự")
    private String gioiTinh;

    private MultipartFile anh;
    private String anhUrl;
}

