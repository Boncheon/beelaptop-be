package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.KichThuocAddRequestDTO;
import com.example.sever.dto.request.KichThuocUpdateRequestDTO;
import com.example.sever.dto.response.KichThuocDisplayReponse;
import com.example.sever.entity.KichThuoc;
import com.example.sever.service.KichThuocService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/kich-thuoc")
@AllArgsConstructor
public class KichThuoccontroller {

    KichThuocService kichthuocService;

    @GetMapping()
    public ResponseEntity<Page<KichThuocDisplayReponse>> getAllramforDisplay(@RequestParam(defaultValue = "1") int page,
                                                                       @RequestParam(defaultValue = "4") int size) {
        int perPage = page - 1;
        if (perPage < 0) perPage = 0;
        Pageable pageable = PageRequest.of(perPage, size);
        Page<KichThuocDisplayReponse> kichthuocPage = kichthuocService.getAllKichThuocforDisplay(pageable);
        return ResponseEntity.ok(kichthuocPage);
    }

    @PostMapping("/them-kich-thuoc")
    public ApiResponse<KichThuoc> addKichThuoc(@RequestBody KichThuocAddRequestDTO kichthuocAddRequestDTO) {
        KichThuoc add = kichthuocService.addKichThuoc(kichthuocAddRequestDTO);
        return ApiResponse.<KichThuoc>builder()
                .message("them thanh cong")
                .data(add)
                .build();
    }
    @PostMapping("/sua-kich-thuoc")
    public ApiResponse<KichThuoc> updateKichThuoc(@RequestBody KichThuocUpdateRequestDTO kichthuocUpdateRequestDTO) {
        KichThuoc  updated = kichthuocService.updateKichThuoc(kichthuocUpdateRequestDTO);
        return ApiResponse.<KichThuoc>builder()
                .message("cap nhap thanh cong")
                .data(updated)
                .build();
    }
}
