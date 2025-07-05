package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.PhienBanAddRequestDTO;
import com.example.sever.dto.request.PhienBanUpdateRequestDTO;
import com.example.sever.dto.response.PhienBanDisplayReponse;
import com.example.sever.service.PhienBanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/phien-ban")
@RequiredArgsConstructor // Tự động tạo constructor cho các final field
public class PhienBanController {

    private final PhienBanService phienBanService;


    @PostMapping("/them-phienban")
    public ApiResponse<PhienBanDisplayReponse> addPhienBan(@RequestBody PhienBanAddRequestDTO requestDTO) {
        PhienBanDisplayReponse add = phienBanService.addPhienBan(requestDTO);
        return ApiResponse.<PhienBanDisplayReponse>builder()
                .message("them thanh cong")
                .data(add)
                .build();
    }

    @PostMapping("/sua-phienban")
    public ApiResponse<PhienBanDisplayReponse> updatePhienBan(@RequestBody PhienBanUpdateRequestDTO requestDTO) {
        PhienBanDisplayReponse updayte = phienBanService.updatePhienBan(requestDTO);
        return ApiResponse.<PhienBanDisplayReponse>builder()
                .message("them thanh cong")
                .data(updayte)
                .build();
    }

    @GetMapping()
    public ResponseEntity<Page<PhienBanDisplayReponse>> getAllPhienBanforDisplay(@RequestParam(defaultValue = "1") int page,
                                                                       @RequestParam(defaultValue = "4") int size) {
        int perPage = page - 1;
        if (perPage < 0) perPage = 0;
        Pageable pageable = PageRequest.of(perPage, size);
        Page<PhienBanDisplayReponse> phienBanpage = phienBanService.getAllPhienBanforDisplay(pageable);
        return ResponseEntity.ok(phienBanpage);
    }
}
