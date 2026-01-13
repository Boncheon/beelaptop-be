package com.example.sever.service.impl;

import com.example.sever.dto.request.CpuAddRequestDTO;
import com.example.sever.dto.request.CpuUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.CpuDisplayReponse;
import com.example.sever.entity.Cpu;
import com.example.sever.mapper.CpuMapper;
import com.example.sever.repository.CpuRepository;
import com.example.sever.service.CpuService;

import com.example.sever.specification.CpuSpecification;
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
public class CpuServiceImpl implements CpuService {
    CpuRepository cpuRepository;
    CpuMapper cpuMapper;

    @Override
    public Page<CpuDisplayReponse> getAllCpuforDisplay(Pageable pageable) {
        Page<Cpu> CpuPage = cpuRepository.findAll(pageable);
        List<CpuDisplayReponse> romDisplayReponses = CpuPage.getContent().stream()
                .map(cpuMapper::getAlldisplayCpu).collect(Collectors.toList());

        return new PageImpl<>(romDisplayReponses , pageable, CpuPage.getTotalElements());
    }
    @Override
    public Page<CpuDisplayReponse> getTrangThaiCpuforDisplay(Pageable pageable) {
        Page<Cpu> CpuPage = cpuRepository.findByTrangThai(1, pageable);

        List<CpuDisplayReponse> responses = CpuPage.getContent()
                .stream()
                .map(cpuMapper::getAlldisplayCpu)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, CpuPage.getTotalElements());
    }
    @Override
    public CpuDisplayReponse getDetailedCpu(UUID id) {
        Cpu cpu = cpuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy CPU với ID: " + id));
        return cpuMapper.getAlldisplayCpu(cpu); // hoặc dùng getDetailCpu nếu bạn cần thông tin chi tiết hơn
    }

    @Override
    public Cpu addCpu(CpuAddRequestDTO adddto) {
        Cpu cpu = cpuMapper.toCpu(adddto);

        // mặc định trạng thái nếu null
        if (cpu.getTrangThai() == null) {
            cpu.setTrangThai(1);   // 1 = hoạt động
        }

        Instant now = Instant.now();
        cpu.setNgayTao(now);
        cpu.setNgaySua(now);

        return cpuRepository.save(cpu);
    }

    @Override
    public Cpu updateCpu(CpuUpdateRequestDTO updatedto) {
        Cpu existing = cpuRepository.findById(updatedto.getId())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy CPU với ID: " + updatedto.getId())
                );

        // map dữ liệu mới
        cpuMapper.updateCpu(existing, updatedto);

        // 👇 ép lại trạng thái cho chắc chắn
        existing.setTrangThai(updatedto.getTrangThai());

        existing.setNgaySua(Instant.now());
        return cpuRepository.save(existing);
    }

    @Override
    public Cpu updateStatus(StatusRequestDTO updatedto) {
        Cpu existing = cpuRepository.findById(updatedto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Đồ Họa với ID: " + updatedto.getId()));
        //cap nhap stattus
        if(existing.getTrangThai()==0){
            updatedto.setTrangThai(1);
        }else {
            updatedto.setTrangThai(0);
        }
        // 2. Cập nhật dữ liệu từ DTO vào entity cũ
        cpuMapper.updateStatusCpu(existing, updatedto);

        // 3. Lưu lại bản ghi đã cập nhật
        return cpuRepository.save(existing);
    }

    @Override
    public Page<CpuDisplayReponse> getCpuByFilter(Integer trangThai, String keyword, Pageable pageable) {
        Specification<Cpu> spec = CpuSpecification.filterByKeywordAndTrangThai(keyword, trangThai);
        Page<Cpu> cpuPage = cpuRepository.findAll(spec, pageable);

        List<CpuDisplayReponse> result = cpuPage.getContent().stream()
                .map(cpuMapper::getAlldisplayCpu)
                .collect(Collectors.toList());

        return new PageImpl<>(result, pageable, cpuPage.getTotalElements());
    }
}
