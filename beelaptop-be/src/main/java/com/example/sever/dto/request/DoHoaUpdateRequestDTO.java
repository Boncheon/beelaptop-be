package com.example.sever.dto.request;

import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;


import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DoHoaUpdateRequestDTO {
    UUID  id;
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

}
