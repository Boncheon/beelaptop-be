package com.example.sever.mapper;

import com.example.sever.dto.request.CpuAddRequestDTO;
import com.example.sever.dto.request.CpuUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.CpuDisplayReponse;
import com.example.sever.entity.Cpu;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CpuMapper {

    Cpu toCpu(CpuAddRequestDTO request);
    CpuDisplayReponse getAlldisplayCpu(Cpu cpu);
    void updateCpu(@MappingTarget Cpu cpu, CpuUpdateRequestDTO request);
    void updateStatusCpu(@MappingTarget Cpu cpu, StatusRequestDTO request);
}
