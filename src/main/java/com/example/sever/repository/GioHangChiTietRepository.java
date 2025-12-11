package com.example.sever.repository;

import com.example.sever.entity.GioHang;
import com.example.sever.entity.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet , UUID> {
    Optional<GioHangChiTiet> findByIdGioHangAndIdSpct(GioHang gioHang, UUID idSpct);
    List<GioHangChiTiet> findByIdGioHang(GioHang gioHang);
    void deleteByIdGioHangAndIdSpct(GioHang gioHang, UUID idSpct);
}
