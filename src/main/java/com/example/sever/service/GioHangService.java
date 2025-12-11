package com.example.sever.service;

import com.example.sever.dto.GioHangDTO.AddProductRequest;
import com.example.sever.dto.GioHangDTO.UpdateQuantityProductRequest;
import com.example.sever.dto.request.AddCartRequestDTO;
import com.example.sever.dto.response.GioHang.CartResponse;
import com.example.sever.repository.GioHangRepository;

import java.util.UUID;

public interface GioHangService {
    CartResponse addProduct(AddProductRequest request);

    CartResponse getProductUser(UUID isUser);

    void updateQuantityProduct(UpdateQuantityProductRequest request);

    void deleteProductCart(UUID idCartDetail);

    void updateStatus(UUID idCartDetail, Integer isSelected);

}
