package com.example.sever.repository;



import com.example.sever.entity.Role;

import com.example.sever.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.example.sever.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, UUID> {


    Optional<TaiKhoan> findByIdTaiKhoan(String idTaiKhoan);
    Optional<TaiKhoan> findByEmail(String email);
    Optional<TaiKhoan> findBySoDienThoai(String soDienThoai);
    boolean existsBySoDienThoai(String soDienThoai);
    boolean existsByEmail(String email);

    List<TaiKhoan> findByIdRole(Role role);
    Optional<TaiKhoan> findByResetToken(String resetToken);



    @Query("SELECT MAX(CAST(SUBSTRING(t.idTaiKhoan, 3) AS int)) FROM TaiKhoan t WHERE t.idTaiKhoan LIKE 'TK%'")
    Integer findMaxUserCode();

    @Query("SELECT MAX(CAST(SUBSTRING(t.idTaiKhoan, 3) AS int)) FROM TaiKhoan t WHERE t.idTaiKhoan LIKE 'NV%'")
    Integer findMaxEmployeeCode();

    @Query("SELECT MAX(CAST(SUBSTRING(t.idTaiKhoan, 3) AS int)) FROM TaiKhoan t WHERE t.idTaiKhoan LIKE 'KH%'")
    Integer findMaxCustomerCode();


}