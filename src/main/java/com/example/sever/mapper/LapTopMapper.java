package com.example.sever.mapper;

import com.example.sever.dto.request.LaptopAddRequestDTO;
import com.example.sever.dto.request.LaptopUpdateRequestDTO;
import com.example.sever.dto.response.LapTopDisplayReponse;
import com.example.sever.dto.response.LaptopResponseDTO;
import com.example.sever.entity.*;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface LapTopMapper {

    // ================= ADD ====================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ngayTao", ignore = true)
    @Mapping(target = "ngaySua", ignore = true)

    @Mapping(target = "idThuonghieu", expression = "java(refThuongHieu(dto.getIdThuongHieu()))")
    @Mapping(target = "idManHinh", expression = "java(refManHinh(dto.getIdManHinh()))")
    @Mapping(target = "idKichThuoc", expression = "java(refKichThuoc(dto.getIdKichThuoc()))")
    @Mapping(target = "idHeDieuHanh", expression = "java(refHeDieuHanh(dto.getIdHeDieuHanh()))")
    @Mapping(target = "idPin", expression = "java(dto.getIdPin() != null ? refPin(dto.getIdPin()) : null)")
    Laptop toEntity(LaptopAddRequestDTO dto);


    // =============== RESPONSE LIST BASIC ============
    @Mapping(target = "tenThuongHieu", source = "idThuonghieu.ten")
    LaptopResponseDTO toResponse(Laptop entity);


    // =============== RESPONSE DETAIL FULL ============
    @Mapping(target = "tenThuongHieu", source = "idThuonghieu.ten")
    @Mapping(target = "tenManHinh", source = "idManHinh.doPhanGiai")
    @Mapping(target = "tenPin", source = "idPin.dungLuong")
    @Mapping(target = "tenKichThuoc", expression = "java(buildKichThuocString(entity.getIdKichThuoc()))")
    @Mapping(target = "tenHeDieuHanh", source = "idHeDieuHanh.ten")

    // map ID đúng chuẩn
    @Mapping(target = "idThuongHieu", source = "idThuonghieu.id")
    @Mapping(target = "idManHinh", source = "idManHinh.id")
    @Mapping(target = "idPin", source = "idPin.id")
    @Mapping(target = "idKichThuoc", source = "idKichThuoc.id")
    @Mapping(target = "idHeDieuHanh", source = "idHeDieuHanh.id")

    LapTopDisplayReponse getAlldisplayLapTop(Laptop entity);


    // =============== UPDATE =======================
    // =============== UPDATE =======================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idThuonghieu", expression = "java(refThuongHieu(dto.getIdThuongHieu()))")
    @Mapping(target = "idManHinh", expression = "java(refManHinh(dto.getIdManHinh()))")
    @Mapping(target = "idKichThuoc", expression = "java(refKichThuoc(dto.getIdKichThuoc()))")
    @Mapping(target = "idHeDieuHanh", expression = "java(refHeDieuHanh(dto.getIdHeDieuHanh()))")
    @Mapping(target = "idPin",
            expression = "java(dto.getIdPin() != null ? refPin(dto.getIdPin()) : null)")
    void updateEntity(@MappingTarget Laptop entity, LaptopUpdateRequestDTO dto);



    // =============== HELPERS ======================
    default ThuongHieu refThuongHieu(UUID id) { if (id==null) return null; var e=new ThuongHieu(); e.setId(id); return e; }
    default ManHinh refManHinh(UUID id) { if (id==null) return null; var e=new ManHinh(); e.setId(id); return e; }
    default KichThuoc refKichThuoc(UUID id) { if (id==null) return null; var e=new KichThuoc(); e.setId(id); return e; }
    default HeDieuHanh refHeDieuHanh(UUID id) { if (id==null) return null; var e=new HeDieuHanh(); e.setId(id); return e; }
    default Pin refPin(UUID id) { if (id==null) return null; var e=new Pin(); e.setId(id); return e; }

    default String buildKichThuocString(KichThuoc k) {
        if (k == null) return null;
        return k.getChieuDai() + " x " + k.getChieuRong() + " x " + k.getChieuCao();
    }
}

