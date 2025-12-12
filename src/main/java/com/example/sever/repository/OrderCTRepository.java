package com.example.sever.repository;

import com.example.sever.entity.OrderCT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface OrderCTRepository extends JpaRepository<OrderCT, UUID> {

    List<OrderCT> findByIdOrder_Id(UUID orderId);

    boolean existsByIdSeri_Id(UUID seriId);
    @Query("SELECT COALESCE(SUM(o.giaBan), 0) FROM OrderCT o WHERE o.idOrder.id = :orderId")
    BigDecimal sumGiaBanByOrderId(@Param("orderId") UUID orderId);
    boolean existsByIdOrder_IdAndIdSeri_Id(UUID orderId, UUID seriId);
}