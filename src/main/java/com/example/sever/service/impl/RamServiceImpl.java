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
    public Ram addRam(RamAddRequestDTO adddto) {
        Ram ram = ramMapper.toDoram(adddto);
        return ramRepository.save(ram);
    }

    @Override
    public Ram updateRam(RamUpdateRequestDTO updatedto) {
        // 1. Lấy bản ghi hiện có từ DB
         Ram  existing = ramRepository.findById(updatedto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Đồ Họa với ID: " + updatedto.getId()));

        // 2. Cập nhật dữ liệu từ DTO vào entity cũ
        ramMapper.updateDisplayRam(existing, updatedto);

        // 3. Lưu lại bản ghi đã cập nhật
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
