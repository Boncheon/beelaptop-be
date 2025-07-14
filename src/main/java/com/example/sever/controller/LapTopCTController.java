package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.LapTopCTAddRequestDTO;
import com.example.sever.dto.request.LapTopCTUpdateRequestDTO;
import com.example.sever.dto.response.LapTopCTDisplayReponse;
import com.example.sever.service.LapTopCTService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/lap-top-ct")
@RequiredArgsConstructor
public class LapTopCTController {

    private final LapTopCTService lapTopService;

    @PostMapping("/them-laptopct")
    public ApiResponse<LapTopCTDisplayReponse> addLapTopCT(@RequestBody LapTopCTAddRequestDTO requestDTO) {
        LapTopCTDisplayReponse response = lapTopService.addLapTopCT(requestDTO);
        return ApiResponse.<LapTopCTDisplayReponse>builder()
                .message("Thêm phiên bản thành công")
                .data(response)
                .build();
    }

    @PutMapping("/sua-laptopct")
    public ApiResponse<LapTopCTDisplayReponse> updateLapTopCT(@RequestBody LapTopCTUpdateRequestDTO requestDTO) {
        LapTopCTDisplayReponse updateResponse = lapTopService.updateLapTopCT(requestDTO);
        return ApiResponse.<LapTopCTDisplayReponse>builder()
                .message("Cập nhật phiên bản thành công")
                .data(updateResponse)
                .build();
    }

    @GetMapping
    public ResponseEntity<Page<LapTopCTDisplayReponse>> getAllLapTopCTforDisplay(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size) {

        int currentPage = Math.max(page - 1, 0); // Đảm bảo không nhỏ hơn 0
        Pageable pageable = PageRequest.of(currentPage, size);
        Page<LapTopCTDisplayReponse> resultPage = lapTopService.getAllLapTopCTforDisplay(pageable);
        return ResponseEntity.ok(resultPage);
    }
}
