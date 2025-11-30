package com.example.sever.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ThongKeResponseDTO {
    private Integer label;          // ngày hoặc tháng
    private BigDecimal value1;      // năm 1 hoặc tháng 1
    private BigDecimal value2;      // năm 2 hoặc tháng 2
    private List<TopLaptopBanChayResponseDTO> topLaptop1;  // Top 10 laptop của năm/tháng/ngày 1
    private List<TopLaptopBanChayResponseDTO> topLaptop2;  // Top 10 laptop của năm/tháng/ngày 2
}


