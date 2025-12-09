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

import java.time.Instant;
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

        // Nếu chưa set trạng thái thì mặc định = 1 (Hoạt động)
        if (rom.getTrangThai() == null) {
            rom.setTrangThai(1);
        }

        Instant now = Instant.now();
        rom.setNgayTao(now);
        rom.setNgaySua(now);

        return romRepository.save(rom);
    }

    @Override
    public Rom updateRom(RomUpdateRequestDTO updatedto) {
        // 1. Lấy bản ghi hiện có từ DB
        Rom existing = romRepository.findById(updatedto.getId())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy ROM với ID: " + updatedto.getId())
                );

        // 2. Cập nhật dữ liệu từ DTO vào entity cũ
        romMapper.updateDisplayRom(existing, updatedto);
        // hoặc nếu mapper của bạn đặt tên khác:
        // romMapper.updateRom(existing, updatedto);

        // 2.1 Đảm bảo trạng thái được cập nhật nếu DTO có gửi lên
        if (updatedto.getTrangThai() != null) {
            existing.setTrangThai(updatedto.getTrangThai());
        }

        // 3. Cập nhật ngày sửa
        existing.setNgaySua(Instant.now());

        // 4. Lưu lại bản ghi đã cập nhật
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
