package com.example.sever.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeTrangThaiResponseDTO {
    private Long donHangChoXacNhan;  // trang_thai = 0
    private Long donHangDangXuLy;     // trang_thai = 1
    private Long donHangDangGiao;     // trang_thai = 2
    private Long donHangHoanThanh;    // trang_thai = 3
    private Long donHangDaHuy;        // trang_thai = 4
}
