package com.example.sever.repository;

import com.example.sever.entity.OrderCT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderCTRepository extends JpaRepository<OrderCT, UUID> {
    @Query("SELECT o FROM OrderCT o WHERE o.idOrder.id = :idOrder AND o.idSeri.id = :idSeri")
    Optional<OrderCT> findByIdOrderAndIdSeri(@Param("idOrder") UUID idOrder, @Param("idSeri") UUID idSeri);
    
    @Query("SELECT o FROM OrderCT o WHERE o.idOrder.id = :idOrder ORDER BY o.id")
    List<OrderCT> findByIdOrder(@Param("idOrder") UUID idOrder);
    

    @Query(value = """
        SELECT 
            CAST(oct.ID AS VARCHAR(36)) AS idOrderCT,
            CAST(s.ID AS VARCHAR(36)) AS idSeri,
            CAST(lct.ID AS VARCHAR(36)) AS idLaptopChiTiet,
            l.ten_san_pham AS tenSanPham,
            a.ImgURL AS anhSanPham,
            oct.gia_ban AS giaBan,
            1 AS soLuong
        FROM dbo.OrderCT oct
        INNER JOIN dbo.Seri s ON oct.id_seri = s.ID
        INNER JOIN dbo.LaptopChiTiet lct ON s.id_lap_top_ct = lct.ID
        INNER JOIN dbo.Laptop l ON lct.id_lap_top = l.ID
        OUTER APPLY (
            SELECT TOP 1 ImgURL
            FROM dbo.Anh a2
            WHERE a2.id_laptop_chi_tiet = lct.ID
            ORDER BY a2.ID
        ) AS a
        WHERE oct.id_order = CAST(:idOrder AS UNIQUEIDENTIFIER)
        ORDER BY oct.ID
        """, nativeQuery = true)
    List<Object[]> findProductInfoByIdOrder(@Param("idOrder") UUID idOrder);
}

