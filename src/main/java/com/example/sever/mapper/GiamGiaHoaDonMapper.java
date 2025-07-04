package com.example.sever.mapper;

import com.example.sever.dto.GiamGiaHoaDonDTO.GiamGiaHoaDonRequest;
import com.example.sever.dto.GiamGiaHoaDonDTO.GiamGiaHoaDonRespone;
import com.example.sever.enity.GiamGiaHoaDon;
import com.example.sever.enity.PhieuGiamGia;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class GiamGiaHoaDonMapper {


    public static GiamGiaHoaDonRespone mapToSale(GiamGiaHoaDon giamGiaHoaDon) {

        return new GiamGiaHoaDonRespone(
                giamGiaHoaDon.getIdGiamgiahoadon(),
                giamGiaHoaDon.getTenPhieu(),
                giamGiaHoaDon.getIdPhieuGiamGia().getIdPhieugiamgia(),
                giamGiaHoaDon.getIdPhieuGiamGia().getGiaTriGiam(),
                giamGiaHoaDon.getIdPhieuGiamGia().getMoTa(),
                giamGiaHoaDon.getIdPhieuGiamGia().getSoLuong(),
                giamGiaHoaDon.getIdPhieuGiamGia().getNgayBatDau() ,
                giamGiaHoaDon.getIdPhieuGiamGia().getNgayKetThuc()
        );
    }


    public static GiamGiaHoaDon mapToSale(GiamGiaHoaDonRequest request) {

        GiamGiaHoaDon giamGiaHoaDon = new GiamGiaHoaDon();
        giamGiaHoaDon.setIdGiamgiahoadon(request.getIdGiamgiahoadon());
        giamGiaHoaDon.setTenPhieu(request.getTenPhieu());

        return giamGiaHoaDon;

    }


    public static GiamGiaHoaDon mapToSale(GiamGiaHoaDonRespone respone) {


        GiamGiaHoaDon giamGiaHoaDon = new GiamGiaHoaDon();
        giamGiaHoaDon.setIdGiamgiahoadon(respone.getIdGiamgiahoadon());
        giamGiaHoaDon.setTenPhieu(respone.getTenPhieu());

        PhieuGiamGia phieu = new PhieuGiamGia();
        phieu.setIdPhieugiamgia(respone.getIdPhieugiamgia());
        phieu.setGiaTriGiam(respone.getGiaTriGiam());
        phieu.setMoTa(respone.getMoTa());
        phieu.setSoLuong(respone.getSoLuong());
        phieu.setNgayBatDau(respone.getNgayBatDau());
        phieu.setNgayKetThuc(respone.getNgayKetThuc());


        giamGiaHoaDon.setIdPhieuGiamGia(phieu);

        return giamGiaHoaDon;
    }


}
