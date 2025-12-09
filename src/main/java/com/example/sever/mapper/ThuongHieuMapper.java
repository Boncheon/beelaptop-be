package com.example.sever.mapper;

import com.example.sever.dto.request.ThuongHieuAddRequestDTO;
import com.example.sever.dto.request.ThuongHieuUpdateRequestDTO;
import com.example.sever.dto.response.ThuongHieuDisplayReponse;
import com.example.sever.entity.ThuongHieu;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ThuongHieuMapper {

    // Add
    ThuongHieu toThuongHieu(ThuongHieuAddRequestDTO request);

    // Detail
    ThuongHieuDisplayReponse toThuongHieuDisplayReponse(ThuongHieu thuongHieu);

    // List
    ThuongHieuDisplayReponse getAlldisplayThuongHieu(ThuongHieu thuongHieu);

    // Update (ignore field = null, giữ nguyên giá trị cũ)
    void updateThuongHieu(@MappingTarget ThuongHieu thuongHieu,
                          ThuongHieuUpdateRequestDTO request);
}
