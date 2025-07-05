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

import java.util.List;
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
    public HeDieuHanh addHeDieuHanh(HeDieuHanhAddRequestDTO adddto) {
        HeDieuHanh hedieuhanh = hedieuhanhMapper.toHeDieuHanh(adddto);
        return hedieuhanhRepository.save(hedieuhanh);
    }

    @Override
    public HeDieuHanh updateHeDieuHanh(HeDieuHanhUpdateRequestDTO updatedto) {
        HeDieuHanh  existing = hedieuhanhRepository.findById(updatedto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Đồ Họa với ID: " + updatedto.getId()));

        // 2. Cập nhật dữ liệu từ DTO vào entity cũ
        hedieuhanhMapper.updateHeDieuHanh(existing, updatedto);

        // 3. Lưu lại bản ghi đã cập nhật
        return hedieuhanhRepository.save(existing);
    }
}
