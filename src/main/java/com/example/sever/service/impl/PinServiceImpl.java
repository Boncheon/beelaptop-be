package com.example.sever.service.impl;

import com.example.sever.dto.request.PinAddRequestDTO;
import com.example.sever.dto.request.PinUpdateRequestDTO;
import com.example.sever.dto.response.HeDieuHanhDisplayReponse;
import com.example.sever.dto.response.PinDisplayReponse;
import com.example.sever.entity.HeDieuHanh;
import com.example.sever.entity.Pin;
import com.example.sever.mapper.PinMapper;
import com.example.sever.repository.PinRepository;
import com.example.sever.service.PinService;
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
public class PinServiceImpl implements PinService {
    PinRepository pinRepository;
    PinMapper pinMapper;

    @Override
    public Page<PinDisplayReponse> getAllPinforDisplay(Pageable pageable) {
        Page<Pin> PinPage = pinRepository.findAll(pageable);
        List<PinDisplayReponse> romDisplayReponses = PinPage.getContent().stream()
                .map(pinMapper::getAlldisplayPin).collect(Collectors.toList());

        return new PageImpl<>(romDisplayReponses , pageable, PinPage.getTotalElements());
    }

    @Override
    public Pin addPin(PinAddRequestDTO adddto) {
        Pin pin = pinMapper.toPin(adddto);

        if (pin.getTrangThai() == null) {
            pin.setTrangThai(1);
        }

        Instant now = Instant.now();
        pin.setNgayTao(now);
        pin.setNgaySua(now);

        return pinRepository.save(pin);
    }

    @Override
    public Pin updatePin(PinUpdateRequestDTO updatedto) {
        Pin existing = pinRepository.findById(updatedto.getId())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy Pin với ID: " + updatedto.getId())
                );

        pinMapper.updatePin(existing, updatedto);

        if (updatedto.getTrangThai() != null) {
            existing.setTrangThai(updatedto.getTrangThai());
        }

        existing.setNgaySua(Instant.now());

        return pinRepository.save(existing);
    }


    @Override
    public PinDisplayReponse getDetailedPin(UUID id) {
        Pin p = pinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy do hoa với ID: " + id));
        return pinMapper.toPinDisplayReponse(p);
    }
}
