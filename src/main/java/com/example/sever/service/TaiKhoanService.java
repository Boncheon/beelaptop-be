package com.example.sever.service;

import com.example.sever.dto.request.TaiKhoanAddRequestDTO;
import com.example.sever.dto.request.TaiKhoanUpdateRequestDTO;
import com.example.sever.dto.response.TaiKhoanDisplayReponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaiKhoanService {
    Page<TaiKhoanDisplayReponse> getAllTaiKhoanforDisplay(Pageable pageable);
    TaiKhoanDisplayReponse addTaiKhoan(TaiKhoanAddRequestDTO adddto);
    TaiKhoanDisplayReponse updateTaiKhoan(TaiKhoanUpdateRequestDTO updatedto);
}
