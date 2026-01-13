package com.example.sever.service.impl;

import com.example.sever.dto.request.HeDieuHanhAddRequestDTO;
import com.example.sever.dto.request.HeDieuHanhUpdateRequestDTO;
import com.example.sever.dto.response.HeDieuHanhDisplayReponse;
import com.example.sever.entity.HeDieuHanh;
import com.example.sever.mapper.HeDieuHanhMapper;

import com.example.sever.repository.HeDieuHanhRepository;
import com.example.sever.service.HeDieuHanhService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class HeDieuHanhServiceImpl implements HeDieuHanhService {
    HeDieuHanhRepository hedieuhanhRepository;
    HeDieuHanhMapper hedieuhanhMapper;

    @Override
    public Page<HeDieuHanhDisplayReponse> getAllHeDieuHanhforDisplay(Pageable pageable) {
        Page<HeDieuHanh> HeDieuHanhPage = hedieuhanhRepository.findAll(pageable);
        List<HeDieuHanhDisplayReponse> romDisplayReponses = HeDieuHanhPage.getContent().stream()
                .map(hedieuhanhMapper::getAlldisplayHeDieuHanh).collect(Collectors.toList());

        return new PageImpl<>(romDisplayReponses , pageable, HeDieuHanhPage.getTotalElements());
    }

    @Override
    public Page<HeDieuHanhDisplayReponse> getTrangThaiCpuforDisplay(Pageable pageable) {
        Page<HeDieuHanh> heDieuHanhPage = hedieuhanhRepository.findByTrangThai(1, pageable);

        List<HeDieuHanhDisplayReponse> responses = heDieuHanhPage.getContent()
                .stream()
                .map(hedieuhanhMapper::getAlldisplayHeDieuHanh)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, heDieuHanhPage.getTotalElements());
    }

    @Override
    public HeDieuHanh addHeDieuHanh(HeDieuHanhAddRequestDTO adddto) {
        HeDieuHanh hedieuhanh = hedieuhanhMapper.toHeDieuHanh(adddto);

        if (hedieuhanh.getTrangThai() == null) {
            hedieuhanh.setTrangThai(1);
        }

        Instant now = Instant.now();
        hedieuhanh.setNgayTao(now);
        hedieuhanh.setNgaySua(now);

        return hedieuhanhRepository.save(hedieuhanh);
    }

    @Override
    public HeDieuHanh updateHeDieuHanh(HeDieuHanhUpdateRequestDTO updatedto) {
        HeDieuHanh existing = hedieuhanhRepository.findById(updatedto.getId())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy Hệ Điều Hành với ID: " + updatedto.getId())
                );

        hedieuhanhMapper.updateHeDieuHanh(existing, updatedto);

        if (updatedto.getTrangThai() != null) {
            existing.setTrangThai(updatedto.getTrangThai());
        }

        existing.setNgaySua(Instant.now());

        return hedieuhanhRepository.save(existing);
    }

    @Override
    public HeDieuHanhDisplayReponse getDetailedHeDieuHanh(UUID id) {
        HeDieuHanh hdh = hedieuhanhRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy do hoa với ID: " + id));
        return hedieuhanhMapper.toHeDieuHanhDisplayReponse(hdh);
    }
}
