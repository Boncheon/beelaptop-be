package com.example.sever.service.impl;


import com.example.sever.dto.DotGiamGia.DotGiamGiaChiTietDTO;
import com.example.sever.entity.DotGiamGia;
import com.example.sever.entity.DotGiamGiaChiTiet;
import com.example.sever.repository.DotGiamGiaChiTietRepository;
import com.example.sever.repository.DotGiamGiaRepository;
import com.example.sever.service.DotGiamGiaChiTietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DotGiamGiaChiTietServiceIpml implements DotGiamGiaChiTietService {


    @Autowired
    private DotGiamGiaChiTietRepository repo;

    @Autowired
    private DotGiamGiaRepository dotRepo;

    @Override
    public DotGiamGiaChiTiet create(DotGiamGiaChiTietDTO dto) {

        DotGiamGia dot = dotRepo.findById(dto.getIdDotGiamGia())
                .orElseThrow(() -> new RuntimeException("Đợt giảm giá không tồn tại!"));

        DotGiamGiaChiTiet ct = new DotGiamGiaChiTiet();
        ct.setIdDotGiamGiaCT(dto.getIdDotGiamGiaCT());
        ct.setDotGiamGia(dot);
        ct.setKieuGiamGia(dto.getKieuGiamGia());
        ct.setGiaTriGiam(dto.getGiaTriGiam());

        return repo.save(ct);
    }

    @Override
    public List<DotGiamGiaChiTiet> getByDot(UUID idDot) {
        return repo.findAll().stream()
                .filter(x -> x.getDotGiamGia().getId().equals(idDot))
                .toList();
    }

    @Override
    public void delete(UUID id) {
        repo.deleteById(id);
    }


}
