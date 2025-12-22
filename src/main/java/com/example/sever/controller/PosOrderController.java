// com.example.sever.controller.PosOrderController
package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.OrderDTO.OrderRespone;
import com.example.sever.dto.Pos.*;
import com.example.sever.service.PosOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pos/orders")
@CrossOrigin("*")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PosOrderController {

    private final PosOrderService posOrderService;

    // 1. Tạo đơn nháp
    @PostMapping
    public ResponseEntity<ApiResponse<PosOrderDetailDTO>> createDraftOrder(
            @RequestBody PosCreateOrderRequest request) {

        PosOrderDetailDTO dto = posOrderService.createDraftOrder(request);
        return ResponseEntity.ok(
                ApiResponse.<PosOrderDetailDTO>builder()
                        .message("Tạo đơn hàng thành công")
                        .data(dto)
                        .build()
        );
    }


    // 2. Lấy chi tiết đơn
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<PosOrderDetailDTO>> getDetail(
            @PathVariable UUID orderId) {

        PosOrderDetailDTO dto = posOrderService.getDetail(orderId);
        return ResponseEntity.ok(
                ApiResponse.<PosOrderDetailDTO>builder()
                        .message("Lấy chi tiết đơn hàng thành công")
                        .data(dto)
                        .build()
        );
    }

    // 3. Thêm Seri (SPCT) vào đơn
    @PostMapping("/{orderId}/items")
    public ResponseEntity<ApiResponse<Void>> addItems(
            @PathVariable UUID orderId,
            @RequestBody PosAddItemsRequest request
    ) {
        posOrderService.addSeriToOrder(orderId, request);

        ApiResponse<Void> res = ApiResponse.<Void>builder()
                .code(200)
                .message("Thêm seri vào đơn hàng thành công")
                .build();

        return ResponseEntity.ok(res);
    }
    // QUÉT QR: Thêm sản phẩm bằng mã seri (String)
    @PostMapping("/{orderId}/items/by-seri-code")
    public ResponseEntity<ApiResponse<PosOrderDetailDTO>> addItemsBySeriCode(
            @PathVariable UUID orderId,
            @RequestBody List<String> seriCodes) {

        PosOrderDetailDTO dto = posOrderService.addItemsBySeriCode(orderId, seriCodes);

        return ResponseEntity.ok(
                ApiResponse.<PosOrderDetailDTO>builder()
                        .message("Thêm sản phẩm bằng mã seri thành công")
                        .data(dto)
                        .build()
        );
    }


    // 4. Xoá 1 dòng Seri khỏi đơn
    @DeleteMapping("/{orderId}/items/{orderCtId}")
    public ResponseEntity<ApiResponse<PosOrderDetailDTO>> removeItem(
            @PathVariable UUID orderId,
            @PathVariable UUID orderCtId) {

        PosOrderDetailDTO dto = posOrderService.removeItem(orderId, orderCtId);
        return ResponseEntity.ok(
                ApiResponse.<PosOrderDetailDTO>builder()
                        .message("Xoá sản phẩm khỏi đơn thành công")
                        .data(dto)
                        .build()
        );
    }

    // 5. Chọn/đổi khách hàng
    @PutMapping("/{orderId}/customer")
    public ResponseEntity<ApiResponse<PosOrderDetailDTO>> selectCustomer(
            @PathVariable UUID orderId,
            @RequestBody PosSelectCustomerRequest request) {

        PosOrderDetailDTO dto = posOrderService.selectCustomer(orderId, request);
        return ResponseEntity.ok(
                ApiResponse.<PosOrderDetailDTO>builder()
                        .message("Cập nhật khách hàng cho đơn thành công")
                        .data(dto)
                        .build()
        );
    }

    // 6. Áp voucher
    @PostMapping("/{orderId}/voucher")
    public ResponseEntity<ApiResponse<OrderRespone>> applyVoucher(
            @PathVariable("orderId") UUID orderId,
            @RequestBody PosApplyVoucherRequest request
    ) {
        OrderRespone data = posOrderService.applyVoucher(orderId, request);

        ApiResponse<OrderRespone> res = ApiResponse.<OrderRespone>builder()
                .code(200)
                .message("Áp dụng voucher thành công")
                .data(data)
                .build();

        return ResponseEntity.ok(res);
    }



    // 7. Thêm thanh toán
    @PostMapping("/{orderId}/payments")
    public ResponseEntity<ApiResponse<PosOrderDetailDTO>> addPayment(
            @PathVariable UUID orderId,
            @RequestBody PosAddPaymentRequest request) {

        PosOrderDetailDTO dto = posOrderService.addPayment(orderId, request);
        return ResponseEntity.ok(
                ApiResponse.<PosOrderDetailDTO>builder()
                        .message("Thêm thanh toán thành công")
                        .data(dto)
                        .build()
        );
    }

    // 8. Hoàn tất đơn
    @PostMapping("/{orderId}/complete")
    public ResponseEntity<ApiResponse<PosOrderDetailDTO>> complete(
            @PathVariable UUID orderId) {

        PosOrderDetailDTO dto = posOrderService.complete(orderId);
        return ResponseEntity.ok(
                ApiResponse.<PosOrderDetailDTO>builder()
                        .message("Xác nhận thanh toán & hoàn tất đơn thành công")
                        .data(dto)
                        .build()
        );
    }

    // 9. Huỷ đơn
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<PosOrderDetailDTO>> cancel(
            @PathVariable UUID orderId) {

        PosOrderDetailDTO dto = posOrderService.cancel(orderId);
        return ResponseEntity.ok(
                ApiResponse.<PosOrderDetailDTO>builder()
                        .message("Huỷ đơn hàng thành công")
                        .data(dto)
                        .build()
        );
    }
}
