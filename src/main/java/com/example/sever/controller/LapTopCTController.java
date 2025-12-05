package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.LapTopCTAddRequestDTO;
import com.example.sever.dto.request.LapTopCTAutoGenRequestDTO;
import com.example.sever.dto.request.LapTopCTUpdateRequestDTO;
import com.example.sever.dto.response.LaptopChiTietResponseDTO;

import com.example.sever.service.LapTopCTService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/laptop-ct")
@RequiredArgsConstructor
public class LapTopCTController {

    private final LapTopCTService service;

    // =================== LIST ALL (PAGING) ===================
    @GetMapping
    public ApiResponse<Page<LaptopChiTietResponseDTO>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);

        return ApiResponse.<Page<LaptopChiTietResponseDTO>>builder()
                .message("Lấy danh sách biến thể thành công")
                .data(service.getAll(pageable))
                .build();
    }

    // =================== LIST BY LAPTOP ===================
    @GetMapping("/by-laptop/{idLaptop}")
    public ApiResponse<List<LaptopChiTietResponseDTO>> getByLaptop(@PathVariable UUID idLaptop) {

        return ApiResponse.<List<LaptopChiTietResponseDTO>>builder()
                .message("Danh sách biến thể của laptop")
                .data(service.getByLaptop(idLaptop))
                .build();
    }

    // =================== GET ONE ===================
    @GetMapping("/{id}")
    public ApiResponse<LaptopChiTietResponseDTO> getDetail(@PathVariable UUID id) {

        return ApiResponse.<LaptopChiTietResponseDTO>builder()
                .message("Lấy chi tiết biến thể thành công")
                .data(service.getById(id))
                .build();
    }

    // =================== ADD ===================
    // LƯU Ý: idLaptop nằm trong URL, không nằm trong body
    @PostMapping("/{idLaptop}")
    public ApiResponse<LaptopChiTietResponseDTO> add(
            @PathVariable UUID idLaptop,
            @RequestBody LapTopCTAddRequestDTO dto) {

        return ApiResponse.<LaptopChiTietResponseDTO>builder()
                .message("Thêm biến thể thành công")
                .data(service.add(idLaptop, dto))
                .build();
    }
    @PostMapping("/auto-gen")
    public ResponseEntity<List<LaptopChiTietResponseDTO>> autoGen(
            @RequestBody LapTopCTAutoGenRequestDTO req
    ) {
        return ResponseEntity.ok(service.autoGenVariants(req));
    }
    // =================== UPDATE ===================
    @PutMapping("/{id}")
    public ApiResponse<LaptopChiTietResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody LapTopCTUpdateRequestDTO dto) {

        return ApiResponse.<LaptopChiTietResponseDTO>builder()
                .message("Cập nhật biến thể thành công")
                .data(service.update(id, dto))
                .build();
    }

    // =================== UPDATE STATUS ===================
    @PutMapping("/{id}/status")
    public ApiResponse<LaptopChiTietResponseDTO> updateStatus(
            @PathVariable UUID id,
            @RequestParam Integer status) {

        return ApiResponse.<LaptopChiTietResponseDTO>builder()
                .message("Cập nhật trạng thái biến thể thành công")
                .data(service.updateStatus(id, status))
                .build();
    }
}
