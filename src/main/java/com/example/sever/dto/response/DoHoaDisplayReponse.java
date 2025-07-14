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
public class DoHoaDisplayReponse {
    private UUID id;
    private String idDohoa;
    private String hangcardOboard;
    private String modelcardOboard;
    private String tenDayDu;
    private String loaiCard;
    private String boNhoRam;
    private String moTa;
    private Integer trangThai;
}
