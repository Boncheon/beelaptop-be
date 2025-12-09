package com.example.sever.service;

import com.example.sever.dto.HinhThucThanhToanDTO;
import com.example.sever.repository.HinhThucThanhToanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import com.example.sever.dto.HinhThucThanhToanDTO;

import java.util.List;

public interface HinhThucThanhToanService {

    // Lấy tất cả (nếu cần)
    List<HinhThucThanhToanDTO> getAll();

    // Lấy các phương thức đang hoạt động (trangThai = 1)

}