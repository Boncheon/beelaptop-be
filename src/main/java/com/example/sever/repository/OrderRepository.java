package com.example.sever.repository;


import com.example.sever.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

//    @Query(value = "SELECT TOP 1 ma_don_hang FROM Orders WHERE ma_don_hang LIKE 'OD___-2024' ORDER BY ma_don_hang DESC", nativeQuery = true)
//    String findLastMaDonHang();

    @Query(value = "SELECT TOP 1 ma_don_hang FROM Orders ORDER BY ID DESC", nativeQuery = true)
    String findLastMaDonHang();

    @Query("""
        SELECT o
        FROM Order o
        LEFT JOIN o.idNhanVien nv
        WHERE (:keyword IS NULL OR :keyword = '' OR
               LOWER(o.maDonHang) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(o.tenKhachHang) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               o.sdtKhachHang LIKE CONCAT('%', :keyword, '%'))
          AND (:loaiDon IS NULL OR :loaiDon = '' OR o.loaiDon = :loaiDon)
          AND (:trangThaiDon IS NULL OR o.trangThai = :trangThaiDon)
          AND (:trangThaiThanhToan IS NULL OR o.trangThaiThanhToan = :trangThaiThanhToan)
          AND (:fromDate IS NULL OR o.ngayTao >= :fromDate)
          AND (:toDate IS NULL OR o.ngayTao < :toDate)
    """)
    Page<Order> searchOrders(
            @Param("keyword") String keyword,
            @Param("loaiDon") String loaiDon,
            @Param("trangThaiDon") Integer trangThaiDon,
            @Param("trangThaiThanhToan") Integer trangThaiThanhToan,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    @Query(
            value = "SELECT TOP 1 ma_don_hang " +
                    "FROM orders " +
                    "WHERE ma_don_hang LIKE :prefix + '%' " +
                    "ORDER BY ma_don_hang DESC",
            nativeQuery = true
    )
    String findLastMaDonHangByPrefix(@Param("prefix") String prefix);
}
