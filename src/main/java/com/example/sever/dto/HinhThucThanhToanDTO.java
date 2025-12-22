package com.example.sever.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HinhThucThanhToanDTO {
    private UUID id;
    private String tenHinhThuc;   // "Tiền mặt", "VNPay", "MoMo" ...

}