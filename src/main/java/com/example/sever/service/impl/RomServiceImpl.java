package com.example.sever.service.impl;

import com.example.sever.dto.request.RomAddRequestDTO;
import com.example.sever.dto.request.RomUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.RomDisplayReponse;
import com.example.sever.entity.Rom;
import com.example.sever.mapper.RomMapper;
import com.example.sever.repository.RomRepository;
import com.example.sever.service.RomService;
import com.example.sever.specification.RomSpecification;
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
public class RomServiceImpl implements RomService {

    RomRepository romRepository;
    RomMapper romMapper;

    @Override
    public Page<RomDisplayReponse> getAllRomforDisplay(Pageable pageable) {
        Page<Rom> RomPage = romRepository.findAll(pageable);
        List<RomDisplayReponse> romDisplayReponses = RomPage.getContent().stream()
                .map(romMapper::getAlldisplayRom).collect(Collectors.toList());

        return new PageImpl<>(romDisplayReponses , pageable, RomPage.getTotalElements());
    }

    @Override
    public Rom addRom(RomAddRequestDTO adddto) {
        Rom rom = romMapper.toDorom(adddto);
        return romRepository.save(rom);
    }

    @Override
    public Rom updateRom(RomUpdateRequestDTO updatedto) {
        Rom  existing = romRepository.findById(updatedto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Đồ Họa với ID: " + updatedto.getId()));

        // 2. Cập nhật dữ liệu từ DTO vào entity cũ
        romMapper.updateDisplayRom(existing, updatedto);

        // 3. Lưu lại bản ghi đã cập nhật
        return romRepository.save(existing);
    }

    @Override
    public Rom updateStatus(StatusRequestDTO updatedto) {
        Rom existing = romRepository.findById(updatedto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Đồ Họa với ID: " + updatedto.getId()));
        //cap nhap stattus
        if(existing.getTrangThai()==0){
            updatedto.setTrangThai(1);
        }else {
            updatedto.setTrangThai(0);
        }
        // 2. Cập nhật dữ liệu từ DTO vào entity cũ
        romMapper.updateStatusRom(existing, updatedto);

        // 3. Lưu lại bản ghi đã cập nhật
        return romRepository.save(existing);
    }

    @Override
    public Page<RomDisplayReponse> getRomByFilter(Integer trangThai, String keyword, Pageable pageable) {
        Specification<Rom> spec = RomSpecification.filterByKeywordAndTrangThai(keyword, trangThai);
        Page<Rom> romPage = romRepository.findAll(spec, pageable);

        List<RomDisplayReponse> result = romPage.getContent().stream()
                .map(romMapper::getAlldisplayRom)
                .collect(Collectors.toList());

        return new PageImpl<>(result, pageable, romPage.getTotalElements());
    }

    @Override
    public RomDisplayReponse getDetailedRom(UUID id) {
        Rom rom = romRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy CPU với ID: " + id));
        return romMapper.getAlldisplayRom(rom);
    }
}
