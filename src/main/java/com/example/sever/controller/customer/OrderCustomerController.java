package com.example.sever.controller.customer;

import com.example.sever.dto.OrderDTO.*;
import com.example.sever.exception.ResourceNotFoundException;
import com.example.sever.service.OrderCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/laptops")
@CrossOrigin(origins = "*")
public class OrderCustomerController {

    @Autowired
    private OrderCustomerService orderCustomerService;

    @PostMapping("/order/create")
    public ResponseEntity<OrderCustomerResponse> taoDonHangCustomer(@RequestBody OrderCustomerRequest request) {
        try {
            OrderCustomerResponse response = orderCustomerService.taoDonHangCustomer(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            OrderCustomerResponse errorResponse = new OrderCustomerResponse();
            errorResponse.setMessage("Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            OrderCustomerResponse errorResponse = new OrderCustomerResponse();
            errorResponse.setMessage("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/order/list/{idTaiKhoan}")
    public ResponseEntity<?> getDanhSachDonHangCustomer(@PathVariable UUID idTaiKhoan) {
        try {
            List<OrderListCustomerResponse> responseList = orderCustomerService.getDanhSachDonHangByTaiKhoanCustomer(idTaiKhoan);
            return ResponseEntity.ok(responseList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @GetMapping("/order/{idOrder}/products")
    public ResponseEntity<?> getDanhSachSanPhamByOrderCustomer(@PathVariable UUID idOrder) {
        try {
            List<OrderProductCustomerResponse> responseList = orderCustomerService.getDanhSachSanPhamByOrderCustomer(idOrder);
            return ResponseEntity.ok(responseList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @PostMapping("/order/search")
    public ResponseEntity<?> timKiemDonHangCustomer(@RequestBody SearchOrderCustomerRequest request) {
        try {
            List<OrderDetailCustomerResponse> responseList = orderCustomerService.timKiemDonHangCustomer(request);
            return ResponseEntity.ok(responseList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @PutMapping("/order/{idOrder}/cancel")
    public ResponseEntity<OrderCustomerResponse> huyDonHangCustomer(
            @PathVariable UUID idOrder,
            @RequestParam UUID idTaiKhoan) {
        try {
            OrderCustomerResponse response = orderCustomerService.huyDonHangCustomer(idOrder, idTaiKhoan);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            OrderCustomerResponse errorResponse = new OrderCustomerResponse();
            errorResponse.setMessage("Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (ResourceNotFoundException e) {
            OrderCustomerResponse errorResponse = new OrderCustomerResponse();
            errorResponse.setMessage("Lỗi: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            OrderCustomerResponse errorResponse = new OrderCustomerResponse();
            errorResponse.setMessage("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}


