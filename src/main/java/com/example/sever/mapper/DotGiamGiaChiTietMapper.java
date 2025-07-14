package com.example.sever.mapper;


import com.example.sever.dto.DotGiamGiaDTO.DotGiamGiaChiTietRequest;
import com.example.sever.dto.DotGiamGiaDTO.DotGiamGiaChiTietResponse;
import com.example.sever.entity.DotGiamGia;
import com.example.sever.entity.DotGiamGiaChiTiet;

public class DotGiamGiaChiTietMapper {

    // Map từ request thành chi tiết entity (dùng khi tạo mới)
    public static DotGiamGiaChiTiet toEntity(DotGiamGiaChiTietRequest request, DotGiamGia dotGiamGia) {
        DotGiamGiaChiTiet entity = new DotGiamGiaChiTiet();
        entity.setKieuGiamGia(request.getKieuGiamGia());
        entity.setGiaTriGiam(request.getGiaTriGiam());
        entity.setDotGiamGia(dotGiamGia);
        return entity;
    }

    // Map từ chi tiết entity thành response DTO (dùng khi trả về cho FE)
    public static DotGiamGiaChiTietResponse toResponse(DotGiamGiaChiTiet chiTiet) {
        DotGiamGia dot = chiTiet.getDotGiamGia();
        return new DotGiamGiaChiTietResponse(
                dot.getIdDotGiamGia(),
                dot.getTenDotGiamGia(),
                dot.getMoTa(),
                dot.getNgayBatDau(),
                dot.getNgayKetThuc(),
                dot.getTrangThai(),
                chiTiet.getKieuGiamGia(),
                chiTiet.getGiaTriGiam()
        );
    }

    public static DotGiamGiaChiTietResponse toBasicResponse(DotGiamGia dot) {
        return new DotGiamGiaChiTietResponse(
                dot.getIdDotGiamGia(),
                dot.getTenDotGiamGia(),
                dot.getMoTa(),
                dot.getNgayBatDau(),
                dot.getNgayKetThuc(),
                dot.getTrangThai(),
                null, // kieuGiamGia
                null  // giaTriGiam
        );
    }



}
