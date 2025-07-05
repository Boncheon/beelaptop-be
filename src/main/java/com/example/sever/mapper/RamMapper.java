package com.example.sever.mapper;

import com.example.sever.dto.request.RamAddRequestDTO;
import com.example.sever.dto.request.RamUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.RamDIsplayReponse;
import com.example.sever.entity.Ram;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface RamMapper {
    Ram toDoram(RamAddRequestDTO addRequestDTO);
    RamDIsplayReponse getAlldisplayRam(Ram ram);
    void updateDisplayRam(@MappingTarget Ram ram, RamUpdateRequestDTO updateRequestDTO);
    void updateStatusRam(@MappingTarget Ram ram, StatusRequestDTO request);
}
