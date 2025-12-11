package com.example.sever.repository;

import com.example.sever.entity.HinhThucThanhToanChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HinhThucThanhToanChiTietRepository extends JpaRepository<HinhThucThanhToanChiTiet, UUID> {
    
    @Query("SELECT httt.tenHinhThuc FROM HinhThucThanhToanChiTiet htttct " +
           "JOIN htttct.idHinhThucThanhToan httt " +
           "WHERE htttct.idOrder.id = :idOrder")
    List<String> findTenHinhThucThanhToanByIdOrder(@Param("idOrder") UUID idOrder);
}


