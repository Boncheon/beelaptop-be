package com.example.sever.service;

import com.example.sever.dto.request.RomAddRequestDTO;
import com.example.sever.dto.request.RomUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.RomDisplayReponse;
import com.example.sever.enity.Rom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RomService {
    Page<RomDisplayReponse> getAllRomforDisplay(Pageable pageable);
    Rom addRom(RomAddRequestDTO adddto);
    Rom updateRom(RomUpdateRequestDTO updatedto);
    Rom updateStatus(StatusRequestDTO updatedto);
    Page<RomDisplayReponse> getRomByFilter(Integer trangThai, String keyword, Pageable pageable);
}
