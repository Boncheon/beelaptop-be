package com.example.sever.dto.request;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RamAddRequestDTO {

    @Size(max = 20)
     String idLoaiRam;

    @Size(max = 50)
     String dungLuongRam;

     Integer bus;

    @Size(max = 500)
     String moTa;

    @ColumnDefault("0")
     Integer trangThai;

}
