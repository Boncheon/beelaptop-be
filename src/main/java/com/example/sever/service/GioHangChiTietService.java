package com.example.sever.service;

import com.example.sever.dto.GioHang.GioHangChiTietDTO;

import java.util.List;
import java.util.UUID;

public interface GioHangChiTietService {

    GioHangChiTietDTO addToCart(GioHangChiTietDTO dto);
    List<GioHangChiTietDTO> getCartItems(UUID idGioHang);
    void updateQuantity(UUID id, Integer quantity);
    void deleteItem(UUID id);

}
