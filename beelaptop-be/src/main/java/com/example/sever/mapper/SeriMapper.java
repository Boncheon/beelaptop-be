package com.example.sever.mapper;

import com.example.sever.dto.request.SeriAddRequestDTO;
import com.example.sever.dto.request.SeriUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.SeriDisplayReponse;
import com.example.sever.entity.Seri;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SeriMapper {


    Seri toSeri(SeriAddRequestDTO request);

    @Mapping(target = "idPhienBan", source = "phienBan.id")// Ngăn MapStruct tự map object phienBan
    SeriDisplayReponse getAlldisplaySeri(Seri seri);

    void updateSeri(@MappingTarget Seri seri, SeriUpdateRequestDTO request);

    void updateStatusSeri(@MappingTarget Seri seri, StatusRequestDTO request);
}