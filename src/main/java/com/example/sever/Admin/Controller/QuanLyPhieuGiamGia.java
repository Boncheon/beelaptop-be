package com.example.sever.Admin.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class QuanLyPhieuGiamGia {
    @GetMapping("/admin/quan-ly-phieu-giam-gia/hien-thi")
    public String adminQuanlyPheuGiamGiaHienThi(){
        return "/Admin/AdminQuanLyPhieuGiamGia/PhieuGiamGiaHienThi";
    }
}
