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
public class PhienBanBatchCreateDTO {

    private UUID idLaptopChiTiet;
    private String baseIdPhienBan;
    private String moTa;
    private Integer trangThai;
    private BigDecimal baseGiaBan;
    private String nguoiTao;
    private List<PhienBanAddRequestDTO> combinations;

}
