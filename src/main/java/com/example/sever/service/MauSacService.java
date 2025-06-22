package com.example.sever.service;

import com.example.sever.dto.request.MauSacAddRequestDTO;
import com.example.sever.dto.request.MauSacUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.MauSacDisplayReponse;
import com.example.sever.enity.MauSac;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MauSacService {
    Page<MauSacDisplayReponse> getAllMauSacforDisplay(Pageable pageable);
    MauSac addMauSac(MauSacAddRequestDTO adddto);
    MauSac updateMauSac(MauSacUpdateRequestDTO updatedto);
    MauSac updateStatus(StatusRequestDTO updatedto);
    Page<MauSacDisplayReponse> getMauSacByFilter(Integer trangThai, String keyword, Pageable pageable);
}
