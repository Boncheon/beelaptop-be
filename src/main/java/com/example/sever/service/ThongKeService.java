package com.example.sever.service;

import com.example.sever.dto.response.ThongKeResponseDTO;
import com.example.sever.dto.response.ThongKeTongQuanResponseDTO;
import com.example.sever.dto.response.TopLaptopBanChayResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface ThongKeService {
    List<ThongKeResponseDTO> thongKe12Thang(int nam1, int nam2);
    List<ThongKeResponseDTO> thongKeTheoNgay(int nam1, int thang1, int nam2, int thang2);
    List<ThongKeResponseDTO> soSanhHaiNgay(LocalDate ngay1, LocalDate ngay2);
    TopLaptopBanChayResponseDTO topLaptopBanChayNhat(int nam);
    ThongKeTongQuanResponseDTO thongKeTongQuan();
}



