package com.example.sever.service.impl;

import com.example.sever.dto.request.ManHinhAddRequestDTO;
import com.example.sever.dto.request.ManHinhUpdateRequestDTO;
import com.example.sever.dto.response.ManHinhDisplayReponse;
import com.example.sever.entity.HeDieuHanh;
import com.example.sever.entity.ManHinh;
import com.example.sever.mapper.ManHinhMapper;
import com.example.sever.repository.ManHinhRepository;
import com.example.sever.service.ManHinhService;
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
public class ManHinhServiceImpl implements ManHinhService {
    ManHinhRepository manhinhRepository;
    ManHinhMapper manhinhMapper;

    @Override
    public Page<ManHinhDisplayReponse> getAllManHinhforDisplay(Pageable pageable) {
        Page<ManHinh> ManHinhPage = manhinhRepository.findAll(pageable);
        List<ManHinhDisplayReponse> romDisplayReponses = ManHinhPage.getContent().stream()
                .map(manhinhMapper::getAlldisplayManHinh).collect(Collectors.toList());

        return new PageImpl<>(romDisplayReponses , pageable, ManHinhPage.getTotalElements());
    }
    @Override
    public ManHinh addManHinh(ManHinhAddRequestDTO adddto) {
        ManHinh manhinh = manhinhMapper.toManHinh(adddto);

        if (manhinh.getTrangThai() == null) {
            manhinh.setTrangThai(1);
        }

        Instant now = Instant.now();
        manhinh.setNgayTao(now);
        manhinh.setNgaySua(now);

        return manhinhRepository.save(manhinh);
    }

    @Override
    public ManHinh updateManHinh(ManHinhUpdateRequestDTO updatedto) {
        ManHinh existing = manhinhRepository.findById(updatedto.getId())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy Màn Hình với ID: " + updatedto.getId())
                );

        manhinhMapper.updateManHinh(existing, updatedto);

        if (updatedto.getTrangThai() != null) {
            existing.setTrangThai(updatedto.getTrangThai());
        }

        existing.setNgaySua(Instant.now());

        return manhinhRepository.save(existing);
    }

    @Override
    public ManHinhDisplayReponse getDetailedManHinh(UUID id) {
        ManHinh mh = manhinhRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy do hoa với ID: " + id));
        return manhinhMapper.toManHinhDisplayReponse(mh);

    }
}
