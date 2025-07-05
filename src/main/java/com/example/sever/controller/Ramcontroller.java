package com.example.sever.controller;


import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.RamAddRequestDTO;
import com.example.sever.dto.request.RamUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.RamDIsplayReponse;
import com.example.sever.entity.Ram;
import com.example.sever.service.RamService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/ram")
@AllArgsConstructor
public class Ramcontroller {
    RamService ramService;

    @GetMapping()
    public ResponseEntity<Page<RamDIsplayReponse>> getAllramforDisplay(@RequestParam(defaultValue = "1") int page,
                                                                       @RequestParam(defaultValue = "4") int size) {
        int perPage = page - 1;
        if (perPage < 0) perPage = 0;
        Pageable pageable = PageRequest.of(perPage, size);
        Page<RamDIsplayReponse> ramPage = ramService.getAllRamforDisplay(pageable);
        return ResponseEntity.ok(ramPage);
    }

    @PostMapping("/them-ram")
    public ApiResponse<Ram> addRam(@RequestBody RamAddRequestDTO ramAddRequestDTO) {
        Ram add = ramService.addRam(ramAddRequestDTO);
        return ApiResponse.<Ram>builder()
                .message("them thanh cong")
                .data(add)
                .build();
    }
    @PostMapping("/sua-ram")
    public ApiResponse<Ram> updateRam(@RequestBody RamUpdateRequestDTO ramUpdateRequestDTO) {
        Ram  updated = ramService.updateRam(ramUpdateRequestDTO);
        return ApiResponse.<Ram>builder()
                .message("cap nhap thanh cong")
                .data(updated)
                .build();
    }
    @PostMapping("/status")
    public ApiResponse<Ram> Status(@RequestBody StatusRequestDTO statusRequestDTO) {
        Ram updatedStatus = ramService.updateStatus(statusRequestDTO);
        return ApiResponse.<Ram>builder()
                .message("thay doi trang thai thanh cong")
                .data(updatedStatus)
                .build();
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Page<RamDIsplayReponse>>> filterRam(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai,
            Pageable pageable) {

        Page<RamDIsplayReponse> result = ramService.getRamByFilter(trangThai,keyword, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<RamDIsplayReponse>>builder()
                        .message("Lọc thành công")
                        .data(result)
                        .build()
        );
    }
    @GetMapping("/detail/{id}")
    public ApiResponse<RamDIsplayReponse> getRamById(@PathVariable("id") UUID id) {
        RamDIsplayReponse ram = ramService.getDetailedRam(id);
        return ApiResponse.<RamDIsplayReponse>builder()
                .message("Lấy chi tiết CPU thành công")
                .data(ram)
                .build();
    }
}
