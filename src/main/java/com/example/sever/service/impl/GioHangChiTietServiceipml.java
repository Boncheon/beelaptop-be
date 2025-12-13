package com.example.sever.service.impl;

import com.example.sever.dto.GioHang.GioHangChiTietDTO;
import com.example.sever.entity.GioHangChiTiet;
import com.example.sever.repository.GioHangChiTietRepository;
import com.example.sever.repository.GioHangRepository;
import com.example.sever.repository.LaptopChiTietRepository;
import com.example.sever.service.GioHangChiTietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GioHangChiTietServiceipml implements GioHangChiTietService {

    @Autowired
    private GioHangChiTietRepository gioHangCTRepo;

    @Autowired
    private GioHangRepository gioHangRepo;

    @Autowired
    private LaptopChiTietRepository laptopChiTietRepo;

    @Override
    @Transactional
    public GioHangChiTietDTO addToCart(GioHangChiTietDTO dto) {
        if (dto.getIdGioHang() == null) throw new IllegalArgumentException("idGioHang is required");
        if (dto.getIdSpct() == null) throw new IllegalArgumentException("idSpct is required");
        if (dto.getSoLuong() == null || dto.getSoLuong() <= 0) dto.setSoLuong(1);
        if (dto.getIsSelected() == null) dto.setIsSelected(1);

        var gioHang = gioHangRepo.findById(dto.getIdGioHang())
                .orElseThrow(() -> new RuntimeException("Cart not found: " + dto.getIdGioHang()));

        var spct = laptopChiTietRepo.findById(dto.getIdSpct())
                .orElseThrow(() -> new RuntimeException("LaptopChiTiet not found: " + dto.getIdSpct()));

        GioHangChiTiet item = new GioHangChiTiet();

        // ✅ CỰC QUAN TRỌNG: KHÔNG setId() vì entity có @GeneratedValue/@UuidGenerator
        // item.setId(UUID.randomUUID()); ❌ XÓA

        item.setIdGiohangchitiet("CT" + System.currentTimeMillis());
        item.setIdGioHang(gioHang);
        item.setIdSpct(spct);
        item.setSoLuong(dto.getSoLuong());
        item.setIsSelected(dto.getIsSelected());

        GioHangChiTiet saved = gioHangCTRepo.save(item);

        dto.setId(saved.getId());
        dto.setIdGiohangchitiet(saved.getIdGiohangchitiet());
        return dto;
    }

    @Override
    public List<GioHangChiTietDTO> getCartItems(UUID idGioHang) {
        return gioHangCTRepo.findByIdGioHang_Id(idGioHang)
                .stream()
                .map(item -> {
                    GioHangChiTietDTO dto = new GioHangChiTietDTO();
                    dto.setId(item.getId());
                    dto.setIdGiohangchitiet(item.getIdGiohangchitiet());
                    dto.setIdGioHang(idGioHang);
                    dto.setIdSpct(item.getIdSpct() != null ? item.getIdSpct().getId() : null);
                    dto.setSoLuong(item.getSoLuong());
                    dto.setIsSelected(item.getIsSelected());
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional
    public void updateQuantity(UUID id, Integer quantity) {
        if (quantity == null || quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");

        GioHangChiTiet item = gioHangCTRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + id));

        item.setSoLuong(quantity);
        // ✅ item managed -> save cũng được, không save vẫn update
        gioHangCTRepo.save(item);
    }

    @Override
    @Transactional
    public void deleteItem(UUID id) {
        gioHangCTRepo.deleteById(id);
    }
}