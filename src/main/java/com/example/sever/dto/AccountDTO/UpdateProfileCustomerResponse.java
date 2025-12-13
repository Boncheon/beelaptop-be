package com.example.sever.dto.AccountDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileCustomerResponse {
    private UUID id;
    private String idTaiKhoan;
    private String ten;
    private String email;
    private String soDienThoai;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String anh;
    private Instant updatedAt;
    private String message;
}




















