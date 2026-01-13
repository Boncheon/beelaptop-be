package com.example.sever.service;

import com.example.sever.dto.response.*;

import java.time.LocalDate;
import java.util.List;

public interface ThongKeService {
    List<ThongKeResponseDTO> thongKe12Thang(int nam1, int nam2);
    List<ThongKeResponseDTO> thongKeTheoNgay(int nam1, int thang1, int nam2, int thang2);
    List<ThongKeResponseDTO> soSanhHaiNgay(LocalDate ngay1, LocalDate ngay2);
    TopLaptopBanChayResponseDTO topLaptopBanChayNhat(int nam);
    ThongKeTongQuanResponseDTO thongKeTongQuan();
    ThongKeTrangThaiResponseDTO thongKeTheoTrangThai();
    ThongKeTruyCapResponseDTO thongKeTruyCap();

    ThongKeKhachHangTongResponseDTO thongKeTongKhachHang();

    List<ThongKeKhachHangSoDonResponseDTO> thongKeKhachHangMuaBaoNhieuDon();
}

