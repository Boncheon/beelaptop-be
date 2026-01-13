package com.example.sever.dto.OrderActionLog;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class OrderTimelineResponse {
    private UUID orderId;
    private String maDonHang;

    private String loaiDon;

    private Integer trangThai;
    private String tenTrangThai;

    private Integer trangThaiThanhToan;
    private String tenTrangThaiThanhToan;

    private List<TimelineStepDTO> steps;
    private List<TimelineLogDTO> logs;

    @Getter @Setter @AllArgsConstructor
    public static class TimelineStepDTO {
        private Integer code;      // 1..6 (7 là cancel)
        private String title;      // tên bước
        private String state;      // DONE | CURRENT | UPCOMING | CANCELLED
    }

    @Getter @Setter @AllArgsConstructor
    public static class TimelineLogDTO {
        private Instant time;
        private String title;        // ví dụ: "Đơn hàng được tạo"
        private String description;  // mô tả chi tiết
        private String by;           // "Hệ thống" / "Nhân viên A"
    }
}