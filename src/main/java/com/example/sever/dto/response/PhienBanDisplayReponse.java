package com.example.sever.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhienBanDisplayReponse {
     private UUID id;
     private String idPhienBan;

     private String tenRam;
     private String tenRom;
     private String tenCpu;
     private String tenDoHoa;
     private String tenMauSac;

     private BigDecimal giaBan;
     private Integer trangThai;
     private String moTa;

     private Long soLuongTonKho;
}
