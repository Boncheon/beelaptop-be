package com.example.sever.mapper;

import com.example.sever.dto.request.LaptopAddRequestDTO;
import com.example.sever.dto.request.LaptopUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.LapTopDisplayReponse;
import com.example.sever.entity.Laptop;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LapTopMapper {

    @Mapping(source = "idLaptop", target = "idLaptop")
    @Mapping(source = "tenSanPham", target = "tenSanPham")
    @Mapping(source = "moTa", target = "moTa")
    LapTopDisplayReponse getAlldisplayLapTop(Laptop laptop);

    Laptop toDolaptop(LaptopAddRequestDTO addRequestDTO);

    void updateDisplayLapTop(@MappingTarget Laptop laptop, LaptopUpdateRequestDTO updateRequestDTO);

    void updateStatusLapTop(@MappingTarget Laptop laptop, StatusRequestDTO request);
}
