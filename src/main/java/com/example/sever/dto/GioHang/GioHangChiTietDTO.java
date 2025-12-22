package com.example.sever.dto.GioHang;

import lombok.Data;

import java.util.UUID;

@Data
public class GioHangChiTietDTO {

    private UUID id;
    private String idGiohangchitiet;
    private UUID idGioHang;
    private UUID idSeri;
    private Integer isSelected;
    private Integer soLuong;

}
