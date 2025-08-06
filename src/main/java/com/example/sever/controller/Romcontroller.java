package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.RomAddRequestDTO;
import com.example.sever.dto.request.RomUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.RomDisplayReponse;
import com.example.sever.entity.Rom;
import com.example.sever.service.RomService;
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
@RequestMapping("/rom")
@AllArgsConstructor
public class Romcontroller {
    RomService romService;

    @GetMapping()
    public ResponseEntity<Page<RomDisplayReponse>> getAllramforDisplay(@RequestParam(defaultValue = "1") int page,
                                                                       @RequestParam(defaultValue = "20") int size) {
        int perPage = page - 1;
        if (perPage < 0) perPage = 0;
        Pageable pageable = PageRequest.of(perPage, size);
        Page<RomDisplayReponse> romPage = romService.getAllRomforDisplay(pageable);
        return ResponseEntity.ok(romPage);
    }

    @PostMapping("/them-rom")
    public ApiResponse<Rom> addRom(@RequestBody RomAddRequestDTO romAddRequestDTO) {
        Rom add = romService.addRom(romAddRequestDTO);
        return ApiResponse.<Rom>builder()
                .message("them thanh cong")
                .data(add)
                .build();
    }
    @PostMapping("/sua-rom")
    public ApiResponse<Rom> updateRom(@RequestBody RomUpdateRequestDTO romUpdateRequestDTO) {
        Rom  updated = romService.updateRom(romUpdateRequestDTO);
        return ApiResponse.<Rom>builder()
                .message("cap nhap thanh cong")
                .data(updated)
                .build();
    }
    @PostMapping("/status")
    public ApiResponse<Rom> Status(@RequestBody StatusRequestDTO statusRequestDTO) {
        Rom updatedStatus = romService.updateStatus(statusRequestDTO);
        return ApiResponse.<Rom>builder()
                .message("thay doi trang thai thanh cong")
                .data(updatedStatus)
                .build();
    }
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Page<RomDisplayReponse>>> filterRom(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai,
            Pageable pageable) {

        Page<RomDisplayReponse> result = romService.getRomByFilter(trangThai,keyword, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<RomDisplayReponse>>builder()
                        .message("Lọc thành công")
                        .data(result)
                        .build()
        );
    }
    @GetMapping("/detail/{id}")
    public ApiResponse<RomDisplayReponse> getRomById(@PathVariable("id") UUID id) {
        RomDisplayReponse rom = romService.getDetailedRom(id);
        return ApiResponse.<RomDisplayReponse>builder()
                .message("Lấy chi tiết CPU thành công")
                .data(rom)
                .build();
    }
}
