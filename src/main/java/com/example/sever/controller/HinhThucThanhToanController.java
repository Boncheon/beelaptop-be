package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.HinhThucThanhToanDTO;
import com.example.sever.service.HinhThucThanhToanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hinh-thuc-thanh-toan")
@CrossOrigin("*")
@RequiredArgsConstructor
public class HinhThucThanhToanController {

    private final HinhThucThanhToanService hinhThucThanhToanService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<HinhThucThanhToanDTO>>> getAll() {
        List<HinhThucThanhToanDTO> data = hinhThucThanhToanService.getAll();
        return ResponseEntity.ok(
                ApiResponse.<List<HinhThucThanhToanDTO>>builder()
                        .code(200)
                        .message("Lấy danh sách hình thức thanh toán thành công")
                        .data(data)
                        .build()
        );
    }
}

