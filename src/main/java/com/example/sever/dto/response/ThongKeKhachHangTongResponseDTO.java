package com.example.sever.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeKhachHangTongResponseDTO {
    private Long tongKhachHangDaMua; // distinct idTaiKhoan có đơn hoàn tất & đã thanh toán
}
