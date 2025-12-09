package com.example.sever.service.impl;

import com.example.sever.dto.HinhThucThanhToanDTO;
import com.example.sever.entity.HinhThucThanhToan;
import com.example.sever.mapper.HinhThucThanhToanMapper;
import com.example.sever.repository.HinhThucThanhToanRepository;
import com.example.sever.service.HinhThucThanhToanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HinhThucThanhToanServiceImpl implements HinhThucThanhToanService {

    private final HinhThucThanhToanRepository repository;
    private final HinhThucThanhToanMapper mapper;

    @Override
    public List<HinhThucThanhToanDTO> getAll() {
        List<HinhThucThanhToan> entities = repository.findAll();
        return mapper.toDtoList(entities);
    }


}