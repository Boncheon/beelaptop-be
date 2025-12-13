package com.example.sever.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public interface ListLaptopCustomerProjection {
    UUID getLaptopChiTietID();
    UUID getLaptopID();
    String getTenSanPham();
    String getCpu();
    String getRam();
    String getSsd();
    String getCard();
    String getDisplay();
    String getResolution();
    BigDecimal getGiaBan();
    Integer getSoLuong();
    String getAnhDaiDien();
}
