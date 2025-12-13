package com.example.sever.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class AddCartRequestDTO {
    private UUID idTaiKhoan;
    private String idSpct;
    private Integer soLuong;
}
