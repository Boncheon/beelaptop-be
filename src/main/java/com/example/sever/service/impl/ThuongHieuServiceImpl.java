package com.example.sever.service.impl;

import com.example.sever.dto.request.ThuongHieuAddRequestDTO;
import com.example.sever.dto.request.ThuongHieuUpdateRequestDTO;
import com.example.sever.dto.response.CpuDisplayReponse;
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

import java.time.Instant;
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
    public Page<ThuongHieuDisplayReponse> getTrangThaiCpuforDisplay(Pageable pageable) {
        Page<ThuongHieu> CpuPage = ThuongHieuRepository.findByTrangThai(1, pageable);

        List<ThuongHieuDisplayReponse> responses = CpuPage.getContent()
                .stream()
                .map(thuongHieuMapper::getAlldisplayThuongHieu)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, CpuPage.getTotalElements());
    }
    @Override
    public ThuongHieu addThuongHieu(ThuongHieuAddRequestDTO adddto) {
        ThuongHieu thuongHieu = thuongHieuMapper.toThuongHieu(adddto);

        // mặc định trạng thái = 1 nếu chưa set
        if (thuongHieu.getTrangThai() == null) {
            thuongHieu.setTrangThai(1);
        }

        Instant now = Instant.now();
        thuongHieu.setNgayTao(now);
        thuongHieu.setNgaySua(now);

        return ThuongHieuRepository.save(thuongHieu);
    }

    @Override
    public ThuongHieu updateThuongHieu(ThuongHieuUpdateRequestDTO updatedto) {
        ThuongHieu existing = ThuongHieuRepository.findById(updatedto.getId())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy Thương Hiệu với ID: " + updatedto.getId())
                );

        // map các field text
        thuongHieuMapper.updateThuongHieu(existing, updatedto);

        // cập nhật trạng thái nếu DTO gửi lên
        if (updatedto.getTrangThai() != null) {
            existing.setTrangThai(updatedto.getTrangThai());
        }

        // cập nhật ngày sửa
        existing.setNgaySua(Instant.now());

        return ThuongHieuRepository.save(existing);
    }

    @Override
    public ThuongHieuDisplayReponse getDetailedThuongHieu(UUID id) {
        ThuongHieu hdh = ThuongHieuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy do hoa với ID: " + id));
        return thuongHieuMapper.toThuongHieuDisplayReponse(hdh);
    }
}
