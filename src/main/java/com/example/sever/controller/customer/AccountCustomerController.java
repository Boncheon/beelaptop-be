package com.example.sever.controller.customer;

import com.example.sever.dto.AccountDTO.DiaChiCreateRequest;
import com.example.sever.dto.AccountDTO.DiaChiUpdateRequest;
import com.example.sever.dto.AccountDTO.UpdateProfileCustomerRequest;
import com.example.sever.dto.AccountDTO.UpdateProfileCustomerResponse;
import com.example.sever.dto.response.DiaChi.DiaChiProjection;
import com.example.sever.dto.response.DiaChi.DiaChiResponse;
import com.example.sever.entity.TaiKhoan;
import com.example.sever.repository.TaiKhoanRepository;
import com.example.sever.service.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/laptops")
@CrossOrigin(origins = "*")
public class AccountCustomerController {

    @Autowired
    private TaiKhoanService taiKhoanService;

    // ✅ ADD: để support principal dạng String (email/phone) khi dùng JWT
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    // =========================
    // Helpers
    // =========================
    private UUID getCurrentUserIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        Object principal = auth.getPrincipal();

        // Case 1: principal là entity TaiKhoan (implements UserDetails)
        if (principal instanceof TaiKhoan tk) {
            return tk.getId();
        }

        // Case 2: principal là String (email/phone/username)
        if (principal instanceof String s) {
            String username = s.trim();
            if (username.isEmpty() || "anonymousUser".equalsIgnoreCase(username)) {
                return null;
            }

            // ưu tiên tìm theo email, fallback theo SĐT nếu repo có
            Optional<TaiKhoan> byEmail = taiKhoanRepository.findByEmail(username);
            if (byEmail.isPresent()) return byEmail.get().getId();

            try {
                Optional<TaiKhoan> byPhone = taiKhoanRepository.findBySoDienThoai(username);
                if (byPhone.isPresent()) return byPhone.get().getId();
            } catch (Exception ignored) {
                // nếu repo không có findBySoDienThoai dạng Optional thì bỏ qua
            }
        }

        return null;
    }

    private UUID requireCurrentUserId() {
        UUID id = getCurrentUserIdOrNull();
        if (id == null) {
            throw new RuntimeException("Bạn chưa đăng nhập hoặc phiên không hợp lệ");
        }
        return id;
    }

    // =========================
    // Address APIs
    // =========================

    // ✅ NEW (chuẩn hơn): không cần truyền id user
    @GetMapping("/get-all-address")
    public ResponseEntity<List<DiaChiProjection>> getAllAddressMe() {
        UUID userId = getCurrentUserIdOrNull();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        List<DiaChiProjection> list = taiKhoanService.findAllAdress(userId);
        return ResponseEntity.ok(list);
    }

    // ✅ GIỮ API CŨ (để FE không gãy):
    // - BẮT BUỘC login
    // - IGNORE id trên URL, luôn lấy theo current user (tránh lộ dữ liệu)
    @GetMapping("/get-all-address/{id}")
    public ResponseEntity<List<DiaChiProjection>> getAllAddress(@PathVariable UUID id) {
        UUID currentUserId = getCurrentUserIdOrNull();
        if (currentUserId == null) {
            return ResponseEntity.status(401).build();
        }
        List<DiaChiProjection> list = taiKhoanService.findAllAdress(currentUserId);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/address/add")
    public ResponseEntity<DiaChiResponse> addAddressCustomer(@RequestBody DiaChiCreateRequest request) {
        UUID userId = getCurrentUserIdOrNull();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        // ép theo user đang login
        request.setIdTaiKhoan(userId);
        return ResponseEntity.ok(taiKhoanService.createAddressCustomer(request));
    }

    @PutMapping("/address/update/{id}")
    public ResponseEntity<DiaChiResponse> updateAddressCustomer(
            @PathVariable UUID id,
            @RequestBody DiaChiUpdateRequest request) {

        // ✅ BẮT BUỘC login (ownership check nên để trong service)
        UUID userId = getCurrentUserIdOrNull();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(taiKhoanService.updateAddressCustomer(id, request));
    }

    @PutMapping("/address/set-default/{id}")
    public ResponseEntity<DiaChiResponse> setDefault(@PathVariable UUID id) {
        UUID userId = getCurrentUserIdOrNull();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(taiKhoanService.setAddressDefaultCustomer(id));
    }

    @DeleteMapping("/address/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        UUID userId = getCurrentUserIdOrNull();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        taiKhoanService.deleteAddressCustomer(id);
        return ResponseEntity.ok("Đã xóa địa chỉ thành công!");
    }

    // =========================
    // Profile APIs
    // =========================

    // ✅ NEW (chuẩn hơn): không cần truyền id user
    @PutMapping("/account/update")
    public ResponseEntity<UpdateProfileCustomerResponse> updateProfileCustomerMe(
            @RequestBody UpdateProfileCustomerRequest request) {

        UUID userId = getCurrentUserIdOrNull();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        UpdateProfileCustomerResponse response = taiKhoanService.updateProfileCustomer(userId, request);
        return ResponseEntity.ok(response);
    }

    // ✅ GIỮ API CŨ (để FE không gãy):
    // - BẮT BUỘC login
    // - IGNORE id trên URL, luôn update theo current user
    @PutMapping("/account/update/{id}")
    public ResponseEntity<UpdateProfileCustomerResponse> updateProfileCustomer(
            @PathVariable UUID id,
            @RequestBody UpdateProfileCustomerRequest request) {

        UUID currentUserId = getCurrentUserIdOrNull();
        if (currentUserId == null) {
            return ResponseEntity.status(401).build();
        }
        UpdateProfileCustomerResponse response = taiKhoanService.updateProfileCustomer(currentUserId, request);
        return ResponseEntity.ok(response);
    }

    // ✅ NEW (chuẩn hơn): form-data không cần truyền id user
    @PutMapping("/account/update-form")
    public ResponseEntity<UpdateProfileCustomerResponse> updateProfileCustomerWithFileMe(
            @ModelAttribute UpdateProfileCustomerRequest request) {

        UUID userId = getCurrentUserIdOrNull();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        UpdateProfileCustomerResponse response = taiKhoanService.updateProfileCustomer(userId, request);
        return ResponseEntity.ok(response);
    }

    // ✅ GIỮ API CŨ (để FE không gãy):
    // - BẮT BUỘC login
    // - IGNORE id trên URL
    @PutMapping("/account/update-form/{id}")
    public ResponseEntity<UpdateProfileCustomerResponse> updateProfileCustomerWithFile(
            @PathVariable UUID id,
            @ModelAttribute UpdateProfileCustomerRequest request) {

        UUID currentUserId = getCurrentUserIdOrNull();
        if (currentUserId == null) {
            return ResponseEntity.status(401).build();
        }
        UpdateProfileCustomerResponse response = taiKhoanService.updateProfileCustomer(currentUserId, request);
        return ResponseEntity.ok(response);
    }
}
