package com.example.sever.service.impl;

import com.example.sever.dto.request.KichThuocAddRequestDTO;
import com.example.sever.dto.request.KichThuocUpdateRequestDTO;
import com.example.sever.dto.response.HeDieuHanhDisplayReponse;
import com.example.sever.dto.response.KichThuocDisplayReponse;
import com.example.sever.entity.HeDieuHanh;
import com.example.sever.entity.KichThuoc;
import com.example.sever.mapper.KichThuocMapper;
import com.example.sever.repository.KichThuocRepository;
import com.example.sever.service.KichThuocService;
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
public class KichThuocServiceImpl implements KichThuocService {
    KichThuocRepository kichthuocRepository;
    KichThuocMapper kichThuocMapper;

    @Override
    public Page<KichThuocDisplayReponse> getAllKichThuocforDisplay(Pageable pageable) {
        Page<KichThuoc> KichThuocPage = kichthuocRepository.findAll(pageable);
        List<KichThuocDisplayReponse> romDisplayReponses = KichThuocPage.getContent().stream()
                .map(kichThuocMapper::getAlldisplayKichThuoc).collect(Collectors.toList());

        return new PageImpl<>(romDisplayReponses , pageable, KichThuocPage.getTotalElements());
    }
    @Override
    public KichThuoc addKichThuoc(KichThuocAddRequestDTO adddto) {
        KichThuoc kichthuoc = kichThuocMapper.toKichThuoc(adddto);

        if (kichthuoc.getTrangThai() == null) {
            kichthuoc.setTrangThai(1);
        }

        Instant now = Instant.now();
        kichthuoc.setNgayTao(now);
        kichthuoc.setNgaySua(now);

        return kichthuocRepository.save(kichthuoc);
    }

    @Override
    public KichThuoc updateKichThuoc(KichThuocUpdateRequestDTO updatedto) {
        KichThuoc existing = kichthuocRepository.findById(updatedto.getId())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy Kích Thước với ID: " + updatedto.getId())
                );

        kichThuocMapper.updateKichThuoc(existing, updatedto);

        if (updatedto.getTrangThai() != null) {
            existing.setTrangThai(updatedto.getTrangThai());
        }

        existing.setNgaySua(Instant.now());

        return kichthuocRepository.save(existing);
    }
    @Override
    public KichThuocDisplayReponse getDetailedKichThuoc(UUID id) {
        KichThuoc kt = kichthuocRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy do hoa với ID: " + id));
        return kichThuocMapper.toKichThuocDisplayReponse(kt);
    }
}
