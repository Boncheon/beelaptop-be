package com.example.sever.dto.GioHang;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class GioHangDTO {

    private UUID id;
    private String idGioHang;
    private UUID idTaiKhoan;
    private Instant ngayTao;

}
