package com.example.sever.dto.DotGiamGia;

import com.example.sever.KieuGiamGia;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class DotGiamGiaChiTietDTO {

    private String idDotGiamGiaCT;
    private UUID idDotGiamGia;     // nhận từ FE
    private UUID idLaptopChiTiet;  // sản phẩm
    private KieuGiamGia kieuGiamGia;
    private BigDecimal giaTriGiam;


}
