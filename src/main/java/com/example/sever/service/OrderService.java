package com.example.sever.service;

import com.example.sever.dto.OrderDTO.OrderRequest;
import com.example.sever.dto.OrderDTO.OrderRespone;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderRespone taoDonHang(OrderRequest request);

    void addItemsToOrder(UUID orderId, List<UUID> seriIds);

}
