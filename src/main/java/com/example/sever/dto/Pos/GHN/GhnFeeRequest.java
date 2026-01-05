package com.example.sever.dto.Pos.GHN;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GhnFeeRequest {
    private Integer toDistrictId;
    private String toWardCode;

    private Integer weight = 500;
    private Integer length = 20;
    private Integer width  = 15;
    private Integer height = 10;

    private Integer insuranceValue = 0;
}
