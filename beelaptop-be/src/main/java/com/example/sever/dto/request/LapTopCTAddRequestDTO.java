package com.example.sever.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LapTopCTAddRequestDTO {
     private UUID idLaptop;
     private String idLaptopChiTiet;
     private UUID idManHinh;
     private UUID idPin;
     private UUID idKichThuoc;
     private UUID idHeDieuHanh;

     private String moTa;
     private String nguoiTao;
     private String ghiChu;
     private Integer trangThai;
}
