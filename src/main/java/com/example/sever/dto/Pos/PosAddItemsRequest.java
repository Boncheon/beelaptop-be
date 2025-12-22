package com.example.sever.dto.Pos;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PosAddItemsRequest {

    // Danh sách ID Seri được chọn từ FE
    private List<UUID> seriIds;
}