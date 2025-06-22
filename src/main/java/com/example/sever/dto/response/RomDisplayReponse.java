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

public class RomDisplayReponse {

    UUID id;

    String idSsd;

    String dungLuongSsd;

    String loaiSsd;

    Integer tocDoDoc;

    Integer tocDoGhi;

    String moTa;

    Integer trangThai;

}
