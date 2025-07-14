package com.example.sever.service;

import com.example.sever.entity.TaiKhoan;
import com.example.sever.exception.AppException;
import com.example.sever.exception.ErrorCode;
import com.example.sever.repository.TaiKhoanRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomUserDetailService implements UserDetailsService {

    TaiKhoanRepository taiKhoanRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) {
        try {
            log.debug("Attempting to load user by identifier: {}", identifier);
            TaiKhoan user = taiKhoanRepository.findBySoDienThoai(identifier)
                    .or(() -> {
                        log.debug("No user found with tenDangNhap: {}, trying email", identifier);
                        return taiKhoanRepository.findByEmail(identifier);
                    })
                    .orElseThrow(() -> {
                        log.error("No user found with identifier: {}", identifier);
                        return new AppException(ErrorCode.USER_NOT_EXIST, "Only JPEG and PNG images are allowed");
                    });
            log.info("User found: {}", user.getSoDienThoai());
            return user;
        } catch (Exception e) {
            log.error("Unexpected error while loading user by identifier: {}. Error: {}", identifier, e.getMessage());
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR, "Only JPEG and PNG images are allowed");
        }
    }
}