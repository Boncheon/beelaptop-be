package com.example.sever.service;

import com.example.sever.dto.request.DoHoaAddRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.request.DoHoaUpdateRequestDTO;
import com.example.sever.dto.response.DoHoaDisplayReponse;
import com.example.sever.entity.DoHoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DoHoaService {
    Page<DoHoaDisplayReponse> getAllDoHoaforDisplayPage (Pageable pageable);
    DoHoa addDohoa(DoHoaAddRequestDTO adddto);
    DoHoa updateDohoa(DoHoaUpdateRequestDTO updatedto);
    DoHoa updateStatus(StatusRequestDTO updatedto);
    Page<DoHoaDisplayReponse> getDoHoaByFilter(Integer trangThai, String keyword, Pageable pageable);
    DoHoaDisplayReponse getDetailedDoHoa(UUID id);
}
