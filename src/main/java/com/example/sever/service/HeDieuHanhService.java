package com.example.sever.service;

import com.example.sever.dto.request.HeDieuHanhAddRequestDTO;
import com.example.sever.dto.request.HeDieuHanhUpdateRequestDTO;
import com.example.sever.dto.response.HeDieuHanhDisplayReponse;
import com.example.sever.enity.HeDieuHanh;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HeDieuHanhService {
    Page<HeDieuHanhDisplayReponse> getAllHeDieuHanhforDisplay(Pageable pageable);
    HeDieuHanh addHeDieuHanh(HeDieuHanhAddRequestDTO adddto);
    HeDieuHanh updateHeDieuHanh(HeDieuHanhUpdateRequestDTO updatedto);
}
