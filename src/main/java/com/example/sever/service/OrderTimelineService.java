package com.example.sever.service;

import com.example.sever.dto.OrderActionLog.OrderTimelineResponse;
import com.example.sever.dto.OrderActionLog.UpdateOrderStatusRequest;

import java.util.UUID;

public interface OrderTimelineService {
    OrderTimelineResponse getTimeline(UUID orderId);
    OrderTimelineResponse updateStatus(UUID orderId, UpdateOrderStatusRequest request);


}