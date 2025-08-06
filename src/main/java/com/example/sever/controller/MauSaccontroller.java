package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.MauSacAddRequestDTO;
import com.example.sever.dto.request.MauSacUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.MauSacDisplayReponse;
import com.example.sever.entity.MauSac;
import com.example.sever.service.MauSacService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping("/mau-sac")
@AllArgsConstructor
public class MauSaccontroller {

    MauSacService mausacService;

    @GetMapping()
    public ResponseEntity<Page<MauSacDisplayReponse>> getAllramforDisplay(@RequestParam(defaultValue = "1") int page,
                                                                       @RequestParam(defaultValue = "20") int size) {
        int perPage = page - 1;
        if (perPage < 0) perPage = 0;
        Pageable pageable = PageRequest.of(perPage, size);
        Page<MauSacDisplayReponse> mausacPage = mausacService.getAllMauSacforDisplay(pageable);
        return ResponseEntity.ok(mausacPage);
    }

    @PostMapping("/them-mausac")
    public ApiResponse<MauSac> addMauSac(@RequestBody MauSacAddRequestDTO mausacAddRequestDTO) {
        MauSac add = mausacService.addMauSac(mausacAddRequestDTO);
        return ApiResponse.<MauSac>builder()
                .message("them thanh cong")
                .data(add)
                .build();
    }
    @PostMapping("/sua-mausac")
    public ApiResponse<MauSac> updateMauSac(@RequestBody MauSacUpdateRequestDTO mausacUpdateRequestDTO) {
        MauSac  updated = mausacService.updateMauSac(mausacUpdateRequestDTO);
        return ApiResponse.<MauSac>builder()
                .message("cap nhap thanh cong")
                .data(updated)
                .build();
    }
    @PostMapping("/status")
    public ApiResponse<MauSac> Status(@RequestBody StatusRequestDTO statusRequestDTO) {
        MauSac updatedStatus = mausacService.updateStatus(statusRequestDTO);
        return ApiResponse.<MauSac>builder()
                .message("thay doi trang thai thanh cong")
                .data(updatedStatus)
                .build();
    }
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Page<MauSacDisplayReponse>>> filterMauSac(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai,
            Pageable pageable) {

        Page<MauSacDisplayReponse> result = mausacService.getMauSacByFilter(trangThai,keyword, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<MauSacDisplayReponse>>builder()
                        .message("Lọc thành công")
                        .data(result)
                        .build()
        );
        
    }
    @GetMapping("/detail/{id}")
    public ApiResponse<MauSacDisplayReponse> getMauSacById(@PathVariable("id") UUID id) {
        MauSacDisplayReponse mausac = mausacService.getDetailedMauSac(id);
        return ApiResponse.<MauSacDisplayReponse>builder()
                .message("Lấy chi tiết CPU thành công")
                .data(mausac)
                .build();
    }
    
}
