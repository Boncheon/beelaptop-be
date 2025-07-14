package com.example.sever.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KichThuocDisplayReponse {

     private UUID id;

     private String idKichThuoc;

     private Double chieuDai;

     private Double chieuRong;

     private Double chieuCao;

     private Double khoiLuong;
}
