package com.example.sever.mapper;

import com.example.sever.dto.request.MauSacAddRequestDTO;
import com.example.sever.dto.request.MauSacUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.MauSacDisplayReponse;
import com.example.sever.enity.MauSac;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MauSacMapper {

    MauSac toMauSac(MauSacAddRequestDTO request);

    MauSacDisplayReponse getAlldisplayMauSac(MauSac cpu);

    void updateMauSac(@MappingTarget MauSac cpu, MauSacUpdateRequestDTO request);
    void updateStatusMauSac(@MappingTarget MauSac cpu, StatusRequestDTO request);
}
