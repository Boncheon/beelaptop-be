package com.example.sever.service;

import com.example.sever.dto.request.RamAddRequestDTO;
import com.example.sever.dto.request.RamUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.RamDIsplayReponse;
import com.example.sever.entity.Ram;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RamService {
    Page<RamDIsplayReponse> getAllRamforDisplay(Pageable pageable);
    Ram addRam(RamAddRequestDTO adddto);
    Ram updateRam(RamUpdateRequestDTO updatedto);
    Ram updateStatus(StatusRequestDTO updatedto);
    Page<RamDIsplayReponse> getRamByFilter(Integer trangThai, String keyword, Pageable pageable);
    RamDIsplayReponse getDetailedRam(UUID id);
}