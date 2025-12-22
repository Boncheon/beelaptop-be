package com.example.sever.mapper;

import com.example.sever.dto.request.ManHinhAddRequestDTO;
import com.example.sever.dto.request.ManHinhUpdateRequestDTO;
import com.example.sever.dto.response.ManHinhDisplayReponse;
import com.example.sever.entity.ManHinh;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ManHinhMapper {

    // Add
    ManHinh toManHinh(ManHinhAddRequestDTO request);

    // List
    ManHinhDisplayReponse getAlldisplayManHinh(ManHinh manHinh);

    // Detail (nếu bạn dùng riêng cho API chi tiết)
    ManHinhDisplayReponse toManHinhDisplayReponse(ManHinh manHinh);

    // Update (ignore field = null, giữ nguyên giá trị cũ)
    void updateManHinh(@MappingTarget ManHinh manHinh,
                       ManHinhUpdateRequestDTO request);
}
