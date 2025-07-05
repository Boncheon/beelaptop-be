package com.example.sever.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class QuanLyKhachHang {
    @GetMapping("/admin/khach-hang/hien-thi")
    public String QuanLyKhachHangHienThi(){
        return "/Admin/AdminQuanLyKhachHang/KhachHangHienThi";
    }
}
