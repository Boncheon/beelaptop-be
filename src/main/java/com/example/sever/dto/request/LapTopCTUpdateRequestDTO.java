package com.example.sever.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LapTopCTUpdateRequestDTO {
     private UUID id;
     private String idLaptopChiTiet;
     //     private Laptop idLapTop;
     private UUID idManHinh;
     private UUID idPin;
     private UUID idKichThuoc;
     private UUID idHeDieuHanh;
     private String moTa;
     private LocalDateTime ngayCapNhat;
     private String nguoiTao;
     private String ghiChu;
     private Integer trangThai;
}
