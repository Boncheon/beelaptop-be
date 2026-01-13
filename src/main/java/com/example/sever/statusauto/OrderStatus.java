package com.example.sever.statusauto;

import java.util.Arrays;

public enum OrderStatus {
    DRAFT(0),            // Đơn nháp (POS đang tạo)
    PENDING_CONFIRM(1),  // Chờ xác nhận
    CONFIRMED(2),        // Đã xác nhận
    PREPARING(3),        // Đang chuẩn bị hàng
    SHIPPING(4),         // Đang vận chuyển
    DELIVERED(5),        // Đã giao hàng
    COMPLETED(6),        // Hoàn tất
    CANCELED(7);         // Hủy

    private final int code;

    OrderStatus(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public static OrderStatus fromCode(Integer code) {
        if (code == null) return null;
        return Arrays.stream(values())
                .filter(s -> s.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown OrderStatus code: " + code));
    }
}
