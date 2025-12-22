package com.example.sever.dto.Pos;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosPaymentDTO {

    private UUID id;
    private UUID idHinhThucThanhToan;
    private String tenHinhThuc;

    // Số tiền thực tế được hạch toán vào đơn (sau khi trừ tiền thừa)
    private BigDecimal soTien;

    // Tiền khách đưa
    private BigDecimal khachDua;

    // Tiền thừa trả lại
    private BigDecimal tienTraLai;
}