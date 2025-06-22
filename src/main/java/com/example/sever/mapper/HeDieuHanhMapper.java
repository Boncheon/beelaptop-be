package com.example.sever.mapper;

import com.example.sever.dto.request.HeDieuHanhAddRequestDTO;
import com.example.sever.dto.request.HeDieuHanhUpdateRequestDTO;
import com.example.sever.dto.response.HeDieuHanhDisplayReponse;
import com.example.sever.enity.HeDieuHanh;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HeDieuHanhMapper {

    HeDieuHanh toHeDieuHanh(HeDieuHanhAddRequestDTO request);

    HeDieuHanhDisplayReponse getAlldisplayHeDieuHanh(HeDieuHanh hedieuhanh);

    void updateHeDieuHanh(@MappingTarget HeDieuHanh hedieuhanh, HeDieuHanhUpdateRequestDTO request);
}
