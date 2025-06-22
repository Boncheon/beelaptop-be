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
public class HeDieuHanhUpdateRequestDTO {
     private UUID id ;

     private String ma;

     private String ten;

     private String phienBan;

}
