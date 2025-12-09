package com.example.sever.controller;


import com.example.sever.dto.GioHang.GioHangChiTietDTO;
import com.example.sever.dto.GioHang.GioHangDTO;
import com.example.sever.service.GioHangChiTietService;
import com.example.sever.service.GioHangService;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/gio-hang")
@AllArgsConstructor
@RestController
public class GioHangController {

    @Autowired
    GioHangService gioHangService;

    @Autowired
    GioHangChiTietService gioHangCTService;


    @PostMapping("/create/{idTaiKhoan}")
    public GioHangDTO create(@PathVariable UUID idTaiKhoan) {
        return gioHangService.createCart(idTaiKhoan);
    }

    @PostMapping("/item")
    public GioHangChiTietDTO add(@RequestBody GioHangChiTietDTO dto) {
        return gioHangCTService.addToCart(dto);
    }

    @GetMapping("/{idGioHang}")
    public List<GioHangChiTietDTO> getCart(@PathVariable UUID idGioHang) {
        return gioHangCTService.getCartItems(idGioHang);
    }

    @PutMapping("/item/{id}")
    public void update(@PathVariable UUID id, @RequestParam Integer quantity) {
        gioHangCTService.updateQuantity(id, quantity);
    }

    @DeleteMapping("/item/{id}")
    public void delete(@PathVariable UUID id) {
        gioHangCTService.deleteItem(id);
    }

}
