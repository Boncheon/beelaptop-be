package com.example.sever.dto.response;

import java.math.BigDecimal;
import java.util.UUID;


public interface CustomerLaptopChiTietProject {
    UUID getCtId();
    UUID getLaptopId();
    String getProductName();
    BigDecimal getPrice();
    String getDisplay();
    String getResolution();
    String getRam();
    String getSsd();
    String getCpu();
    String getCard();
    String getColor();
    String getImage();
    String getDescription();
}
