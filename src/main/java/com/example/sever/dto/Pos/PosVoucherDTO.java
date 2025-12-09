package com.example.sever.dto.Pos;


import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosVoucherDTO {

    private String idPhieuGiamGia;   // mã voucher (ID_PhieuGiamGia)
    private String ten;
    private String kieuGiamGia;      // GIAM_PHAN_TRAM / GIAM_CO_DINH
    private BigDecimal giaTriGiam;   // số tiền thực tế đang giảm cho đơn này
    private BigDecimal giaTriMin;
    private BigDecimal giaTriMax;
    private Integer trangThai;
}