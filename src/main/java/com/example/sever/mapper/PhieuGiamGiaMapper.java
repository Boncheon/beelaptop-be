package com.example.sever.mapper;

import com.example.sever.dto.PhieuGiamGiaDTO.PhieuGiamGiaDto;
import com.example.sever.enity.PhieuGiamGia;

public class PhieuGiamGiaMapper {

    public static PhieuGiamGiaDto mapTopVoucher(PhieuGiamGia phieuGiamGia){

        return new PhieuGiamGiaDto(


                phieuGiamGia.getIdPhieugiamgia(),
                phieuGiamGia.getTen(),
                phieuGiamGia.getSoLuong(),
                phieuGiamGia.getKieuGiamGia(),
                phieuGiamGia.getGiaTriGiam(),
                phieuGiamGia.getNgayBatDau(),
                phieuGiamGia.getNgayKetThuc(),
                phieuGiamGia.getGiaTriMin(),
                phieuGiamGia.getGiaTriMax(),
                phieuGiamGia.getMoTa(),
                phieuGiamGia.getTrangThai()

        );



    }


    public static PhieuGiamGia mapTopVoucher(PhieuGiamGiaDto phieuGiamGiadto){

        return new PhieuGiamGia(
                phieuGiamGiadto.getIdPhieugiamgia(),
                phieuGiamGiadto.getTen(),
                phieuGiamGiadto.getSoLuong(),
                phieuGiamGiadto.getKieuGiamGia(),
                phieuGiamGiadto.getGiaTriGiam(),
                phieuGiamGiadto.getNgayBatDau(),
                phieuGiamGiadto.getNgayKetThuc(),
                phieuGiamGiadto.getGiaTriMin(),
                phieuGiamGiadto.getGiaTriMax(),
                phieuGiamGiadto.getMoTa(),
                phieuGiamGiadto.getTrangThai()

        );


    }





}
