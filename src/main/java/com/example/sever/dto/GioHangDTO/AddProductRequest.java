package com.example.sever.dto.GioHangDTO;

import lombok.Data;

import java.util.UUID;
@Data
public class AddProductRequest {
    private UUID idTaiKhoan;
    private UUID idSpct;
    private Integer soLuong;
}
