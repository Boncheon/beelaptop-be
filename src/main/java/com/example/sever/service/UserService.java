package com.example.sever.service;

import com.example.sever.dto.request.UserCreationRequest;
import com.example.sever.dto.response.UserDetailResponse;
import com.example.sever.entity.TaiKhoan;

import java.util.List;

public interface UserService {
    UserDetailResponse createUser(UserCreationRequest request);
    UserDetailResponse createEmployee(UserCreationRequest request);
    UserDetailResponse createCustomer(UserCreationRequest request);
    UserDetailResponse updateEmployee(String id, UserCreationRequest request);
    UserDetailResponse updateCustomer(String id, UserCreationRequest request);
    TaiKhoan findUserById(String id);
    UserDetailResponse getCurrentUser();
    UserDetailResponse getUserDetail(String id);

    List<UserDetailResponse> getUsersByRole(String roleId);
    UserDetailResponse toggleUserStatus(String id);
    void changePassword(TaiKhoan user, String currentPassword, String newPassword);

}