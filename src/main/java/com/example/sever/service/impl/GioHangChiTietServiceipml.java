package com.example.sever.service.impl;

import com.example.sever.dto.GioHang.GioHangChiTietDTO;
import com.example.sever.entity.GioHangChiTiet;
import com.example.sever.repository.GioHangChiTietRepository;
import com.example.sever.repository.GioHangRepository;
import com.example.sever.repository.SeriRepository;
import com.example.sever.service.GioHangChiTietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GioHangChiTietServiceipml implements GioHangChiTietService {

    @Autowired
    GioHangChiTietRepository gioHangCTRepo;

    @Autowired
    GioHangRepository gioHangRepo;

    @Autowired
    SeriRepository seriRepo;


    @Override
    public GioHangChiTietDTO addToCart(GioHangChiTietDTO dto) {
        var gioHang = gioHangRepo.findById(dto.getIdGioHang())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        var seri = seriRepo.findById(dto.getIdSeri())
                .orElseThrow(() -> new RuntimeException("Seri not found"));

        GioHangChiTiet item = new GioHangChiTiet();
        item.setId(UUID.randomUUID());
        item.setIdGioHang(gioHang);
        item.setIdSeri(seri);
        item.setSoLuong(dto.getSoLuong());
        item.setIsSelected(dto.getIsSelected());
        item.setIdGiohangchitiet("CT" + System.currentTimeMillis());

        gioHangCTRepo.save(item);

        dto.setId(item.getId());
        return dto;
    }

    @Override
    public List<GioHangChiTietDTO> getCartItems(UUID idGioHang) {
        return gioHangCTRepo.findByIdGioHang_Id(idGioHang)
                .stream().map(item -> {
                    GioHangChiTietDTO dto = new GioHangChiTietDTO();
                    dto.setId(item.getId());
                    dto.setIdGioHang(idGioHang);
                    dto.setIdSeri(item.getIdSeri().getId());
                    dto.setSoLuong(item.getSoLuong());
                    dto.setIsSelected(item.getIsSelected());
                    return dto;
                }).toList();
    }

    @Override
    public void updateQuantity(UUID id, Integer quantity) {
        var item = gioHangCTRepo.findById(id).orElseThrow();
        item.setSoLuong(quantity);
        gioHangCTRepo.save(item);
    }

    @Override
    public void deleteItem(UUID id) {
        gioHangCTRepo.deleteById(id);
    }
}
