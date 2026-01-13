package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;

import com.example.sever.dto.Pos.OrderListDTO;
import com.example.sever.dto.Pos.PageResult;
import com.example.sever.service.OrderManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/order-management")
@CrossOrigin("*")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','NHAN_VIEN')")
public class OrderManagementController {

    private final OrderManagementService orderManagementService;

//    @GetMapping("/orders")
//    public ResponseEntity<ApiResponse<PageResult<OrderListDTO>>> getOrders(
//            @RequestParam(required = false) String keyword,
//            @RequestParam(required = false) String loaiDon,
//            @RequestParam(required = false) Integer trangThaiDon,
//            @RequestParam(required = false) Integer trangThaiThanhToan,
//            @RequestParam(required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
//            @RequestParam(required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "newest") String sortType
//    ) {
//        Sort sort = "oldest".equalsIgnoreCase(sortType)
//                ? Sort.by("ngayTao").ascending()
//                : Sort.by("ngayTao").descending();
//
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        PageResult<OrderListDTO> data = orderManagementService.searchOrders(
//                keyword, loaiDon, trangThaiDon, trangThaiThanhToan,
//                fromDate, toDate, pageable
//        );
//
//        ApiResponse<PageResult<OrderListDTO>> res = ApiResponse.<PageResult<OrderListDTO>>builder()
//                .code(200)
//                .message("Lấy danh sách đơn hàng thành công")
//                .data(data)
//                .build();
//
//        return ResponseEntity.ok(res);
//    }
@GetMapping("/orders")
public ResponseEntity<ApiResponse<PageResult<OrderListDTO>>> getOrders(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String loaiDon,
        @RequestParam(required = false) Integer trangThaiDon,
        @RequestParam(required = false) Integer trangThaiThanhToan,

        // DÒNG MỚI – CHỈ DÙNG KHI MUỐN ẨN ĐƠN TẠI QUẦY CHƯA HOÀN THÀNH
        @RequestParam(required = false) List<Integer> trangThaiDonForTaiQuay,

    @RequestParam(required = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
    @RequestParam(required = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(defaultValue = "newest") String sortType
) {
        Sort sort = "oldest".equalsIgnoreCase(sortType)
                ? Sort.by("ngayTao").ascending()
                : Sort.by("ngayTao").descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        PageResult<OrderListDTO> data = orderManagementService.searchOrders(
                keyword, loaiDon, trangThaiDon, trangThaiThanhToan,
                trangThaiDonForTaiQuay,   // TRUYỀN THÊM VÀO SERVICE
                fromDate, toDate, pageable
        );

        return ResponseEntity.ok(ApiResponse.<PageResult<OrderListDTO>>builder()
                .code(200)
                .message("Lấy danh sách đơn hàng thành công")
                .data(data)
                .build());
    }
}

