package com.example.sever.service.impl;

import com.example.sever.dto.request.KichThuocAddRequestDTO;
import com.example.sever.dto.request.KichThuocUpdateRequestDTO;
import com.example.sever.dto.response.KichThuocDisplayReponse;
import com.example.sever.entity.KichThuoc;
import com.example.sever.mapper.KichThuocMapper;
import com.example.sever.repository.KichThuocRepository;
import com.example.sever.service.KichThuocService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
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
        return kichthuocRepository.save(kichthuoc);
    }

    @Override
    public KichThuoc updateKichThuoc(KichThuocUpdateRequestDTO updatedto) {
        KichThuoc  existing = kichthuocRepository.findById(updatedto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Đồ Họa với ID: " + updatedto.getId()));

        // 2. Cập nhật dữ liệu từ DTO vào entity cũ
        kichThuocMapper.updateKichThuoc(existing, updatedto);

        // 3. Lưu lại bản ghi đã cập nhật
        return kichthuocRepository.save(existing);
    }

}
