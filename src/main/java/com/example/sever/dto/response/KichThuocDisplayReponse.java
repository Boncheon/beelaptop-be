package com.example.sever.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KichThuocDisplayReponse {

     private UUID id;

     private String idKichThuoc;

     private Double chieuDai;

     private Double chieuRong;

     private Double chieuCao;

     private Double khoiLuong;
}
