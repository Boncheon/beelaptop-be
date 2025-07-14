package com.example.sever.statusautoDot;

import com.example.sever.service.DotGiamGiaChiTietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class DotGiamGiaScheduler {


    @Autowired
    private DotGiamGiaChiTietService dotGiamGiaChiTietService;

    @Scheduled(cron = "0 0 0 * * *") // 0h mỗi ngày
    public void capNhatHangNgay() {
        dotGiamGiaChiTietService.capNhatTrangThaiTuDong();

    }



}
