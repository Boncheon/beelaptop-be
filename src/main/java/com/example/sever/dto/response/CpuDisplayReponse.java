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
public class CpuDisplayReponse {

     UUID id;
    
     String idCpu;
    
     String ten;
    
     String moTa;
    
     Integer trangThai;
}
