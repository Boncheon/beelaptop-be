package com.example.sever.service.impl;

import com.example.sever.enity.DoHoa;
import com.example.sever.repository.DoHoaRepository;
import com.example.sever.service.DoHoaService;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class DoHoaServiceImpl implements DoHoaService {

    DoHoaRepository doHoaRepository;

    @Override
    public List<DoHoa> getAllDoHoaforDisplayPage(Pageable pageable) {
        return  doHoaRepository.findAll();
    }
}
