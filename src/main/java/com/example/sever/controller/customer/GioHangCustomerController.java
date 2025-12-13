package com.example.sever.controller.customer;

import com.example.sever.dto.GioHangDTO.AddProductRequest;
import com.example.sever.dto.GioHangDTO.UpdateQuantityProductRequest;
import com.example.sever.dto.response.GioHang.CartResponse;
import com.example.sever.dto.response.GioHang.ProductCartResponse;
import com.example.sever.service.GioHangService;
import com.example.sever.service.LapTopCTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/laptops")
public class GioHangCustomerController {
    @Autowired
    private GioHangService gioHangService;
    @Autowired
    private LapTopCTService lapTopCTService;

    @PostMapping("/cart/add")
    public ResponseEntity<?> addProduct(@RequestBody AddProductRequest request) {
        try {
            CartResponse response = gioHangService.addProduct(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
    @GetMapping("/cart/{idTaiKhoan}")
    public ResponseEntity<?> getCart(@PathVariable UUID idTaiKhoan) {
        try {
            CartResponse response = gioHangService.getProductUser(idTaiKhoan);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Lỗi: " + e.getMessage());
        }
    }
    @PutMapping("/cart/update-cart-quantity")
    public ResponseEntity<?> capNhatSoLuong(@RequestBody UpdateQuantityProductRequest request) {
        try {
            gioHangService.updateQuantityProduct(request);
            return ResponseEntity.ok("Cập nhật số lượng thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
    @DeleteMapping("/cart/remove/{idGioHangCT}")
    public ResponseEntity<?> xoaSanPham(@PathVariable UUID idGioHangCT) {
        try {
            gioHangService.deleteProductCart(idGioHangCT);
            return ResponseEntity.ok("Xóa sản phẩm thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
    @PutMapping("/chon/{idGioHangCT}/{isSelected}")
    public ResponseEntity<?> capNhatTrangThaiChon(
            @PathVariable UUID idGioHangCT,
            @PathVariable Integer isSelected) {
        try {
            gioHangService.updateStatus(idGioHangCT, isSelected);
            return ResponseEntity.ok("Cập nhật trạng thái thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
    @GetMapping("/getCart-customer/{id}")
    public ResponseEntity<ProductCartResponse> getCartCustomer(@PathVariable UUID id) {
        ProductCartResponse productCartResponse = lapTopCTService.getProductForCustomer(id);
        return ResponseEntity.ok(productCartResponse);
    }
}
