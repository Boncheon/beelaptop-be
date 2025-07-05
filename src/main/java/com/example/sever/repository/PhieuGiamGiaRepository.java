package com.example.sever.repository;

import com.example.sever.entity.PhieuGiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, UUID> {
    Optional<PhieuGiamGia> findByIdPhieugiamgia(String idPhieugiamgia);

    List<PhieuGiamGia> findByIdPhieugiamgiaContainingIgnoreCaseOrTenContainingIgnoreCase(String idPhieugiamgia, String tenPhieugiamgia);

    List<PhieuGiamGia> findByTrangThai(Integer trangThai);

    @Query("SELECT p FROM PhieuGiamGia p WHERE p.trangThai = 1 AND p.ngayBatDau <= :now AND p.ngayKetThuc >= :now AND p.soLuong > 0")
    List<PhieuGiamGia> findAllValidCoupons(@Param("now") LocalDate today);

    List<PhieuGiamGia> findAll();

    Page<PhieuGiamGia> findAll(Pageable pageable);

    boolean existsByIdPhieugiamgia(String idPhieugiamgia);

}