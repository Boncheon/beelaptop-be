package com.example.sever.service;

import com.example.sever.dto.request.ThuongHieuAddRequestDTO;
import com.example.sever.dto.request.ThuongHieuUpdateRequestDTO;
import com.example.sever.dto.response.ThuongHieuDisplayReponse;
import com.example.sever.entity.ThuongHieu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ThuongHieuService {
    Page<ThuongHieuDisplayReponse> getAllThuongHieuforDisplay(Pageable pageable);
    ThuongHieu addThuongHieu(ThuongHieuAddRequestDTO adddto);
    ThuongHieu updateThuongHieu(ThuongHieuUpdateRequestDTO updatedto);
    ThuongHieuDisplayReponse getDetailedThuongHieu(UUID id);
}
