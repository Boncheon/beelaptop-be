package com.example.sever.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManHinhAddRequestDTO {


     private String ma;

     private String doPhanGiai;

     private Integer tanSoQuet;

     private Double kichThuoc;
}
