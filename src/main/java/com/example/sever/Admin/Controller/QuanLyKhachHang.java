package com.example.sever.Admin.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class QuanLyKhachHang {
    @GetMapping("/khach-hang/hien-thi")
    public String QuanLyKhachHangHienThi(){
        return "/Admin/AdminQuanLyKhachHang/KhachHangHienThi";
    }
}
