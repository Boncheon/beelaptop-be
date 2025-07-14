package com.example.sever.dto.DotGiamGiaDTO;

import com.example.sever.entity.DotGiamGia;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class DotGiamGiaChiTietRequest {



    private String kieuGiamGia;

    private BigDecimal giaTriGiam;

    private DotGiamGia dotGiamGia;



}
