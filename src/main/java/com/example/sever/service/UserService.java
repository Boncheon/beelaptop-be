package com.example.sever.service;

import com.example.sever.dto.req.UserCreationRequest;
import com.example.sever.dto.res.UserDetailResponse;
import com.example.sever.entity.TaiKhoan;

public interface UserService {
    UserDetailResponse createUser(UserCreationRequest request);
    TaiKhoan findUserById(String id);
    UserDetailResponse getCurrentUser();
}