package com.example.sever.mapper;

import com.example.sever.dto.request.ThuongHieuAddRequestDTO;
import com.example.sever.dto.request.ThuongHieuUpdateRequestDTO;
import com.example.sever.dto.response.ThuongHieuDisplayReponse;
import com.example.sever.entity.ThuongHieu;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ThuongHieuMapper {

    ThuongHieu toThuongHieu(ThuongHieuAddRequestDTO request);
    ThuongHieuDisplayReponse toThuongHieuDisplayReponse(ThuongHieu thuongHieu);

    ThuongHieuDisplayReponse getAlldisplayThuongHieu(ThuongHieu thuongHieu);

    void updateThuongHieu(@MappingTarget ThuongHieu thuongHieu, ThuongHieuUpdateRequestDTO request);
}
