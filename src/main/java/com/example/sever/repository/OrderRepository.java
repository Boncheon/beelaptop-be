package com.example.sever.repository;


import com.example.sever.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query(value = "SELECT TOP 1 ma_don_hang FROM dbo.Orders WHERE ma_don_hang LIKE 'OD___-2024' ORDER BY ma_don_hang DESC", nativeQuery = true)
    String findLastMaDonHang();

    @Query("SELECT o FROM Order o WHERE o.idTaiKhoan.id = :idTaiKhoan ORDER BY o.id DESC")
    List<Order> findByIdTaiKhoanOrderByIdDesc(@Param("idTaiKhoan") UUID idTaiKhoan);

    @Query("SELECT o FROM Order o WHERE o.maDonHang = :maDonHang AND o.sdtKhachHang = :sdt")
    List<Order> findByMaDonHangAndSdtKhachHang(@Param("maDonHang") String maDonHang, @Param("sdt") String sdt);

    @org.springframework.data.jpa.repository.Modifying
    @Query(value = "UPDATE dbo.Orders SET ngay_tao = GETDATE() WHERE ID = :id", nativeQuery = true)
    void updateNgayTaoById(@Param("id") UUID id);

}
