package com.example.sever.service.impl;

import com.example.sever.dto.GioHang.GioHangDTO;
import com.example.sever.entity.GioHang;
import com.example.sever.entity.GioHangChiTiet;
import com.example.sever.repository.GioHangChiTietRepository;
import com.example.sever.repository.GioHangRepository;
import com.example.sever.repository.TaiKhoanRepository;
import com.example.sever.service.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GioHangServiceipml implements GioHangService {

    @Autowired
    GioHangRepository gioHangRepo;

    @Autowired
    GioHangChiTietRepository gioHangChiTietRepo;

    @Autowired
    TaiKhoanRepository taiKhoanRepo;


    @Override
    public GioHangDTO createCart(UUID idTaiKhoan) {
        var taiKhoan = taiKhoanRepo.findById(idTaiKhoan)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GioHang gh = new GioHang();
        gh.setId(UUID.randomUUID());
        gh.setIdTaiKhoan(taiKhoan);
        gh.setNgayTao(Instant.now());
        gh.setIdGioHang("GH" + System.currentTimeMillis());

        gioHangRepo.save(gh);

        GioHangDTO dto = new GioHangDTO();
        dto.setId(gh.getId());
        dto.setIdGioHang(gh.getIdGioHang());
        dto.setIdTaiKhoan(idTaiKhoan);
        dto.setNgayTao(gh.getNgayTao());
        return dto;
    }

    @Override
    public GioHangDTO getCartByTaiKhoan(UUID idTaiKhoan) {
        var gh = gioHangRepo.findByIdTaiKhoan_Id(idTaiKhoan)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        GioHangDTO dto = new GioHangDTO();
        dto.setId(gh.getId());
        dto.setIdGioHang(gh.getIdGioHang());
        dto.setIdTaiKhoan(idTaiKhoan);
        dto.setNgayTao(gh.getNgayTao());
        return dto;
    }



}
