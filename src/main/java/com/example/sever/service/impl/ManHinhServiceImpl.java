package com.example.sever.service.impl;

import com.example.sever.dto.request.ManHinhAddRequestDTO;
import com.example.sever.dto.request.ManHinhUpdateRequestDTO;
import com.example.sever.dto.response.ManHinhDisplayReponse;
import com.example.sever.entity.ManHinh;
import com.example.sever.mapper.ManHinhMapper;
import com.example.sever.repository.ManHinhRepository;
import com.example.sever.service.ManHinhService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
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
        return manhinhRepository.save(manhinh);
    }

    @Override
    public ManHinh updateManHinh(ManHinhUpdateRequestDTO updatedto) {
        ManHinh  existing = manhinhRepository.findById(updatedto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Đồ Họa với ID: " + updatedto.getId()));

        // 2. Cập nhật dữ liệu từ DTO vào entity cũ
        manhinhMapper.updateManHinh(existing, updatedto);

        // 3. Lưu lại bản ghi đã cập nhật
        return manhinhRepository.save(existing);
    }
}
