package com.example.sever.dto.request;

import com.example.sever.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
