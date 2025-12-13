package com.example.sever.dto.GioHang;

import lombok.Data;
import java.util.UUID;

@Data
public class GioHangChiTietDTO {
    private UUID id;
    private String idGiohangchitiet;
    private UUID idGioHang;

    // ✅ thêm field đúng với entity: id_spct
    private UUID idSpct;

    private Integer isSelected;
    private Integer soLuong;
}
