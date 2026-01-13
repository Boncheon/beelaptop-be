package com.example.sever.repository;

import com.example.sever.entity.HinhThucThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HinhThucThanhToanRepository extends JpaRepository<HinhThucThanhToan, UUID> {

    Optional<HinhThucThanhToan> findFirstByTenHinhThucContainingIgnoreCase(String tenHinhThuc);

    Optional<HinhThucThanhToan> findByTenHinhThucIgnoreCase(String tenHinhThuc);


}