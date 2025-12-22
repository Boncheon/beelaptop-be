package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.ThuongHieuAddRequestDTO;
import com.example.sever.dto.request.ThuongHieuUpdateRequestDTO;
import com.example.sever.dto.response.ThuongHieuDisplayReponse;
import com.example.sever.entity.ThuongHieu;
import com.example.sever.service.ThuongHieuService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin("*")
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/thuong-hieu")
@AllArgsConstructor
public class ThuongHieucontroller {

    ThuongHieuService thuonghieuService;

    @GetMapping()
    public ResponseEntity<Page<ThuongHieuDisplayReponse>> getAllramforDisplay(@RequestParam(defaultValue = "1") int page,
                                                                              @RequestParam(defaultValue = "20") int size) {
        int perPage = page - 1;
        if (perPage < 0) perPage = 0;
        Pageable pageable = PageRequest.of(perPage, size);
        Page<ThuongHieuDisplayReponse> ThuongHieuPage = thuonghieuService.getAllThuongHieuforDisplay(pageable);
        return ResponseEntity.ok(ThuongHieuPage);
    }

    @PostMapping("/them-thuong-hieu")
    public ApiResponse<ThuongHieu> addThuongHieu(@RequestBody ThuongHieuAddRequestDTO ThuongHieuAddRequestDTO) {
        ThuongHieu add = thuonghieuService.addThuongHieu(ThuongHieuAddRequestDTO);
        return ApiResponse.<ThuongHieu>builder()
                .message("them thanh cong")
                .data(add)
                .build();
    }
    @PostMapping("/sua-thuong-hieu")
    public ApiResponse<ThuongHieu> updateThuongHieu(@RequestBody ThuongHieuUpdateRequestDTO ThuongHieuUpdateRequestDTO) {
        ThuongHieu  updated = thuonghieuService.updateThuongHieu(ThuongHieuUpdateRequestDTO);
        return ApiResponse.<ThuongHieu>builder()
                .message("cap nhap thanh cong")
                .data(updated)
                .build();
    }
    @GetMapping("/detail/{id}")
    public ApiResponse<ThuongHieuDisplayReponse> getDoHoaById(@PathVariable("id") UUID id) {
        ThuongHieuDisplayReponse hdh = thuonghieuService.getDetailedThuongHieu(id);
        return ApiResponse.<ThuongHieuDisplayReponse>builder()
                .message("Lấy chi tiết HDH thành công")
                .data(hdh)
                .build();
    }
}