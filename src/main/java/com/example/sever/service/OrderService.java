package com.example.sever.service;

import com.example.sever.dto.OrderDTO.OrderRequest;
import com.example.sever.dto.OrderDTO.OrderRespone;

public interface OrderService {

    OrderRespone taoDonHang(OrderRequest request);



}
