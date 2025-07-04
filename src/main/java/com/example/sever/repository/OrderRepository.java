package com.example.sever.repository;


import com.example.sever.enity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query(value = "SELECT TOP 1 ma_don_hang FROM Orders WHERE ma_don_hang LIKE 'OD___-2024' ORDER BY ma_don_hang DESC", nativeQuery = true)
    String findLastMaDonHang();


}
