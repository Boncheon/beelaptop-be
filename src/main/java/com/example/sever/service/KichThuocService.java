package com.example.sever.service;

import com.example.sever.dto.request.KichThuocAddRequestDTO;
import com.example.sever.dto.request.KichThuocUpdateRequestDTO;
import com.example.sever.dto.response.KichThuocDisplayReponse;
import com.example.sever.entity.KichThuoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface KichThuocService {
    Page<KichThuocDisplayReponse> getAllKichThuocforDisplay(Pageable pageable);
    KichThuoc addKichThuoc(KichThuocAddRequestDTO adddto);
    KichThuoc updateKichThuoc(KichThuocUpdateRequestDTO updatedto);
//    KichThuoc updateStatus(StatusRequestDTO updatedto);
//    Page<KichThuocDisplayReponse> getKichThuocByFilter(Integer trangThai, String keyword, Pageable pageable);
}
