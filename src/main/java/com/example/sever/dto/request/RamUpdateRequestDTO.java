package com.example.sever.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RamUpdateRequestDTO {
     UUID id;

     String idLoaiRam;

     String dungLuongRam;

     Integer bus;

     String moTa;
}
