package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.PinAddRequestDTO;
import com.example.sever.dto.request.PinUpdateRequestDTO;
import com.example.sever.dto.response.HeDieuHanhDisplayReponse;
import com.example.sever.dto.response.PinDisplayReponse;
import com.example.sever.entity.Pin;
import com.example.sever.service.PinService;
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
@RequestMapping("/api/pin")
@AllArgsConstructor
public class Pincontroller {

    PinService pinService;

    @GetMapping()
    public ResponseEntity<Page<PinDisplayReponse>> getAllramforDisplay(@RequestParam(defaultValue = "1") int page,
                                                                       @RequestParam(defaultValue = "4") int size) {
        int perPage = page - 1;
        if (perPage < 0) perPage = 0;
        Pageable pageable = PageRequest.of(perPage, size);
        Page<PinDisplayReponse> pinPage = pinService.getAllPinforDisplay(pageable);
        return ResponseEntity.ok(pinPage);
    }

    @PostMapping("/them-pin")
    public ApiResponse<Pin> addPin(@RequestBody PinAddRequestDTO pinAddRequestDTO) {
        Pin add = pinService.addPin(pinAddRequestDTO);
        return ApiResponse.<Pin>builder()
                .message("them thanh cong")
                .data(add)
                .build();
    }
    @PostMapping("/sua-pin")
    public ApiResponse<Pin> updatePin(@RequestBody PinUpdateRequestDTO pinUpdateRequestDTO) {
        Pin  updated = pinService.updatePin(pinUpdateRequestDTO);
        return ApiResponse.<Pin>builder()
                .message("cap nhap thanh cong")
                .data(updated)
                .build();
    }

    @GetMapping("/detail/{id}")
    public ApiResponse<PinDisplayReponse> getDoHoaById(@PathVariable("id") UUID id) {
        PinDisplayReponse p = pinService.getDetailedPin(id);
        return ApiResponse.<PinDisplayReponse>builder()
                .message("Lấy chi tiết Pin thành công")
                .data(p)
                .build();
    }
}
