package com.example.sever.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThongKeTruyCapResponseDTO {
    private List<TaiKhoanTruyCapDTO> onlineAccounts;
    private List<TaiKhoanTruyCapDTO> offlineAccounts;
    private int totalOnline;
    private int totalOffline;
}
