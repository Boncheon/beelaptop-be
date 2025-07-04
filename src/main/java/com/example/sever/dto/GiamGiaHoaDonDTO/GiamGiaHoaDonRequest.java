package com.example.sever.dto.GiamGiaHoaDonDTO;

import com.example.sever.enity.PhieuGiamGia;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class GiamGiaHoaDonRequest {

    private String idGiamgiahoadon;

    private String tenPhieu;




}
