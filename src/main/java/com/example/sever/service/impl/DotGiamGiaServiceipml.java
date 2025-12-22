package com.example.sever.service.impl;

import com.example.sever.dto.DotGiamGia.DotGiamGiaDTO;
import com.example.sever.entity.DotGiamGia;
import com.example.sever.repository.DotGiamGiaRepository;
import com.example.sever.service.DotGiamGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DotGiamGiaServiceipml implements DotGiamGiaService {

    @Autowired
    private DotGiamGiaRepository repo;

    @Override
    public DotGiamGia create(DotGiamGiaDTO dto) {
        DotGiamGia dot = new DotGiamGia(
                dto.getIdDotGiamGia(),
                dto.getTenDotGiamGia(),
                dto.getMoTa(),
                dto.getNgayBatDau(),
                dto.getNgayKetThuc(),
                dto.getTrangThai()
        );
        return repo.save(dot);
    }

    @Override
    public DotGiamGia update(UUID id, DotGiamGiaDTO dto) {
        DotGiamGia dot = repo.findById(id).orElseThrow(() -> new RuntimeException("Đợt giảm giá không tồn tại!"));

        dot.setIdDotGiamGia(dto.getIdDotGiamGia());
        dot.setTenDotGiamGia(dto.getTenDotGiamGia());
        dot.setMoTa(dto.getMoTa());
        dot.setNgayBatDau(dto.getNgayBatDau());
        dot.setNgayKetThuc(dto.getNgayKetThuc());
        dot.setTrangThai(dto.getTrangThai());

        return repo.save(dot);
    }

    @Override
    public List<DotGiamGia> getAll() {
        return repo.findAll();
    }

    @Override
    public DotGiamGia getById(UUID id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy đợt giảm giá!"));

    }

    @Override
    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
