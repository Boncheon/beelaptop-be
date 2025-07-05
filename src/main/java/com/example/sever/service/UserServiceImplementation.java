package com.example.sever.service;


import com.example.sever.dto.request.UserCreationRequest;
import com.example.sever.dto.response.UserDetailResponse;
import com.example.sever.entity.Role;
import com.example.sever.entity.TaiKhoan;
import com.example.sever.exception.AppException;
import com.example.sever.exception.ErrorCode;
import com.example.sever.mapper.UserMapper;
import com.example.sever.repository.RoleRepository;
import com.example.sever.repository.TaiKhoanRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImplementation implements UserService {

    TaiKhoanRepository taiKhoanRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDetailResponse createUser(UserCreationRequest request) {
        if (taiKhoanRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        if (taiKhoanRepository.existsByTenDangNhap(request.getTenDangNhap())) {
            throw new AppException(ErrorCode.TEN_DANG_NHAP_EXISTED);
        }

        Role role = roleRepository.findByIdRole("R003")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        long count = taiKhoanRepository.count();
        String idTaiKhoan = "TK" + String.format("%03d", count + 1);

        TaiKhoan user = userMapper.toTaiKhoan(request);
        user.setId(UUID.randomUUID());
        user.setIdTaiKhoan(idTaiKhoan);
        user.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        user.setTrangThai(1);
        user.setIdRole(role);

        user = taiKhoanRepository.save(user);
        return userMapper.toUserDetailResponse(user);
    }

    @Override
    public TaiKhoan findUserById(String id) {
        return taiKhoanRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public UserDetailResponse getCurrentUser() {
        try {
            TaiKhoan user = (TaiKhoan) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return userMapper.toUserDetailResponse(user);
        } catch (Exception e) {
            log.error("Error getting current user: {}", e.getMessage());
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
    }
}