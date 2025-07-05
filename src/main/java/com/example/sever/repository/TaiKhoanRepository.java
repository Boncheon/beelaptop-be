package com.example.sever.repository;

import com.example.sever.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, UUID> {

    Optional<TaiKhoan> findByIdTaiKhoan(String idTaiKhoan);
    Optional<TaiKhoan> findByEmail(String email);
    Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);
    boolean existsByTenDangNhap(String tenDangNhap);
    boolean existsByEmail(String email);
    Optional<TaiKhoan> findByResetToken(String resetToken);

}