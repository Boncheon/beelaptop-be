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
public class LapTopCTAutoGenRequestDTO {

    // Laptop cha
    private UUID idLaptop;

    // Mã nhóm biến thể (nếu ông đang dùng idLaptopCT như group code)
    private String idLaptopCT;

    // Danh sách ID các option
    private List<UUID> idRams;
    private List<UUID> idSsds;
    private List<UUID> idCpus;
    private List<UUID> idDohoas;
    private List<UUID> idMauSacs;

    // Thông tin chung cho tất cả biến thể
    private BigDecimal giaBan;
    private String moTa;
    private Integer trangThai;
    private String ghiChu;
}
