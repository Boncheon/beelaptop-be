package com.example.sever.mapper;

import com.example.sever.dto.request.ManHinhAddRequestDTO;
import com.example.sever.dto.request.ManHinhUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.ManHinhDisplayReponse;
import com.example.sever.enity.ManHinh;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ManHinhMapper {

    ManHinh toManHinh(ManHinhAddRequestDTO request);

    ManHinhDisplayReponse getAlldisplayManHinh(ManHinh cpu);

    void updateManHinh(@MappingTarget ManHinh cpu, ManHinhUpdateRequestDTO request);
}
