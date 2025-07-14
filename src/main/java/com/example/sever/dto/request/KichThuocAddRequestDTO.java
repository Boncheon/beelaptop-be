package com.example.sever.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KichThuocAddRequestDTO {

     private String idKichThuoc;

     private Double chieuDai;

     private Double chieuRong;

     private Double chieuCao;

     private Double khoiLuong;
}
