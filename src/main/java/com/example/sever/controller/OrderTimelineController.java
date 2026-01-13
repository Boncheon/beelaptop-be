package com.example.sever.controller;


import com.example.sever.dto.OrderActionLog.OrderTimelineResponse;
import com.example.sever.dto.OrderActionLog.UpdateOrderStatusRequest;
import com.example.sever.service.OrderTimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@PreAuthorize("hasAnyRole('ADMIN','NHAN_VIEN')")
public class OrderTimelineController {

    private final OrderTimelineService timelineService;

    @GetMapping("/{orderId}/timeline")
    public OrderTimelineResponse getTimeline(@PathVariable UUID orderId) {
        return timelineService.getTimeline(orderId);
    }

    // admin/nhân viên bấm “Xác nhận đơn / chuyển bước”
    @PutMapping("/{orderId}/status")
    public OrderTimelineResponse updateStatus(
            @PathVariable UUID orderId,
            @RequestBody UpdateOrderStatusRequest request
    ) {
        return timelineService.updateStatus(orderId, request);
    }


}