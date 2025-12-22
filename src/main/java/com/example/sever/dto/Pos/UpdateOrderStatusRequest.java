// src/main/java/com/example/sever/dto/Pos/UpdateOrderStatusRequest.java
package com.example.sever.dto.Pos;

import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    private Integer trangThaiMoi;  // ví dụ 1: mới, 2: đã xác nhận, 3: đang giao,...
    private String lyDo;           // lý do / mô tả nhập trong popup
}
