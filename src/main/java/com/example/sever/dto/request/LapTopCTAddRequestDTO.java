package com.example.sever.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LapTopCTAddRequestDTO {

     @NotNull
     private UUID idLaptop;      // FK Laptop base
     @NotNull
     private String idLaptopCT;
     @NotNull
     private UUID idRam;

     @NotNull
     private UUID idSsd;

     @NotNull
     private UUID idCpu;

     @NotNull
     private UUID idDohoa;

     @NotNull
     private UUID idMauSac;

     @NotNull
     private BigDecimal giaBan;

     @Size(max = 500)
     private String moTa;

     private Integer trangThai;

     private String ghiChu;
}
