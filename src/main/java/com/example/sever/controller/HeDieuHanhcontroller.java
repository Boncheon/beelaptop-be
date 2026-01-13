package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.HeDieuHanhAddRequestDTO;
import com.example.sever.dto.request.HeDieuHanhUpdateRequestDTO;
import com.example.sever.dto.response.DoHoaDisplayReponse;
import com.example.sever.dto.response.HeDieuHanhDisplayReponse;
import com.example.sever.entity.HeDieuHanh;
import com.example.sever.service.HeDieuHanhService;
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
@RequestMapping("/api/he-dieu-hanh")
@AllArgsConstructor
public class HeDieuHanhcontroller {

    HeDieuHanhService hedieuhanhService;

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN','NHAN_VIEN')")
    public ResponseEntity<Page<HeDieuHanhDisplayReponse>> getAllramforDisplay(@RequestParam(defaultValue = "1") int page,
                                                                              @RequestParam(defaultValue = "20") int size) {
        int perPage = page - 1;
        if (perPage < 0) perPage = 0;
        Pageable pageable = PageRequest.of(perPage, size);
        Page<HeDieuHanhDisplayReponse> hedieuhanhPage = hedieuhanhService.getAllHeDieuHanhforDisplay(pageable);
        return ResponseEntity.ok(hedieuhanhPage);
    }

    @PostMapping("/them-he-dieu-hanh")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<HeDieuHanh> addHeDieuHanh(@RequestBody HeDieuHanhAddRequestDTO hedieuhanhAddRequestDTO) {
        HeDieuHanh add = hedieuhanhService.addHeDieuHanh(hedieuhanhAddRequestDTO);
        return ApiResponse.<HeDieuHanh>builder()
                .message("them thanh cong")
                .data(add)
                .build();
    }
    @PostMapping("/sua-he-dieu-hanh")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<HeDieuHanh> updateHeDieuHanh(@RequestBody HeDieuHanhUpdateRequestDTO hedieuhanhUpdateRequestDTO) {
        HeDieuHanh  updated = hedieuhanhService.updateHeDieuHanh(hedieuhanhUpdateRequestDTO);
        return ApiResponse.<HeDieuHanh>builder()
                .message("cap nhap thanh cong")
                .data(updated)
                .build();
    }
    @GetMapping("/detail/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','NHAN_VIEN')")
    public ApiResponse<HeDieuHanhDisplayReponse> getDoHoaById(@PathVariable("id") UUID id) {
        HeDieuHanhDisplayReponse hdh = hedieuhanhService.getDetailedHeDieuHanh(id);
        return ApiResponse.<HeDieuHanhDisplayReponse>builder()
                .message("Lấy chi tiết HDH thành công")
                .data(hdh)
                .build();
    }

    @GetMapping("/trang-thai")
    @PreAuthorize("hasAnyRole('ADMIN','NHAN_VIEN')")
    public ResponseEntity<Page<HeDieuHanhDisplayReponse>> gettrangThaiforDisplay(@RequestParam(defaultValue = "1") int page,
                                                                            @RequestParam(defaultValue = "20") int size) {
        int perPage = page - 1;
        if (perPage < 0) perPage = 0;
        Pageable pageable = PageRequest.of(perPage, size);
        Page<HeDieuHanhDisplayReponse> cpuPage = hedieuhanhService.getTrangThaiCpuforDisplay(pageable);
        return ResponseEntity.ok(cpuPage);
    }
}