package com.example.sever.service.impl;


import com.example.sever.TrangThaiDotGiamGia;
import com.example.sever.dto.DotGiamGiaDTO.DotGiamGiaChiTietResponse;
import com.example.sever.entity.DotGiamGia;
import com.example.sever.entity.DotGiamGiaChiTiet;
import com.example.sever.exception.ResourceNotFoundExceptionDot;
import com.example.sever.mapper.DotGiamGiaChiTietMapper;
import com.example.sever.repository.DotGiamGiaChiTietRepository;
import com.example.sever.repository.DotGiamGiaRepository;
import com.example.sever.service.DotGiamGiaChiTietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DotGiamGiaChiTietServiceIpml implements DotGiamGiaChiTietService {


    @Autowired
    private DotGiamGiaRepository dotGiamGiaRepo;

    @Autowired
    private DotGiamGiaChiTietRepository dotGiamGiaChiTietRepo;


    @Override
    public List<DotGiamGiaChiTietResponse> getAllDotGiamGia() {
        capNhatTrangThaiTuDong();

        List<DotGiamGiaChiTiet> list = dotGiamGiaChiTietRepo.findAll();
        return list.stream()
                .map(DotGiamGiaChiTietMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void capNhatTrangThaiTuDong() {
        List<DotGiamGia> danhSach = dotGiamGiaRepo.findAll();
        LocalDate today = LocalDate.now();

        for (DotGiamGia dot : danhSach) {
            // Không cập nhật nếu là NGƯNG HOẠT ĐỘNG
            if (dot.getTrangThai() != null &&
                    dot.getTrangThai().equals(TrangThaiDotGiamGia.NGUNG_HOAT_DONG.getValue())) {
                continue;
            }

            if (dot.getNgayKetThuc().isBefore(today)) {
                dot.setTrangThai(TrangThaiDotGiamGia.HET_HAN.getValue());
            } else if (dot.getNgayBatDau().isAfter(today)) {
                dot.setTrangThai(TrangThaiDotGiamGia.CHUA_KICH_HOAT.getValue());
            } else {
                dot.setTrangThai(TrangThaiDotGiamGia.DANG_HOAT_DONG.getValue());
            }
        }

        dotGiamGiaRepo.saveAll(danhSach);
    }

    @Override
    public DotGiamGiaChiTietResponse doiTrangThaiNgungHoatDong(String idDotGiamGia) {
        DotGiamGia dot = dotGiamGiaRepo.findByIdDotGiamGia(idDotGiamGia)
                .orElseThrow(() -> new ResourceNotFoundExceptionDot("Đợt giảm giá không tồn tại: " + idDotGiamGia));

        if (dot.getTrangThai() != TrangThaiDotGiamGia.NGUNG_HOAT_DONG.getValue()) {
            dot.setTrangThai(TrangThaiDotGiamGia.NGUNG_HOAT_DONG.getValue());
            dot = dotGiamGiaRepo.save(dot);
        }

        return dotGiamGiaChiTietRepo.findByDotGiamGia(dot)
                .map(DotGiamGiaChiTietMapper::toResponse)
                .orElse(DotGiamGiaChiTietMapper.toBasicResponse(dot));
    }

    @Override
    public Page<DotGiamGiaChiTietResponse> getDotGiamGiaPage(int page, int size) {
        capNhatTrangThaiTuDong();

        Pageable pageable = PageRequest.of(page, size, Sort.by("dotGiamGia.ngayBatDau").descending());
        Page<DotGiamGiaChiTiet> chiTietPage = dotGiamGiaChiTietRepo.findAll(pageable);

        return chiTietPage.map(DotGiamGiaChiTietMapper::toResponse);
    }

    @Override
    public List<DotGiamGiaChiTietResponse> searchDotByIdOrTen(String keyword) {

        List<DotGiamGiaChiTiet> list = dotGiamGiaChiTietRepo
                .findByDotGiamGia_IdDotGiamGiaContainingIgnoreCaseOrDotGiamGia_TenDotGiamGiaContainingIgnoreCase(keyword, keyword);

        return list.stream()
                .map(DotGiamGiaChiTietMapper::toResponse)
                .collect(Collectors.toList());

    }

    @Override
    public Page<DotGiamGiaChiTietResponse> filterDot(String keyword, LocalDate start, LocalDate end, Integer status, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, buildSort(sortBy));
        Page<DotGiamGiaChiTiet> result = dotGiamGiaChiTietRepo.filterDot(keyword, start, end, status, pageable);
        return result.map(DotGiamGiaChiTietMapper::toResponse);
    }


    private Sort buildSort(String sortBy) {
        if (sortBy == null || sortBy.isEmpty()) {
            return Sort.unsorted();
        }

        String[] parts = sortBy.split("_");
        if (parts.length != 2) return Sort.unsorted();

        String field = parts[0];
        String direction = parts[1];

        String sortField;
        switch (field) {
            case "ten":
                sortField = "dotGiamGia.tenDotGiamGia";
                break;
            case "ngayBatDau":
                sortField = "dotGiamGia.ngayBatDau";
                break;
            case "ngayKetThuc":
                sortField = "dotGiamGia.ngayKetThuc";
                break;
            default:
                sortField = "dotGiamGia.ngayBatDau";
        }

        return direction.equalsIgnoreCase("desc")
                ? Sort.by(Sort.Order.desc(sortField))
                : Sort.by(Sort.Order.asc(sortField));
    }


}
