package com.example.sever.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RomAddRequestDTO {
    String idSsd;

    String dungLuongSsd;

    String loaiSsd;

    Integer tocDoDoc;

    Integer tocDoGhi;

    String moTa;

    Integer trangThai;
}
