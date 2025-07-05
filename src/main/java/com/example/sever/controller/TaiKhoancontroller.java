package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.TaiKhoanAddRequestDTO;
import com.example.sever.dto.request.TaiKhoanUpdateRequestDTO;
import com.example.sever.dto.response.TaiKhoanDisplayReponse;
import com.example.sever.service.TaiKhoanService;
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
@RequestMapping("/tai-khoan")
@AllArgsConstructor
public class TaiKhoancontroller {

    TaiKhoanService taiKhoanService;

    @GetMapping()
    public ResponseEntity<Page<TaiKhoanDisplayReponse>> getAllramforDisplay(@RequestParam(defaultValue = "1") int page,
                                                                            @RequestParam(defaultValue = "4") int size) {
        int perPage = page - 1;
        if (perPage < 0) perPage = 0;
        Pageable pageable = PageRequest.of(perPage, size);
        Page<TaiKhoanDisplayReponse> TaiKhoanPage = taiKhoanService.getAllTaiKhoanforDisplay(pageable);
        return ResponseEntity.ok(TaiKhoanPage);
    }

    @PostMapping("/them-tai-khoan")
    public ApiResponse<TaiKhoanDisplayReponse> addTaiKhoan(@RequestBody TaiKhoanAddRequestDTO TaiKhoanAddRequestDTO) {
        TaiKhoanDisplayReponse add = taiKhoanService.addTaiKhoan(TaiKhoanAddRequestDTO);
        return ApiResponse.<TaiKhoanDisplayReponse>builder()
                .message("them thanh cong")
                .data(add)
                .build();
    }
    @PostMapping("/sua-tai-khoan")
    public ApiResponse<TaiKhoanDisplayReponse> updateTaiKhoan(@RequestBody TaiKhoanUpdateRequestDTO taiKhoanUpdateRequestDTO) {
        TaiKhoanDisplayReponse update = taiKhoanService.updateTaiKhoan(taiKhoanUpdateRequestDTO);
        return ApiResponse.<TaiKhoanDisplayReponse>builder()
                .message("sua thanh cong")
                .data(update)
                .build();
    }
}
