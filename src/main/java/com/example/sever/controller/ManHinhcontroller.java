package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.ManHinhAddRequestDTO;
import com.example.sever.dto.request.ManHinhUpdateRequestDTO;
import com.example.sever.dto.response.ManHinhDisplayReponse;
import com.example.sever.entity.ManHinh;
import com.example.sever.service.ManHinhService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin("*")
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/man-hinh")
@AllArgsConstructor
public class ManHinhcontroller {

    ManHinhService manhinhService;

    @GetMapping()
    public ResponseEntity<Page<ManHinhDisplayReponse>> getAllramforDisplay(@RequestParam(defaultValue = "1") int page,
                                                                           @RequestParam(defaultValue = "4") int size) {
        int perPage = page - 1;
        if (perPage < 0) perPage = 0;
        Pageable pageable = PageRequest.of(perPage, size);
        Page<ManHinhDisplayReponse> manhinhPage = manhinhService.getAllManHinhforDisplay(pageable);
        return ResponseEntity.ok(manhinhPage);
    }

    @PostMapping("/them-man-hinh")
    public ApiResponse<ManHinh> addManHinh(@RequestBody ManHinhAddRequestDTO manhinhAddRequestDTO) {
        ManHinh add = manhinhService.addManHinh(manhinhAddRequestDTO);
        return ApiResponse.<ManHinh>builder()
                .message("them thanh cong")
                .data(add)
                .build();
    }
    @PostMapping("/sua-man-hinh")
    public ApiResponse<ManHinh> updateManHinh(@RequestBody ManHinhUpdateRequestDTO manhinhUpdateRequestDTO) {
        ManHinh  updated = manhinhService.updateManHinh(manhinhUpdateRequestDTO);
        return ApiResponse.<ManHinh>builder()
                .message("cap nhap thanh cong")
                .data(updated)
                .build();
    }
}