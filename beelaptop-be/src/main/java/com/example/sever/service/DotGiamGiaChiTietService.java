package com.example.sever.service;



import com.example.sever.dto.DotGiamGiaDTO.DotGiamGiaChiTietResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface DotGiamGiaChiTietService {

    List<DotGiamGiaChiTietResponse> getAllDotGiamGia();

    void capNhatTrangThaiTuDong();

    DotGiamGiaChiTietResponse doiTrangThaiNgungHoatDong(String idDotGiamGia);

    Page<DotGiamGiaChiTietResponse> getDotGiamGiaPage(int page, int size);

    List<DotGiamGiaChiTietResponse> searchDotByIdOrTen(String keyword);


    Page<DotGiamGiaChiTietResponse> filterDot(String keyword, LocalDate start, LocalDate end, Integer trangThai, int page, int size,String sortBy);



}
