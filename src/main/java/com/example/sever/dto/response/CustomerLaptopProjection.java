package com.example.sever.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;


public interface CustomerLaptopProjection {
        UUID getLaptopID();
        String getProductName();
        String getCpu();
        String getMeMoRy();
        String getSsd();
        String getCard();
        String getDisplay();
        String getResolution();
        BigDecimal getPrice();
        String getImage();
}
