package com.example.sever.statusauto;

import com.example.sever.service.PhieuGiamGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class VoucherStatusAuto {


    @Autowired
    private PhieuGiamGiaService phieuGiamGiaService;

    @Scheduled(cron = "0 0 0 * * *") // mỗi ngày lúc 0h
    public void tuDongCapNhat() {
        phieuGiamGiaService.capNhatTrangThaiTuDong();
    }



}
