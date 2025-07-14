package com.example.sever.service;

import com.example.sever.dto.request.PhienBanAddRequestDTO;
import com.example.sever.dto.request.PhienBanBatchCreateDTO;
import com.example.sever.dto.request.PhienBanUpdateRequestDTO;
import com.example.sever.dto.response.PhienBanDisplayReponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PhienBanService {
    Page<PhienBanDisplayReponse> getAllPhienBanforDisplay(Pageable pageable);

    PhienBanDisplayReponse updatePhienBan(PhienBanUpdateRequestDTO dto);
    List<PhienBanDisplayReponse> generatePhienBanBienThe(PhienBanBatchCreateDTO request);

}