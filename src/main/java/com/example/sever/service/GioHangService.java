package com.example.sever.service;

import com.example.sever.dto.GioHang.GioHangDTO;

import java.util.UUID;

public interface GioHangService {

    GioHangDTO createCart(UUID idTaiKhoan);
    GioHangDTO getCartByTaiKhoan(UUID idTaiKhoan);


}
