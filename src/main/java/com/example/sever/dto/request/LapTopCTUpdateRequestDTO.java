package com.example.sever.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LapTopCTUpdateRequestDTO {
     private UUID id;   // ID biến thể – bắt buộc phải có
     private String idLaptopCT;
     private UUID idRam;
     private UUID idSsd;
     private UUID idCpu;
     private UUID idDohoa;
     private UUID idMauSac;

     private BigDecimal giaBan;

     @Size(max = 500)
     private String moTa;

     private Integer trangThai;

     private String ghiChu;
}
