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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RamUpdateRequestDTO {
     UUID id;

     String idLoaiRam;

     String dungLuongRam;

     Integer bus;

     String moTa;
}
