package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.HeDieuHanhAddRequestDTO;
import com.example.sever.dto.request.HeDieuHanhUpdateRequestDTO;
import com.example.sever.dto.response.HeDieuHanhDisplayReponse;
import com.example.sever.entity.HeDieuHanh;
import com.example.sever.service.HeDieuHanhService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController
@RequestMapping("/he-dieu-hanh")
@AllArgsConstructor
public class HeDieuHanhcontroller {

    HeDieuHanhService hedieuhanhService;

    @GetMapping()
    public ResponseEntity<Page<HeDieuHanhDisplayReponse>> getAllramforDisplay(@RequestParam(defaultValue = "1") int page,
                                                                       @RequestParam(defaultValue = "20") int size) {
        int perPage = page - 1;
        if (perPage < 0) perPage = 0;
        Pageable pageable = PageRequest.of(perPage, size);
        Page<HeDieuHanhDisplayReponse> hedieuhanhPage = hedieuhanhService.getAllHeDieuHanhforDisplay(pageable);
        return ResponseEntity.ok(hedieuhanhPage);
    }

    @PostMapping("/them-he-dieu-hanh")
    public ApiResponse<HeDieuHanh> addHeDieuHanh(@RequestBody HeDieuHanhAddRequestDTO hedieuhanhAddRequestDTO) {
        HeDieuHanh add = hedieuhanhService.addHeDieuHanh(hedieuhanhAddRequestDTO);
        return ApiResponse.<HeDieuHanh>builder()
                .message("them thanh cong")
                .data(add)
                .build();
    }
    @PostMapping("/sua-he-dieu-hanh")
    public ApiResponse<HeDieuHanh> updateHeDieuHanh(@RequestBody HeDieuHanhUpdateRequestDTO hedieuhanhUpdateRequestDTO) {
        HeDieuHanh  updated = hedieuhanhService.updateHeDieuHanh(hedieuhanhUpdateRequestDTO);
        return ApiResponse.<HeDieuHanh>builder()
                .message("cap nhap thanh cong")
                .data(updated)
                .build();
    }
}
