package com.example.sever.service;

import com.example.sever.dto.request.PinAddRequestDTO;
import com.example.sever.dto.request.PinUpdateRequestDTO;
import com.example.sever.dto.response.PinDisplayReponse;
import com.example.sever.entity.Pin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PinService {
    Page<PinDisplayReponse> getAllPinforDisplay(Pageable pageable);
    Pin addPin(PinAddRequestDTO adddto);
    Pin updatePin(PinUpdateRequestDTO updatedto);
}
