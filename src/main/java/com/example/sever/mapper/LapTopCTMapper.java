package com.example.sever.mapper;

import com.example.sever.dto.request.LapTopCTAddRequestDTO;
import com.example.sever.dto.request.LapTopCTUpdateRequestDTO;
import com.example.sever.dto.response.LaptopChiTietResponseDTO;
import com.example.sever.entity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface LapTopCTMapper {

    // ===================== ADD ======================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "idLaptop", ignore = true)

    // Nếu muốn nhận idLaptopCT từ body (AddRequest):
    // dto và entity cùng kiểu (UUID) thì giữ dòng dưới
    @Mapping(target = "idLaptopCT", source = "idLaptopCT")

    // Nếu vẫn set idLaptopCT bằng path param ở service
    // thì đổi thành ignore:
    // @Mapping(target = "idLaptopCT", ignore = true)

    @Mapping(target = "ngayTao", ignore = true)
    @Mapping(target = "ngayCapNhat", ignore = true)
    @Mapping(target = "nguoiTao", ignore = true)
    @Mapping(target = "ghiChu", source = "ghiChu")

    @Mapping(target = "idRam", expression = "java(refRam(dto.getIdRam()))")
    @Mapping(target = "idSsd", expression = "java(refSsd(dto.getIdSsd()))")
    @Mapping(target = "idCpu", expression = "java(refCpu(dto.getIdCpu()))")
    @Mapping(target = "idDohoa", expression = "java(refDohoa(dto.getIdDohoa()))")
    @Mapping(target = "idMauSac", expression = "java(refMauSac(dto.getIdMauSac()))")
    LaptopChiTiet toEntity(LapTopCTAddRequestDTO dto);


    // ===================== UPDATE ======================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idRam", expression = "java(dto.getIdRam() != null ? refRam(dto.getIdRam()) : entity.getIdRam())")
    @Mapping(target = "idSsd", expression = "java(dto.getIdSsd() != null ? refSsd(dto.getIdSsd()) : entity.getIdSsd())")
    @Mapping(target = "idCpu", expression = "java(dto.getIdCpu() != null ? refCpu(dto.getIdCpu()) : entity.getIdCpu())")
    @Mapping(target = "idDohoa", expression = "java(dto.getIdDohoa() != null ? refDohoa(dto.getIdDohoa()) : entity.getIdDohoa())")
    @Mapping(target = "idMauSac", expression = "java(dto.getIdMauSac() != null ? refMauSac(dto.getIdMauSac()) : entity.getIdMauSac())")
    // Thường idLaptopCT không cho update, nên ignore
    // Nếu cho update thì có thể map tương tự các FK khác
    void updateEntity(@MappingTarget LaptopChiTiet entity, LapTopCTUpdateRequestDTO dto);


    // ===================== ENTITY → RESPONSE ======================
    // Thêm map cho idLaptopCT vào Response DTO
    @Mapping(target = "idLaptopCT", source = "idLaptopCT")

    @Mapping(target = "idLaptop", source = "idLaptop.id")
    @Mapping(target = "idRam", source = "idRam.id")
    @Mapping(target = "idSsd", source = "idSsd.id")
    @Mapping(target = "idCpu", source = "idCpu.id")
    @Mapping(target = "idDohoa", source = "idDohoa.id")
    @Mapping(target = "idMauSac", source = "idMauSac.id")

    @Mapping(target = "tenRam", source = "idRam.dungLuongRam")
    @Mapping(target = "tenSsd", source = "idSsd.dungLuongSsd")
    @Mapping(target = "tenCpu", source = "idCpu.ten")
    @Mapping(target = "tenDohoa", source = "idDohoa.tenDayDu")
    @Mapping(target = "tenMauSac", source = "idMauSac.ten")
    LaptopChiTietResponseDTO toResponse(LaptopChiTiet entity);


    // ===================== Helper FK ======================
    default Ram refRam(java.util.UUID id) {
        if (id == null) return null;
        Ram o = new Ram();
        o.setId(id);
        return o;
    }

    default Rom refSsd(java.util.UUID id) {
        if (id == null) return null;
        Rom o = new Rom();
        o.setId(id);
        return o;
    }

    default Cpu refCpu(java.util.UUID id) {
        if (id == null) return null;
        Cpu o = new Cpu();
        o.setId(id);
        return o;
    }

    default DoHoa refDohoa(java.util.UUID id) {
        if (id == null) return null;
        DoHoa o = new DoHoa();
        o.setId(id);
        return o;
    }

    default MauSac refMauSac(java.util.UUID id) {
        if (id == null) return null;
        MauSac o = new MauSac();
        o.setId(id);
        return o;
    }
}
