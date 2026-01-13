package com.example.sever.service;

import com.example.sever.dto.request.ManHinhAddRequestDTO;
import com.example.sever.dto.request.ManHinhUpdateRequestDTO;
import com.example.sever.dto.response.HeDieuHanhDisplayReponse;
import com.example.sever.dto.response.ManHinhDisplayReponse;
import com.example.sever.entity.ManHinh;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ManHinhService {
    Page<ManHinhDisplayReponse> getAllManHinhforDisplay(Pageable pageable);
    ManHinh addManHinh(ManHinhAddRequestDTO adddto);
    ManHinh updateManHinh(ManHinhUpdateRequestDTO updatedto);
    ManHinhDisplayReponse getDetailedManHinh(UUID id);

    Page<ManHinhDisplayReponse> getTrangThaiCpuforDisplay(Pageable pageable);
}
