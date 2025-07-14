package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.SeriAddRequestDTO;
import com.example.sever.dto.request.SeriUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.SeriDisplayReponse;
import com.example.sever.entity.Seri;
import com.example.sever.service.SeriService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/seri")
@AllArgsConstructor
public class Sericontroller {

    SeriService seriService;

    @GetMapping()
    public ResponseEntity<Page<SeriDisplayReponse>> getAllramforDisplay(@RequestParam(defaultValue = "1") int page,
                                                                        @RequestParam(defaultValue = "20") int size) {
        int perPage = page - 1;
        if (perPage < 0) perPage = 0;
        Pageable pageable = PageRequest.of(perPage, size);
        Page<SeriDisplayReponse> seriPage = seriService.getAllSeriforDisplay(pageable);
        return ResponseEntity.ok(seriPage);
    }
    @GetMapping("/detail/{id}")
    public ApiResponse<SeriDisplayReponse> getSeriById(@PathVariable("id") UUID id) {
        SeriDisplayReponse seri = seriService.getDetailedSeri(id);
        return ApiResponse.<SeriDisplayReponse>builder()
                .message("Lấy chi tiết CPU thành công")
                .data(seri)
                .build();
    }
    @PostMapping("/them-seri")
    public ApiResponse<SeriDisplayReponse> addSeri(@RequestBody SeriAddRequestDTO seriAddRequestDTO) {
        SeriDisplayReponse add = seriService.addSeri(seriAddRequestDTO);
        return ApiResponse.<SeriDisplayReponse>builder()
                .message("them thanh cong")
                .data(add)
                .build();
    }
    @PostMapping("/sua-seri")
    public ApiResponse<Seri> updateSeri(@RequestBody SeriUpdateRequestDTO seriUpdateRequestDTO) {
//        seriUpdateRequestDTO.setIdSeri(id.toString());
        Seri  updated = seriService.updateSeri(seriUpdateRequestDTO);
        return ApiResponse.<Seri>builder()
                .message("cap nhap thanh cong")
                .data(updated)
                .build();
    }
    @PostMapping("/status")
    public ApiResponse<Seri> Status(@RequestBody StatusRequestDTO statusRequestDTO) {
        Seri updatedStatus = seriService.updateStatus(statusRequestDTO);
        return ApiResponse.<Seri>builder()
                .message("thay doi trang thai thanh cong")
                .data(updatedStatus)
                .build();
    }
//    @GetMapping("/filter")
//    public ResponseEntity<ApiResponse<Page<SeriDisplayReponse>>> filterSeri(
//            @RequestParam(required = false) String keyword,
//            @RequestParam(required = false) Integer trangThai,
//            Pageable pageable) {
//
//        Page<SeriDisplayReponse> result = seriService.getSeriByFilter(trangThai,keyword, pageable);
//
//        return ResponseEntity.ok(
//                ApiResponse.<Page<SeriDisplayReponse>>builder()
//                        .message("Lọc thành công")
//                        .data(result)
//                        .build()
//        );
//    }
}