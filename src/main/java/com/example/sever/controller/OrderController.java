package com.example.sever.controller;


import com.example.sever.dto.OrderDTO.OrderRequest;
import com.example.sever.dto.OrderDTO.OrderRespone;
import com.example.sever.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/orders")
public class OrderController {


    @Autowired
    private OrderService orderService;

    @PostMapping("/test")
    public ResponseEntity<OrderRespone> taoDon(@RequestBody OrderRequest req) {
        return ResponseEntity.ok(orderService.taoDonHang(req));
    }





}
