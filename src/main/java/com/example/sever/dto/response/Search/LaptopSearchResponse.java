package com.example.sever.dto.response.Search;

import java.math.BigDecimal;
import java.util.UUID;

public interface LaptopSearchResponse {
    UUID getId();
    String getTenSanPham();
    UUID getIdSpct();
    BigDecimal getGiaBan();
    String getCpu();
    String getRam();
    String getSsd();
    String getGpu();
    String getImage();
}
