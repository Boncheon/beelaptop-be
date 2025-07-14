package com.example.sever.service;

import com.example.sever.dto.request.CpuAddRequestDTO;
import com.example.sever.dto.request.CpuUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.CpuDisplayReponse;
import com.example.sever.entity.Cpu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CpuService {
    Page<CpuDisplayReponse> getAllCpuforDisplay(Pageable pageable);
    CpuDisplayReponse getDetailedCpu(UUID id);
    Cpu addCpu(CpuAddRequestDTO adddto);
    Cpu updateCpu(CpuUpdateRequestDTO updatedto);
    Cpu updateStatus(StatusRequestDTO updatedto);
    Page<CpuDisplayReponse> getCpuByFilter(Integer trangThai, String keyword, Pageable pageable);
}
