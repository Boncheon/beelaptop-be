package com.example.sever.dto.Pos.GHN;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GhnFeeRequest {
    private Integer toDistrictId;
    private String toWardCode;

    private Integer weight ;
    private Integer length ;
    private Integer width ;
    private Integer height ;

    private Integer insuranceValue ;
}
