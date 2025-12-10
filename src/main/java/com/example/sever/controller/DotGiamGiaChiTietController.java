package com.example.sever.controller;

import com.example.sever.dto.DotGiamGia.DotGiamGiaChiTietDTO;
import com.example.sever.entity.DotGiamGiaChiTiet;
import com.example.sever.service.DotGiamGiaChiTietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/dot-giam-gia-ct")
public class DotGiamGiaChiTietController {


    @Autowired
    private DotGiamGiaChiTietService service;

    @PostMapping
    public ResponseEntity<DotGiamGiaChiTiet> create(@RequestBody DotGiamGiaChiTietDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping("/dot/{idDot}")
    public ResponseEntity<List<DotGiamGiaChiTiet>> getByDot(@PathVariable UUID idDot) {
        return ResponseEntity.ok(service.getByDot(idDot));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok("Xóa thành công");
    }



}
