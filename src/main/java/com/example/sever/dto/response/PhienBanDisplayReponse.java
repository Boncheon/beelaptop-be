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
     UUID id;
     String idPhienBan;

     RamDIsplayReponse ram;
    
     RomDisplayReponse rom;
     CpuDisplayReponse cpu;

     DoHoaDisplayReponse doHoa;
    
     MauSacDisplayReponse mauSac;

     BigDecimal giaBan;

     Integer trangThai;
    
     String moTa;
}
