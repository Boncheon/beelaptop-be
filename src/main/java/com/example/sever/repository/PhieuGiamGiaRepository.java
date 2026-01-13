package com.example.sever.repository;

import com.example.sever.dto.response.PhieuGiamGiaCustomerProjection;
import com.example.sever.entity.PhieuGiamGia;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    //------------------------------Code huy bán onl-----------/




    @Query(value = """
        SELECT 
            p.id as id ,
            p.ID_PhieuGiamGia AS maGiamGia,
            p.ten AS ten,
            p.kieu_giam_gia AS kieuGiamGia,
            p.gia_tri_giam AS giaTriGiam,
            p.ngay_bat_dau AS ngayBatDau,
            p.ngay_ket_thuc AS ngayKetThuc,
            p.gia_tri_min AS giaTriMin,
            p.gia_tri_max AS giaTriMax
        FROM dbo.PhieuGiamGia p
        WHERE p.trang_thai = 1
          AND p.so_luong > 0
          AND GETDATE() BETWEEN p.ngay_bat_dau AND p.ngay_ket_thuc
          AND :tongTien >= p.gia_tri_min
        ORDER BY p.ngay_ket_thuc ASC
        """, nativeQuery = true)
    List<PhieuGiamGiaCustomerProjection> findPhieuGiamGiaPhuHop(@Param("tongTien") BigDecimal tongTien);


    @Modifying
    @Transactional
    @Query(value = """
UPDATE dbo.PhieuGiamGia
SET so_luong = so_luong - 1
WHERE ID = :id AND so_luong > 0
""", nativeQuery = true)
    int decrementQtyIfAvailable(@Param("id") UUID id);


}