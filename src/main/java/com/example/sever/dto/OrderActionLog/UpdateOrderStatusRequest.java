package com.example.sever.dto.OrderActionLog;


import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateOrderStatusRequest {
    private Integer newStatus; // 2..7
    private String note;       // ghi chú optional
}