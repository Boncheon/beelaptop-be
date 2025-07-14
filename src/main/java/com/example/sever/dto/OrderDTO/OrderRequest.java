package com.example.sever.dto.OrderDTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    private String tenKhachHang;
    private BigDecimal tongTien;


}
