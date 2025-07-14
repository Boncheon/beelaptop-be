package com.example.sever.service;

import com.example.sever.dto.request.LaptopAddRequestDTO;
import com.example.sever.dto.request.LaptopUpdateRequestDTO;
import com.example.sever.dto.request.SanPhamFullCreateDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.LapTopCTDisplayReponse;
import com.example.sever.dto.response.LapTopDisplayReponse;
import com.example.sever.entity.Laptop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;
import java.util.Map;

/**
 * Interface định nghĩa các phương thức service cho Laptop
 */
public interface LaptopService {
    Page<LapTopDisplayReponse> getAllLapTopforDisplay(Pageable pageable);
    Laptop addLapTop(LaptopAddRequestDTO adddto);
    Laptop updateLapTop(LaptopUpdateRequestDTO updatedto);
    UUID createSanPhamHoanChinh(SanPhamFullCreateDTO dto);

    //    Laptop updateStatus(StatusRequestDTO updatedto);
//    Page<LapTopDisplayReponse> getLapTopByFilter(Integer trangThai, String keyword, Pageable pageable);
    List<LapTopCTDisplayReponse> getDetailedLapTop(UUID idLaptop);

}