package com.example.sever.service.impl;

import com.example.sever.dto.request.MauSacAddRequestDTO;
import com.example.sever.dto.request.MauSacUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.MauSacDisplayReponse;
import com.example.sever.entity.MauSac;
import com.example.sever.mapper.MauSacMapper;
import com.example.sever.repository.MauSacRepository;
import com.example.sever.service.MauSacService;
import com.example.sever.specification.MauSacSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MauSacServiceImpl implements MauSacService {

    MauSacRepository mausacRepository;
    MauSacMapper mauSacMapper;

    @Override
    public Page<MauSacDisplayReponse> getAllMauSacforDisplay(Pageable pageable) {
        Page<MauSac> MauSacPage = mausacRepository.findAll(pageable);
        List<MauSacDisplayReponse> mausacDisplayReponses = MauSacPage.getContent().stream()
                .map(mauSacMapper::getAlldisplayMauSac).collect(Collectors.toList());

        return new PageImpl<>(mausacDisplayReponses , pageable, MauSacPage.getTotalElements());
    }

    @Override
    public MauSac addMauSac(MauSacAddRequestDTO adddto) {
        MauSac mausac = mauSacMapper.toMauSac(adddto);
        return mausacRepository.save(mausac);
    }

    @Override
    public MauSac updateMauSac(MauSacUpdateRequestDTO updatedto) {
        MauSac  existing = mausacRepository.findById(updatedto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Đồ Họa với ID: " + updatedto.getId()));

        // 2. Cập nhật dữ liệu từ DTO vào entity cũ
        mauSacMapper.updateMauSac(existing, updatedto);

        // 3. Lưu lại bản ghi đã cập nhật
        return mausacRepository.save(existing);
    }

    @Override
    public MauSac updateStatus(StatusRequestDTO updatedto) {
        MauSac existing = mausacRepository.findById(updatedto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Đồ Họa với ID: " + updatedto.getId()));
        //cap nhap stattus
        if(existing.getTrangThai()==0){
            updatedto.setTrangThai(1);
        }else {
            updatedto.setTrangThai(0);
        }
        // 2. Cập nhật dữ liệu từ DTO vào entity cũ
        mauSacMapper.updateStatusMauSac(existing, updatedto);

        // 3. Lưu lại bản ghi đã cập nhật
        return mausacRepository.save(existing);
    }

    @Override
    public Page<MauSacDisplayReponse> getMauSacByFilter(Integer trangThai, String keyword, Pageable pageable) {
        Specification<MauSac> spec = MauSacSpecification.filterByKeywordAndTrangThai(keyword, trangThai);
        Page<MauSac> mausacPage = mausacRepository.findAll(spec, pageable);

        List<MauSacDisplayReponse> result = mausacPage.getContent().stream()
                .map(mauSacMapper::getAlldisplayMauSac)
                .collect(Collectors.toList());

        return new PageImpl<>(result, pageable, mausacPage.getTotalElements());
    }

    @Override
    public MauSacDisplayReponse getDetailedMauSac(UUID id) {
        MauSac mausac = mausacRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy CPU với ID: " + id));
        return mauSacMapper.getAlldisplayMauSac(mausac);
    }
}
