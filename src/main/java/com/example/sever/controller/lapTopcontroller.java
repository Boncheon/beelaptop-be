package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.LaptopAddRequestDTO;
import com.example.sever.dto.request.RomAddRequestDTO;
import com.example.sever.dto.request.SanPhamFullCreateDTO;
import com.example.sever.dto.response.LapTopCTDisplayReponse;
import com.example.sever.dto.response.LapTopDisplayReponse;
import com.example.sever.dto.response.LaptopDisplayResponseDTO;
import com.example.sever.dto.response.RomDisplayReponse;
import com.example.sever.entity.Laptop;
import com.example.sever.entity.Rom;
import com.example.sever.service.LaptopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
@CrossOrigin("*")
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/laptop")
public class lapTopcontroller {


    @Autowired
    private LaptopService laptopService;

    //lấy tất cả laptop và phân trang

    @GetMapping()
    public ResponseEntity<Page<LapTopDisplayReponse>> getAlllapTopforDisplay(@RequestParam(defaultValue = "1") int page,
                                                                             @RequestParam(defaultValue = "20") int size) {
        int perPage = page - 1;
        if (perPage < 0) perPage = 0;
        Pageable pageable = PageRequest.of(perPage, size);
        Page<LapTopDisplayReponse> laptopPage = laptopService.getAllLapTopforDisplay(pageable);
        return ResponseEntity.ok(laptopPage);
    }

    @PostMapping("/them-full")
    public ResponseEntity<UUID> createFullLaptop(@RequestBody SanPhamFullCreateDTO dto) {
        UUID id = laptopService.createSanPhamHoanChinh(dto);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{idLaptop}")
    public ResponseEntity<List<LapTopCTDisplayReponse>> getLaptopDetail(@PathVariable UUID idLaptop) {
        List<LapTopCTDisplayReponse> response = laptopService.getDetailedLapTop(idLaptop);
        return ResponseEntity.ok(response);
    }

}