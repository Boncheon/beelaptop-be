package com.example.sever.mapper;

import com.example.sever.dto.request.RomAddRequestDTO;
import com.example.sever.dto.request.RomUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.RomDisplayReponse;
import com.example.sever.entity.Rom;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RomMapper {
    Rom toDorom(RomAddRequestDTO addRequestDTO);
    RomDisplayReponse getAlldisplayRom(Rom rom);
    void updateDisplayRom(@MappingTarget Rom rom, RomUpdateRequestDTO updateRequestDTO);
    void updateStatusRom(@MappingTarget Rom rom, StatusRequestDTO request);
}
