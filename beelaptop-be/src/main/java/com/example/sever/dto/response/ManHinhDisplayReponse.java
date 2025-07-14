package com.example.sever.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ManHinhDisplayReponse {

     private UUID id ;

     private String ma;

     private String doPhanGiai;

     private Integer tanSoQuet;

     private Double kichThuoc;

}
