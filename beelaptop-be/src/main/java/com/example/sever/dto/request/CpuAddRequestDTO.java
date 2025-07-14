package com.example.sever.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CpuAddRequestDTO {

     String idCpu;
    
     String ten;
    
     String moTa;
    
     Integer trangThai;
}
