package com.example.sever.mapper;

import com.example.sever.dto.request.HeDieuHanhAddRequestDTO;
import com.example.sever.dto.request.HeDieuHanhUpdateRequestDTO;
import com.example.sever.dto.response.HeDieuHanhDisplayReponse;
import com.example.sever.entity.HeDieuHanh;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface HeDieuHanhMapper {

    // Add
    HeDieuHanh toHeDieuHanh(HeDieuHanhAddRequestDTO request);

    // Detail
    HeDieuHanhDisplayReponse toHeDieuHanhDisplayReponse(HeDieuHanh hedieuhanh);

    // List
    HeDieuHanhDisplayReponse getAlldisplayHeDieuHanh(HeDieuHanh hedieuhanh);

    // Update (ignore field = null, giữ nguyên giá trị cũ)
    void updateHeDieuHanh(@MappingTarget HeDieuHanh hedieuhanh,
                          HeDieuHanhUpdateRequestDTO request);
}
