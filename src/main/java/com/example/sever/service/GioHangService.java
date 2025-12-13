package com.example.sever.service;

import com.example.sever.dto.GioHang.GioHangDTO;
import com.example.sever.dto.GioHangDTO.AddProductRequest;
import com.example.sever.dto.GioHangDTO.UpdateQuantityProductRequest;
import com.example.sever.dto.response.GioHang.CartResponse;

import java.util.UUID;

public interface GioHangService {

    CartResponse addProduct(AddProductRequest request);

    CartResponse getProductUser(UUID isUser);

    void updateQuantityProduct(UpdateQuantityProductRequest request);

    void deleteProductCart(UUID idCartDetail);

    void updateStatus(UUID idCartDetail, Integer isSelected);

}
