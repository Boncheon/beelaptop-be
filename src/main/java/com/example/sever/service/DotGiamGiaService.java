package com.example.sever.service;

import com.example.sever.dto.DotGiamGia.DotGiamGiaDTO;
import com.example.sever.entity.DotGiamGia;

import java.util.List;
import java.util.UUID;

public interface DotGiamGiaService {

    DotGiamGia create(DotGiamGiaDTO dto);

    DotGiamGia update(UUID id, DotGiamGiaDTO dto);

    List<DotGiamGia> getAll();

    DotGiamGia getById(UUID id);

    void delete(UUID id);


}
