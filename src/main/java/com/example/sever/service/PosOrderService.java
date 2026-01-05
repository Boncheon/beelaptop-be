// com.example.sever.service.PosOrderService
package com.example.sever.service;

import com.example.sever.dto.OrderDTO.OrderRespone;
import com.example.sever.dto.Pos.*;
import com.example.sever.dto.Pos.GHN.PosUpdateShippingRequest;


import java.util.List;
import java.util.UUID;

public interface PosOrderService {

    PosOrderDetailDTO createDraftOrder(PosCreateOrderRequest request);

    PosOrderDetailDTO addItems(UUID orderId, PosAddItemsRequest request);

    PosOrderDetailDTO removeItem(UUID orderId, UUID orderCtId);

    PosOrderDetailDTO selectCustomer(UUID orderId, PosSelectCustomerRequest request);

    OrderRespone applyVoucher(UUID orderId, PosApplyVoucherRequest request);

    PosOrderDetailDTO addPayment(UUID orderId, PosAddPaymentRequest request);

    PosOrderDetailDTO complete(UUID orderId);

    PosOrderDetailDTO cancel(UUID orderId);

    PosOrderDetailDTO getDetail(UUID orderId);
    void addSeriToOrder(UUID orderId, PosAddItemsRequest request);

    PosOrderDetailDTO addItemsBySeriCode(UUID orderId, List<String> seriCodes);

    void clearVoucher(UUID orderId);
    PosOrderDetailDTO updateShipping(UUID orderId, PosUpdateShippingRequest req);


}
