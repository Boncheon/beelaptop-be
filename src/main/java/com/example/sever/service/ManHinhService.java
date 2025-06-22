package com.example.sever.service;

import com.example.sever.dto.request.ManHinhAddRequestDTO;
import com.example.sever.dto.request.ManHinhUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.ManHinhDisplayReponse;
import com.example.sever.enity.ManHinh;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ManHinhService {
    Page<ManHinhDisplayReponse> getAllManHinhforDisplay(Pageable pageable);
    ManHinh addManHinh(ManHinhAddRequestDTO adddto);
    ManHinh updateManHinh(ManHinhUpdateRequestDTO updatedto);
}
