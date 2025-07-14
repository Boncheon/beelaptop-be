package com.example.sever.dto.request;

import com.example.sever.entity.PhienBan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriAddRequestDTO {

     private String idSeri;

     private UUID idPhienBan;

     private BigDecimal giaGoc;

     private String nguonSeri;
     private Integer trangThai;
}
