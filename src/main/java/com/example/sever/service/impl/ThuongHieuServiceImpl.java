package com.example.sever.service.impl;

import com.example.sever.dto.request.ThuongHieuAddRequestDTO;
import com.example.sever.dto.request.ThuongHieuUpdateRequestDTO;
import com.example.sever.dto.response.ThuongHieuDisplayReponse;
import com.example.sever.entity.ThuongHieu;
import com.example.sever.mapper.ThuongHieuMapper;
import com.example.sever.repository.ThuongHieuRepository;
import com.example.sever.service.ThuongHieuService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ThuongHieuServiceImpl implements ThuongHieuService {
    ThuongHieuRepository ThuongHieuRepository;
    ThuongHieuMapper thuongHieuMapper;

    @Override
    public Page<ThuongHieuDisplayReponse> getAllThuongHieuforDisplay(Pageable pageable) {
        Page<ThuongHieu> ThuongHieuPage = ThuongHieuRepository.findAll(pageable);
        List<ThuongHieuDisplayReponse> romDisplayReponses = ThuongHieuPage.getContent().stream()
                .map(thuongHieuMapper::getAlldisplayThuongHieu).collect(Collectors.toList());

        return new PageImpl<>(romDisplayReponses , pageable, ThuongHieuPage.getTotalElements());
    }

    @Override
    public ThuongHieu addThuongHieu(ThuongHieuAddRequestDTO adddto) {
        ThuongHieu ThuongHieu = thuongHieuMapper.toThuongHieu(adddto);
        return ThuongHieuRepository.save(ThuongHieu);
    }

    @Override
    public ThuongHieu updateThuongHieu(ThuongHieuUpdateRequestDTO updatedto) {
        ThuongHieu  existing = ThuongHieuRepository.findById(updatedto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Đồ Họa với ID: " + updatedto.getId()));

        // 2. Cập nhật dữ liệu từ DTO vào entity cũ
        thuongHieuMapper.updateThuongHieu(existing, updatedto);

        // 3. Lưu lại bản ghi đã cập nhật
        return ThuongHieuRepository.save(existing);
    }

    @Override
    public ThuongHieuDisplayReponse getDetailedThuongHieu(UUID id) {
        ThuongHieu hdh = ThuongHieuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy do hoa với ID: " + id));
        return thuongHieuMapper.toThuongHieuDisplayReponse(hdh);
    }
}
