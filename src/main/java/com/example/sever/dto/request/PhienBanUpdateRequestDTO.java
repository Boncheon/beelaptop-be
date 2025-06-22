package com.example.sever.dto.request;

import com.example.sever.dto.response.CpuDisplayReponse;
import com.example.sever.dto.response.DoHoaDisplayReponse;
import com.example.sever.dto.response.MauSacDisplayReponse;
import com.example.sever.dto.response.RamDIsplayReponse;
import com.example.sever.dto.response.RomDisplayReponse;
import com.example.sever.enity.Cpu;
import com.example.sever.enity.DoHoa;
import com.example.sever.enity.MauSac;
import com.example.sever.enity.Ram;
import com.example.sever.enity.Rom;
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
public class PhienBanUpdateRequestDTO {
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
