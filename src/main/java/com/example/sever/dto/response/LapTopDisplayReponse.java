package com.example.sever.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LapTopDisplayReponse {
     private UUID id;
     private String idLaptop;
     private String tenSanPham;
//     private UUID idDanhMuc;
//     private UUID idThuongHieu;
     private String moTa;
     private String nguoiTao;
     private Long soLuongTon; // tổng số Seri thuộc các phiên bản của laptop

}
