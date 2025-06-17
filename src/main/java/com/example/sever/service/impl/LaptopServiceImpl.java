package com.example.sever.service.impl;

import com.example.sever.dto.response.LaptopDisplayResponseDTO;
import com.example.sever.service.LaptopService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

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
