package com.example.sever.dto.request;

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
public class PhienBanAddRequestDTO  {
     private String idPhienBan;

     private UUID ramId;
     private UUID romId;
     private UUID cpuId;
     private UUID doHoaId;
     private UUID mauSacId;

     private BigDecimal giaBan;
     private Integer trangThai;
     private String moTa;
}