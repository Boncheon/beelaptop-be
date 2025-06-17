package com.example.sever.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoHoaRequestDTO {
    private UUID id;
    @Size(max = 20)
    private String idDohoa;

    @Size(max = 255)
    private String hangcardOboard;

    @Size(max = 255)
    private String modelcardOboard;

    @Size(max = 255)
    private String tenDayDu;

    @Size(max = 100)
    private String loaiCard;

    @Size(max = 50)
    private String boNhoRam;

    @Size(max = 500)
    private String moTa;

    @ColumnDefault("0")
    private Integer trangThai;
}
