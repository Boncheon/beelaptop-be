package com.example.sever.controller;

import com.example.sever.dto.GiamGiaHoaDonDTO.GiamGiaHoaDonRespone;
import com.example.sever.service.GiamGiaHoaDonService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/giam-gia-hoa-don")
public class GiamGiaHoaDonController {



    @Autowired
    private GiamGiaHoaDonService giamGiaHoaDonService;


    @GetMapping
    public ResponseEntity<List<GiamGiaHoaDonRespone>> getAll(){

        List<GiamGiaHoaDonRespone> ghd = giamGiaHoaDonService.getAllGiamGia();
        return ResponseEntity.ok(ghd);

    }








}
