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
public class RomUpdateRequestDTO {

    UUID id;

    String idSsd;

    String dungLuongSsd;

    String loaiSsd;

    Integer tocDoDoc;

    Integer tocDoGhi;

    String moTa;

}
