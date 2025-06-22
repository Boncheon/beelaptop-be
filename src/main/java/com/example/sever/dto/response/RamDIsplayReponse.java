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

public class RamDIsplayReponse {
     UUID id;
     String idLoaiRam;
     String dungLuongRam;
     Integer bus;
     String moTa;
     Integer trangThai;

}
