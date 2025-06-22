package com.example.sever.service.impl;

import com.example.sever.dto.request.PinAddRequestDTO;
import com.example.sever.dto.request.PinUpdateRequestDTO;
import com.example.sever.dto.response.PinDisplayReponse;
import com.example.sever.enity.Pin;
import com.example.sever.mapper.PinMapper;
import com.example.sever.repository.PinRepository;
import com.example.sever.service.PinService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
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
        return pinRepository.save(pin);
    }

    @Override
    public Pin updatePin(PinUpdateRequestDTO updatedto) {
        Pin  existing = pinRepository.findById(updatedto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Đồ Họa với ID: " + updatedto.getId()));

        // 2. Cập nhật dữ liệu từ DTO vào entity cũ
        pinMapper.updatePin(existing, updatedto);

        // 3. Lưu lại bản ghi đã cập nhật
        return pinRepository.save(existing);
    }
}
