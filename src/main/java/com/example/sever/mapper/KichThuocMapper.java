package com.example.sever.mapper;

import com.example.sever.dto.request.KichThuocAddRequestDTO;
import com.example.sever.dto.request.KichThuocUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.KichThuocDisplayReponse;
import com.example.sever.entity.KichThuoc;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface KichThuocMapper {

    KichThuoc toKichThuoc(KichThuocAddRequestDTO request);
    KichThuocDisplayReponse getAlldisplayKichThuoc(KichThuoc kichthuoc);
    void updateKichThuoc(@MappingTarget KichThuoc kichthuoc, KichThuocUpdateRequestDTO request);
    void updateStatusKichThuoc(@MappingTarget KichThuoc kichthuoc, StatusRequestDTO request);
}
