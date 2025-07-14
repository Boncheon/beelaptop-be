package com.example.sever.mapper;

import com.example.sever.dto.response.HeDieuHanhDisplayReponse;
import com.example.sever.dto.response.KichThuocDisplayReponse;
import com.example.sever.dto.response.ManHinhDisplayReponse;
import com.example.sever.dto.response.LapTopCTDisplayReponse;
import com.example.sever.dto.response.PinDisplayReponse;
import com.example.sever.entity.HeDieuHanh;
import com.example.sever.entity.KichThuoc;
import com.example.sever.entity.LaptopChiTiet;
import com.example.sever.entity.ManHinh;

import com.example.sever.entity.Pin;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LapTopCTMapper {

    @Mapping(target = "manHinh", expression = "java(entity.getIdManHinh() != null ? entity.getIdManHinh().getKichThuoc() + \" inch - \" + entity.getIdManHinh().getTanSoQuet() + \"Hz - \" + entity.getIdManHinh().getDoPhanGiai() : null)")
    @Mapping(target = "pin", expression = "java(entity.getIdPin() != null ? entity.getIdPin().getDungLuong() : null)")
    @Mapping(target = "kichThuoc", expression = "java(entity.getIdKichThuoc() != null ? entity.getIdKichThuoc().getChieuDai() + \" x \" + entity.getIdKichThuoc().getChieuRong() + \" x \" + entity.getIdKichThuoc().getChieuCao() + \" cm - \" + entity.getIdKichThuoc().getKhoiLuong() + \"kg\" : null)")
    @Mapping(target = "heDieuHanh", expression = "java(entity.getIdHeDieuHanh() != null ? entity.getIdHeDieuHanh().getTen() + \" - phiên bản \" + entity.getIdHeDieuHanh().getPhienBan() : null)")
    @Mapping(source = "idLapTop.id", target = "idLaptop")
    LapTopCTDisplayReponse getAlldisplayLapTopCT(LaptopChiTiet entity);

}

