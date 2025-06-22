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
public class CpuUpdateRequestDTO {

     UUID id;
    
     String idCpu;
    
     String ten;
    
     String moTa;
    
     Integer trangThai;
}
