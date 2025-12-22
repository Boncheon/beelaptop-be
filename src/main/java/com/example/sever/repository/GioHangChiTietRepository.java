package com.example.sever.repository;

import com.example.sever.entity.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, UUID> {

    List<GioHangChiTiet> findByIdGioHang_Id(UUID idGioHang);
}
