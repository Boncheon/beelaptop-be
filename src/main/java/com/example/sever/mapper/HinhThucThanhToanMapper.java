package com.example.sever.mapper;

import com.example.sever.dto.HinhThucThanhToanDTO;
import com.example.sever.entity.HinhThucThanhToan;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HinhThucThanhToanMapper {

    HinhThucThanhToanDTO toDto(HinhThucThanhToan entity);

    List<HinhThucThanhToanDTO> toDtoList(List<HinhThucThanhToan> entities);
}