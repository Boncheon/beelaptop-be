package com.example.sever.repository;

import com.example.sever.entity.GiamGiaHoaDon;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GiamGiaHoaDonRepository extends JpaRepository<GiamGiaHoaDon, UUID> {
    List<GiamGiaHoaDon> findByIdOrders_Id(UUID orderId);
    Optional<GiamGiaHoaDon> findFirstByIdOrders_Id(UUID orderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select g from GiamGiaHoaDon g where g.idOrders.id = :orderId")
    Optional<GiamGiaHoaDon> findOneByOrderIdForUpdate(@Param("orderId") UUID orderId);

    // ✅ Xóa thẳng theo order (nhanh + ít vấn đề)
    @Modifying
    @Query("delete from GiamGiaHoaDon g where g.idOrders.id = :orderId")
    int deleteByOrderId(@Param("orderId") UUID orderId);


}