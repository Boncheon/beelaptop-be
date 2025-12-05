package com.example.sever.service;

import com.example.sever.dto.request.SeriAddRequestDTO;
import com.example.sever.dto.request.SeriUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.SeriDisplayReponse;
import com.example.sever.entity.Seri;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface SeriService {
    void addListSeri(SeriAddRequestDTO dto);

    List<SeriDisplayReponse> getByLaptopCt(UUID idLaptopCt);
    void updateSeri(SeriUpdateRequestDTO dto);
    List<SeriDisplayReponse> getAll();

    SeriDisplayReponse getDetail(UUID id);
}