package com.example.sever.repository;

import com.example.sever.entity.OrderActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderActionLogRepository extends JpaRepository<OrderActionLog, UUID> {
    
    @Query(value = "SELECT TOP 1 ngay_tao FROM dbo.OrderActionLog WHERE id_order = :idOrder ORDER BY ngay_tao ASC", nativeQuery = true)
    Optional<Instant> findFirstNgayTaoByIdOrder(@Param("idOrder") UUID idOrder);
}


