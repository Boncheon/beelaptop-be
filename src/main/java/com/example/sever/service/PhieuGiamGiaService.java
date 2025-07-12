package com.example.sever.service;

import com.example.sever.dto.PhieuGiamGiaDTO.PhieuGiamGiaDto;
import com.example.sever.entity.PhieuGiamGia;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;



public interface PhieuGiamGiaService {

    List<PhieuGiamGiaDto> getAllVoucher();

    PhieuGiamGiaDto addVoucher(PhieuGiamGiaDto phieuGiamGiaDto);

    PhieuGiamGiaDto detailVoucher(String voucherId);

    PhieuGiamGiaDto updateVoucher(String voucherId,PhieuGiamGiaDto phieuGiamGiaDto);

    void deleteVoucher(String voucherId);


    List<PhieuGiamGiaDto> searchVoucherByIdOrTen(String keyword);


    List<PhieuGiamGiaDto> filterVoucher(String keyword, LocalDate startDate, LocalDate endDate, Integer trangThai, String sortBy);

    PhieuGiamGia findBestCouponForOrder(BigDecimal total);

    BigDecimal calculateDiscount(PhieuGiamGia coupon, BigDecimal total);


    void capNhatTrangThaiTuDong();

    Page<PhieuGiamGiaDto> getVouchersPaging(int page, int size);

    boolean checkMaTrung(String idPhieugiamgia);


    PhieuGiamGiaDto doiTrangThaiNgungHoatDong(String idPhieuGiamGia);



}
