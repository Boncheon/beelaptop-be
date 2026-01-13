package com.example.sever.service.impl;



import com.example.sever.dto.request.RamAddRequestDTO;
import com.example.sever.dto.request.RamUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;

import com.example.sever.dto.response.RamDIsplayReponse;
import com.example.sever.entity.Ram;
import com.example.sever.mapper.RamMapper;
import com.example.sever.repository.RamRepository;
import com.example.sever.service.RamService;
import com.example.sever.specification.RamSpecification;
import org.springframework.beans.factory.annotation.Autowired;
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
public class RamServiceImpl implements RamService {
    @Autowired
   RamRepository ramRepository;
    @Autowired
    RamMapper ramMapper;

    @Override
    public Page<RamDIsplayReponse> getAllRamforDisplay(Pageable pageable) {
        Page<Ram> RamPage = ramRepository.findAll(pageable);
        List<RamDIsplayReponse> ramDIsplayReponses = RamPage.getContent().stream()
                .map(ramMapper::getAlldisplayRam).collect(Collectors.toList());

        return new PageImpl<>(ramDIsplayReponses , pageable, RamPage.getTotalElements());
    }
    @Override
    public Page<RamDIsplayReponse> getTrangThaiCpuforDisplay(Pageable pageable) {
        Page<Ram> ramPage = ramRepository.findByTrangThai(1, pageable);

        List<RamDIsplayReponse> responses = ramPage.getContent()
                .stream()
                .map(ramMapper::getAlldisplayRam)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, ramPage.getTotalElements());
    }
    @Override
    public Ram addRam(RamAddRequestDTO adddto) {
        Ram ram = ramMapper.toDoram(adddto);

        // Nếu chưa set trạng thái thì mặc định = 1 (Hoạt động)
        if (ram.getTrangThai() == null) {
            ram.setTrangThai(1);
        }

        Instant now = Instant.now();
        ram.setNgayTao(now);
        ram.setNgaySua(now);

        return ramRepository.save(ram);
    }

    @Override
    public Ram updateRam(RamUpdateRequestDTO updatedto) {
        // 1. Lấy bản ghi hiện có từ DB
        Ram existing = ramRepository.findById(updatedto.getId())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy RAM với ID: " + updatedto.getId())
                );

        // 2. Cập nhật dữ liệu từ DTO vào entity cũ
        ramMapper.updateDisplayRam(existing, updatedto);

        // 2.1 đảm bảo trạng thái được cập nhật nếu DTO có gửi lên
        if (updatedto.getTrangThai() != null) {
            existing.setTrangThai(updatedto.getTrangThai());
        }

        // 3. Cập nhật ngày sửa
        existing.setNgaySua(Instant.now());

        // 4. Lưu lại bản ghi đã cập nhật
        return ramRepository.save(existing);
    }
    @Override
    public Ram updateStatus(StatusRequestDTO updatedto) {
        Ram existing = ramRepository.findById(updatedto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Đồ Họa với ID: " + updatedto.getId()));
        //cap nhap stattus
        if(existing.getTrangThai()==0){
            updatedto.setTrangThai(1);
        }else {
            updatedto.setTrangThai(0);
        }
        // 2. Cập nhật dữ liệu từ DTO vào entity cũ
        ramMapper.updateStatusRam(existing, updatedto);

        // 3. Lưu lại bản ghi đã cập nhật
        return ramRepository.save(existing);
    }

    @Override
    public Page<RamDIsplayReponse> getRamByFilter(Integer trangThai, String keyword, Pageable pageable) {
        Specification<Ram> spec = RamSpecification.filterByKeywordAndTrangThai(keyword, trangThai);
        Page<Ram> ramPage = ramRepository.findAll(spec, pageable);

        List<RamDIsplayReponse> result = ramPage.getContent().stream()
                .map(ramMapper::getAlldisplayRam)
                .collect(Collectors.toList());

        return new PageImpl<>(result, pageable, ramPage.getTotalElements());
    }

    @Override
    public RamDIsplayReponse getDetailedRam(UUID id) {
        Ram ram = ramRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy CPU với ID: " + id));
        return ramMapper.getAlldisplayRam(ram);
    }
}
