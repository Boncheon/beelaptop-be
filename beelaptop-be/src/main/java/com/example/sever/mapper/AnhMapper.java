package com.example.sever.mapper;

import com.example.sever.dto.request.AnhAddRequestDTO;
import com.example.sever.dto.response.AnhDisplayReponse;
import com.example.sever.dto.response.CpuDisplayReponse;
import com.example.sever.entity.Anh;
import com.example.sever.entity.Cpu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AnhMapper {

    AnhMapper INSTANCE = Mappers.getMapper(AnhMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imgURL", ignore = true)
    @Mapping(target = "idLaptopChiTiet", ignore = true) // vẫn cần ignore nếu có trong entity
    Anh toEntity(AnhAddRequestDTO request);
    AnhDisplayReponse toResponse(Anh entity);
}
