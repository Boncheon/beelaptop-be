package com.example.sever.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface PhieuGiamGiaCustomerProjection {
    UUID getId();
    String getMaGiamGia();
    String getTen();
    String getKieuGiamGia();
    BigDecimal getGiaTriGiam();
    LocalDateTime getNgayBatDau();
    LocalDateTime getNgayKetThuc();
    BigDecimal getGiaTriMin();
    BigDecimal getGiaTriMax();
}
