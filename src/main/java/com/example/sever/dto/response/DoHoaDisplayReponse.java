package com.example.sever.dto.response;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

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
