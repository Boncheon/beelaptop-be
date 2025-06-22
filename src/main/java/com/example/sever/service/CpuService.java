package com.example.sever.service;

import com.example.sever.dto.request.CpuAddRequestDTO;
import com.example.sever.dto.request.CpuUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.CpuDisplayReponse;
import com.example.sever.enity.Cpu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CpuService {
    Page<CpuDisplayReponse> getAllCpuforDisplay(Pageable pageable);
    Cpu addCpu(CpuAddRequestDTO adddto);
    Cpu updateCpu(CpuUpdateRequestDTO updatedto);
    Cpu updateStatus(StatusRequestDTO updatedto);
    Page<CpuDisplayReponse> getCpuByFilter(Integer trangThai, String keyword, Pageable pageable);
}
