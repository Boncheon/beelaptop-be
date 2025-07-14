package com.example.sever.controller;


import com.example.sever.dto.DotGiamGiaDTO.DotGiamGiaChiTietResponse;
import com.example.sever.service.DotGiamGiaChiTietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/dot-giam-gia")
public class DotGiamGiaController {

    @Autowired
    private DotGiamGiaChiTietService dotGiamGiaChiTietService;

    // ✅ Lấy tất cả đợt giảm giá
    @GetMapping
    public ResponseEntity<List<DotGiamGiaChiTietResponse>> getAll() {
        List<DotGiamGiaChiTietResponse> dgg = dotGiamGiaChiTietService.getAllDotGiamGia();
        return ResponseEntity.ok(dgg);
    }

    // ✅ Đổi trạng thái thành "Ngừng hoạt động"
    @PutMapping("/ngung-hoat-dong/{id}")
    public ResponseEntity<?> ngungHoatDong(@PathVariable("id") String id) {
        try {
            DotGiamGiaChiTietResponse dto = dotGiamGiaChiTietService.doiTrangThaiNgungHoatDong(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy đợt giảm giá hoặc lỗi xảy ra.");
        }
    }

    // ✅ Phân trang
    @GetMapping("/phan-trang")
    public ResponseEntity<Map<String, Object>> getDotGiamGiaPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<DotGiamGiaChiTietResponse> pageResult = dotGiamGiaChiTietService.getDotGiamGiaPage(page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("dotGiamGia", pageResult.getContent());
        response.put("currentPage", pageResult.getNumber());
        response.put("totalItems", pageResult.getTotalElements());
        response.put("totalPages", pageResult.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<DotGiamGiaChiTietResponse>> filterDot(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String sortBy
    ) {
        System.out.println("🔥 Status nhận vào = " + status); // 👈 log tạm ra

        return ResponseEntity.ok(dotGiamGiaChiTietService.filterDot(keyword, start, end, status, page, size,sortBy));
    }





}
