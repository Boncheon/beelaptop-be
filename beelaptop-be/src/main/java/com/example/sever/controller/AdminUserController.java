package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.UserCreationRequest;
import com.example.sever.dto.response.UserDetailResponse;
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

@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminUserController {

    UserService userService;

    @PostMapping("/create-employee")
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

    @PostMapping("/create-customer")
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

    @PutMapping("/update-employee/{id}")
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

    @PutMapping("/update-customer/{id}")
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

    @GetMapping("/{id}")
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

    @GetMapping("/by-role/{roleId}")
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

    @PatchMapping("/{id}/status")
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
}