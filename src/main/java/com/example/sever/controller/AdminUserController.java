package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.AccountDTO.DiaChiCreateRequest;
import com.example.sever.dto.AccountDTO.DiaChiUpdateRequest;
import com.example.sever.dto.request.UserCreationRequest;
import com.example.sever.dto.response.DiaChi.DiaChiProjection;
import com.example.sever.dto.response.DiaChi.DiaChiResponse;
import com.example.sever.dto.response.UserDetailResponse;
import com.example.sever.service.TaiKhoanService;
import com.example.sever.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminUserController {

    UserService userService;
    TaiKhoanService taiKhoanService; // ← ĐÃ CÓ SẴN TRONG SERVICE IMPL

    // ===== QUẢN LÝ TÀI KHOẢN (GIỮ NGUYÊN NHƯ CŨ) =====
    @PostMapping("/users/create-employee")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserDetailResponse> createEmployee(@Valid @ModelAttribute UserCreationRequest request) {
        log.info("Tạo tài khoản nhân viên với email: {}", request.getEmail());
        UserDetailResponse response = userService.createEmployee(request);
        return ApiResponse.<UserDetailResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Tạo tài khoản nhân viên thành công. Mật khẩu đã được gửi qua email.")
                .data(response)
                .build();
    }

    @PostMapping("/users/create-customer")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserDetailResponse> createCustomer(@Valid @ModelAttribute UserCreationRequest request) {
        log.info("Tạo tài khoản khách hàng với email: {}", request.getEmail());
        UserDetailResponse response = userService.createCustomer(request);
        return ApiResponse.<UserDetailResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Tạo tài khoản khách hàng thành công. Mật khẩu đã được gửi qua email.")
                .data(response)
                .build();
    }

    @PutMapping("/users/update-employee/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserDetailResponse> updateEmployee(@PathVariable String id, @Valid @ModelAttribute UserCreationRequest request) {
        log.info("Cập nhật tài khoản nhân viên với ID: {}", id);
        UserDetailResponse response = userService.updateEmployee(id, request);
        return ApiResponse.<UserDetailResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Cập nhật tài khoản nhân viên thành công.")
                .data(response)
                .build();
    }

    @PutMapping("/users/update-customer/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserDetailResponse> updateCustomer(@PathVariable String id, @Valid @ModelAttribute UserCreationRequest request) {
        log.info("Cập nhật tài khoản khách hàng với ID: {}", id);
        UserDetailResponse response = userService.updateCustomer(id, request);
        return ApiResponse.<UserDetailResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Cập nhật tài khoản khách hàng thành công.")
                .data(response)
                .build();
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserDetailResponse> getUserDetail(@PathVariable String id) {
        log.info("Lấy thông tin chi tiết tài khoản với ID: {}", id);
        UserDetailResponse response = userService.getUserDetail(id);
        return ApiResponse.<UserDetailResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy thông tin tài khoản thành công.")
                .data(response)
                .build();
    }

    @GetMapping("/users/by-role/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserDetailResponse>> getUsersByRole(@PathVariable String roleId) {
        log.info("Lấy danh sách tài khoản với vai trò: {}", roleId);
        List<UserDetailResponse> response = userService.getUsersByRole(roleId);
        return ApiResponse.<List<UserDetailResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách tài khoản theo vai trò thành công.")
                .data(response)
                .build();
    }

    @PatchMapping("/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserDetailResponse> toggleUserStatus(@PathVariable String id) {
        log.info("Chuyển trạng thái tài khoản với ID: {}", id);
        UserDetailResponse response = userService.toggleUserStatus(id);
        return ApiResponse.<UserDetailResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Chuyển trạng thái tài khoản thành công.")
                .data(response)
                .build();
    }

    // ===== MỚI: QUẢN LÝ ĐỊA CHỈ KHÁCH HÀNG (CHO BÁN HÀNG ONLINE + POS) =====

    /**
     * Lấy danh sách địa chỉ của một khách hàng
     */
    @GetMapping("/address/customer/{taiKhoanId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<DiaChiProjection>> getAddressesByCustomer(@PathVariable UUID taiKhoanId) {
        log.info("Lấy danh sách địa chỉ của khách hàng ID: {}", taiKhoanId);
        List<DiaChiProjection> addresses = taiKhoanService.findAllAdress(taiKhoanId);
        return ApiResponse.<List<DiaChiProjection>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách địa chỉ thành công")
                .data(addresses)
                .build();
    }

    /**
     * Tạo địa chỉ mới cho khách hàng
     */
    @PostMapping("/address/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DiaChiResponse> createAddress(@Valid @RequestBody DiaChiCreateRequest request) {
        log.info("Tạo địa chỉ mới cho tài khoản ID: {}", request.getIdTaiKhoan());
        DiaChiResponse response = taiKhoanService.createAddressCustomer(request);
        return ApiResponse.<DiaChiResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Tạo địa chỉ thành công")
                .data(response)
                .build();
    }

    /**
     * Cập nhật địa chỉ
     */
    @PutMapping("/address/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DiaChiResponse> updateAddress(
            @PathVariable UUID id,
            @Valid @RequestBody DiaChiUpdateRequest request) {
        log.info("Cập nhật địa chỉ ID: {}", id);
        DiaChiResponse response = taiKhoanService.updateAddressCustomer(id, request);
        return ApiResponse.<DiaChiResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Cập nhật địa chỉ thành công")
                .data(response)
                .build();
    }

    /**
     * Đặt địa chỉ làm mặc định
     */
    @PatchMapping("/address/default/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DiaChiResponse> setDefaultAddress(@PathVariable UUID id) {
        log.info("Đặt địa chỉ mặc định ID: {}", id);
        DiaChiResponse response = taiKhoanService.setAddressDefaultCustomer(id);
        return ApiResponse.<DiaChiResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Đặt địa chỉ mặc định thành công")
                .data(response)
                .build();
    }

    /**
     * Xóa địa chỉ
     */
    @DeleteMapping("/address/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteAddress(@PathVariable UUID id) {
        log.info("Xóa địa chỉ ID: {}", id);
        taiKhoanService.deleteAddressCustomer(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Xóa địa chỉ thành công")
                .build();
    }
}