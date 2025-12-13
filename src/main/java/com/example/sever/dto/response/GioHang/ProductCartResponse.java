package com.example.sever.dto.response.GioHang;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductCartResponse {
    UUID getId();
    String getName();
    String getColor();
    String getCpu();
    String getRam();
    String getSsd();
    String getCard();
    BigDecimal getPrice();
    Integer getQuantity();
    String getImage();
}
