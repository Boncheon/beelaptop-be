package com.example.sever.service;



import com.example.sever.dto.DotGiamGia.DotGiamGiaChiTietDTO;
import com.example.sever.entity.DotGiamGiaChiTiet;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DotGiamGiaChiTietService {

    DotGiamGiaChiTiet create(DotGiamGiaChiTietDTO dto);

    List<DotGiamGiaChiTiet> getByDot(UUID idDot);

    void delete(UUID id);

}
