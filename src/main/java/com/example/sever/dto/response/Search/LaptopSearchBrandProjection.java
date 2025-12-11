package com.example.sever.dto.response.Search;

import java.math.BigDecimal;
import java.util.UUID;

public interface LaptopSearchBrandProjection {
    UUID getLaptopID();
    String getTenSanPham();
    String getIdLaptop();
    String getThuongHieu();
    String getManHinh();
    String getHeDieuHanh();
    UUID getChiTietID();
    String getRAM();
    String getSSD();
    String getCPU();
    String getGPU();
    String getMauSac();
    BigDecimal getGiaBan();
    String getImage();
}
