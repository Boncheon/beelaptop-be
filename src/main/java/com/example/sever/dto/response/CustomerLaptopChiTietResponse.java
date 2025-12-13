package com.example.sever.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLaptopChiTietResponse {
    private UUID ctId;
    private UUID laptopId;
    private String productName;
    private BigDecimal price;
    private String display;
    private String resolution;
    private String ram;
    private String ssd;
    private String cpu;
    private String card;
    private String color;
    private List<String> images;
    private String description;
    private Integer trangThaiSeri;
}











