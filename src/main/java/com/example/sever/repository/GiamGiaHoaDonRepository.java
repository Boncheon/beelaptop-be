package com.example.sever.repository;

import com.example.sever.entity.GiamGiaHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GiamGiaHoaDonRepository extends JpaRepository<GiamGiaHoaDon, UUID> {
    List<GiamGiaHoaDon> findByIdOrders_Id(UUID orderId);

}