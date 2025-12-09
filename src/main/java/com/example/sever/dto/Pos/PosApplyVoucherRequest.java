package com.example.sever.dto.Pos;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PosApplyVoucherRequest {

    // Ví dụ "VC123" = ID_PhieuGiamGia
    private List<UUID> voucherIds;
}