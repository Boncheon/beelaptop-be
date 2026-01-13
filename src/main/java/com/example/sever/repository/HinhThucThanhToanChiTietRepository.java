// com.example.sever.repository.HinhThucThanhToanChiTietRepository
package com.example.sever.repository;

import com.example.sever.entity.HinhThucThanhToan;
import com.example.sever.entity.HinhThucThanhToanChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HinhThucThanhToanChiTietRepository
        extends JpaRepository<HinhThucThanhToanChiTiet, UUID> {

    List<HinhThucThanhToanChiTiet> findByIdOrder_Id(UUID orderId);

//    @Query("""
//           SELECT COALESCE(SUM(h.soTienThanhToan), 0)
//           FROM HinhThucThanhToanChiTiet h
//           WHERE h.idOrder.id = :orderId
//           """)
//    BigDecimal sumSoTienByOrder(@Param("orderId") UUID orderId);

    @Query("""
   SELECT COALESCE(SUM(h.soTienThanhToan), 0)
   FROM HinhThucThanhToanChiTiet h
   WHERE h.idOrder.id = :orderId
   """)
    BigDecimal sumSoTienByOrder(@Param("orderId") UUID orderId);


    @Query("SELECT httt.tenHinhThuc FROM HinhThucThanhToanChiTiet htttct " +
            "JOIN htttct.idHinhThucThanhToan httt " +
            "WHERE htttct.idOrder.id = :idOrder")
    List<String> findTenHinhThucThanhToanByIdOrder(@Param("idOrder") UUID idOrder);

//    Optional<HinhThucThanhToan> findByTenHinhThucIgnoreCase(String tenHinhThuc);

    Optional<HinhThucThanhToanChiTiet>
    findFirstByIdOrder_IdAndIdHinhThucThanhToan_TenHinhThucIgnoreCaseAndSoTienThanhToan(
            UUID orderId,
            String tenHinhThuc,
            BigDecimal soTienThanhToan
    );
}
