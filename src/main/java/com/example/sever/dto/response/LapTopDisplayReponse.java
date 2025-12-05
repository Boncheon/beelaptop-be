package com.example.sever.dto.response;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LapTopDisplayReponse {

     private UUID id;          // ID laptop
     private String idLaptop;  // Mã SKU

     private String tenSanPham;
     private String moTa;

     // ===== ID FK (để FE bind form sửa) =====
     private UUID idThuongHieu;
     private UUID idManHinh;
     private UUID idPin;
     private UUID idKichThuoc;
     private UUID idHeDieuHanh;

     // ===== Tên hiển thị =====
     private String tenThuongHieu;
     private String tenManHinh;
     private String tenPin;
     private String tenKichThuoc;
     private String tenHeDieuHanh;

     private Instant ngayTao;
     private Instant ngaySua;
}
