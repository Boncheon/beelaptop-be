package com.example.sever.dto.request;

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
public class PhienBanAddRequestDTO {

     String idPhienBan;

     Ram ram;

     Rom rom;

     Cpu cpu;

     DoHoa doHoa;

     MauSac mauSac;

     BigDecimal giaBan;

     Integer trangThai;

     String moTa;
}
