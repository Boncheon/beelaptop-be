package com.example.sever.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoHoaAddRequestDTO {

    @Size(max = 20)
     String idDohoa;

    @Size(max = 255)
     String hangcardOboard;

    @Size(max = 255)
    
 String modelcardOboard;

    @Size(max = 255)
    
 String tenDayDu;

    @Size(max = 100)
    
 String loaiCard;

    @Size(max = 50)
    
 String boNhoRam;

    @Size(max = 500)
    
 String moTa;

    @ColumnDefault("0")
    
 Integer trangThai;
}
