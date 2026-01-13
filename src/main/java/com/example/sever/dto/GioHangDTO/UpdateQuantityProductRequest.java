package com.example.sever.dto.GioHangDTO;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateQuantityProductRequest {
    private UUID idGioHangCT;
    private Integer soLuong;
}
