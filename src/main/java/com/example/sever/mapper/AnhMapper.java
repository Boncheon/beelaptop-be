package com.example.sever.mapper;

import com.example.sever.dto.request.AnhAddRequestDTO;
import com.example.sever.dto.response.AnhDisplayReponse;
import com.example.sever.dto.response.CpuDisplayReponse;
import com.example.sever.entity.Anh;
import com.example.sever.entity.Cpu;
import com.example.sever.entity.LaptopChiTiet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface AnhMapper {

    // ===== ADD: map từ DTO -> Entity =====
    @Mapping(target = "id", ignore = true)          // tự set trong service
    @Mapping(target = "idAnh", ignore = true)       // sinh trong service (anh0001,...)
    @Mapping(target = "imgURL", ignore = true)      // set sau khi upload Cloudinary
    @Mapping(
            target = "idLaptopChiTiet",
            expression = "java(refLaptopChiTiet(request.getIdLaptopChiTiet()))"
    )
    Anh toEntity(AnhAddRequestDTO request);

    // ===== RESPONSE: Entity -> DTO hiển thị =====
    @Mapping(target = "idLaptopChiTiet", source = "idLaptopChiTiet.id")
    AnhDisplayReponse toResponse(Anh entity);

    // ===== helper: tạo reference LaptopChiTiet chỉ với id =====
    default LaptopChiTiet refLaptopChiTiet(UUID id) {
        if (id == null) return null;
        LaptopChiTiet ct = new LaptopChiTiet();
        ct.setId(id);
        return ct;
    }
}
