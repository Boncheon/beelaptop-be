package com.example.sever.service;

import com.example.sever.dto.request.SeriAddRequestDTO;
import com.example.sever.dto.request.SeriUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.SeriDisplayReponse;
import com.example.sever.entity.Seri;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SeriService {
    Page<SeriDisplayReponse> getAllSeriforDisplay(Pageable pageable);
    SeriDisplayReponse getDetailedSeri(UUID id);
    Seri addSeri(SeriAddRequestDTO adddto);
    Seri updateSeri(SeriUpdateRequestDTO updatedto);
    Seri updateStatus(StatusRequestDTO updatedto);
//    Page<SeriDisplayReponse> getSeriByFilter(Integer trangThai, String keyword, Pageable pageable);
}
