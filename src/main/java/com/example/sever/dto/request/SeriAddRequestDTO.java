package com.example.sever.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriAddRequestDTO {

     private UUID idLaptopCt;          // id_lap_top_ct

     // Danh sách seri cần thêm
     private List<SeriItemDTO> list;

     @Data
     public static class SeriItemDTO {
          private String idSeri;
          private  Integer trangThai;
//          private Long giaGoc;          // optional
//          private String nguonSeri;     // optional
     }
}