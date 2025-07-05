package com.example.sever.service.impl;

import com.example.sever.dto.response.LaptopDisplayResponseDTO;
import com.example.sever.service.LaptopService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LaptopServiceImpl implements LaptopService {


    @Override
    public Page<LaptopDisplayResponseDTO> getAllLaptopsForDisplayPaged(Pageable pageable) {
        return null;
    }

    @Override
    public LaptopDisplayResponseDTO getLaptopByIdForDisplay(UUID id) {
        return null;
    }

    @Override
    public Page<LaptopDisplayResponseDTO> searchLaptopsByFilters(String keyword, Boolean trangThai, String thuongHieuTen, String danhMucTen, Pageable pageable) {
        return null;
    }
}
