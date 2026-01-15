package com.example.sever.service;

import com.example.sever.dto.OrderDTO.*;

import java.util.List;
import java.util.UUID;

public interface OrderCustomerService {
    OrderCustomerResponse taoDonHangCustomer(OrderCustomerRequest request);
    
    List<OrderListCustomerResponse> getDanhSachDonHangByTaiKhoanCustomer(UUID idTaiKhoan);
    
    List<OrderProductCustomerResponse> getDanhSachSanPhamByOrderCustomer(UUID idOrder);
    
    List<OrderDetailCustomerResponse> timKiemDonHangCustomer(SearchOrderCustomerRequest request);
    
    OrderCustomerResponse huyDonHangCustomer(UUID idOrder, UUID idTaiKhoan);


}


