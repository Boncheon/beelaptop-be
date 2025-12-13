package com.example.sever.repository;

import com.example.sever.entity.GioHang;
import com.example.sever.entity.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, UUID> {

    List<GioHangChiTiet> findByIdGioHang_Id(UUID idGioHang);


    Optional<GioHangChiTiet> findByIdGioHangAndIdSpct_Id(GioHang gioHang, UUID idSpct);

    List<GioHangChiTiet> findByIdGioHang(GioHang gioHang);

    void deleteByIdGioHangAndIdSpct_Id(GioHang gioHang, UUID idSpct);
}
