package com.example.sever.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminQuanlyNhanVienController {
    @GetMapping("/admin/quan-ly-nhan-vien")
    public String adminQuanlyNhanVienHienThi(){
        return "/Admin/AdminQuanLyKhachHang/NhanVienHienThi";
    }
}
