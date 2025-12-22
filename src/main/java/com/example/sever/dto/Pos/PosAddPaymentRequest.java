package com.example.sever.dto.Pos;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PosAddPaymentRequest {

    private UUID idHinhThucThanhToan;  // ID_HinhThucThanhToan


    // Tiền khách đưa (ở UI: ô "Khách hàng đưa")
    private BigDecimal khachDua;         // nếu cần
}