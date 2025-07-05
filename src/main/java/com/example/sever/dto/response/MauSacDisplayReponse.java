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

public class MauSacDisplayReponse {
     UUID id;
    
     String idMauSac;
    
     String ten;
    
     String moTa;
    
     Integer trangThai;

}
