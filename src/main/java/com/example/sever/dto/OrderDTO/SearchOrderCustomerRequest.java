package com.example.sever.dto.OrderDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchOrderCustomerRequest {
    private String maDonHang; // Mã đơn hàng từ bảng Order
    private String sdt; // Số điện thoại từ bảng TaiKhoan (qua Order.id_tai_khoan)
}

