package com.example.sever.repository;

import com.example.sever.entity.Seri;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeriRepository extends JpaRepository<Seri, UUID> {

    @Query(value = """
    SELECT pb.ID, COUNT(s.id_seri) AS so_luong_ton_kho
    FROM PhienBan pb
    LEFT JOIN Seri s ON pb.ID = s.id_phien_ban AND s.trang_thai = 1
    GROUP BY pb.ID
""", nativeQuery = true)
    List<Object[]> demSoLuongSeriTheoPhienBan();

    @Query("""
    SELECT lct.idLapTop.id, COUNT(s.id)
    FROM Seri s
    JOIN s.phienBan pb
    JOIN PhienbanLaptopct map ON pb.id = map.idPhienBan.id
    JOIN LaptopChiTiet lct ON map.idLaptopChiTiet.id = lct.id
    WHERE s.trangThai = 1
    GROUP BY lct.idLapTop.id
""")
    List<Object[]> demSoLuongSeriTheoLaptop();

    @Query(value = "SELECT s.trang_thai FROM dbo.Seri s WHERE s.ID = :id", nativeQuery = true)
    Optional<Integer> findTrangThaiById(@Param("id") UUID id);
    
    @Query(value = "SELECT COUNT(*) FROM dbo.Seri s WHERE s.ID = :id", nativeQuery = true)
    int countById(@Param("id") UUID id);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query(value = "UPDATE dbo.Seri SET trang_thai = :trangThai WHERE ID = :id", nativeQuery = true)
    void updateTrangThaiSeri(@Param("id") UUID id, @Param("trangThai") Integer trangThai);

    @Query(value = """
        SELECT CAST(s.ID AS VARCHAR(36))
        FROM dbo.Seri s
        WHERE s.id_lap_top_ct = :laptopChiTietId
        AND s.trang_thai = 1
        ORDER BY s.ID
        """, nativeQuery = true)
    List<String> findSeriIdsByLaptopChiTietId(@Param("laptopChiTietId") UUID laptopChiTietId);
    
    @Query(value = """
        SELECT COUNT(*)
        FROM dbo.Seri s
        WHERE s.id_lap_top_ct = :laptopChiTietId
        AND s.trang_thai = 1
        """, nativeQuery = true)
    int countSeriByLaptopChiTietId(@Param("laptopChiTietId") UUID laptopChiTietId);

    @Query(value = """
    SELECT s.trang_thai
    FROM Seri s
    WHERE s.id_lap_top_ct = :laptopChiTietId
""", nativeQuery = true)
    List<Integer> findAllTrangThaiSeri(@Param("laptopChiTietId") UUID laptopChiTietId);


    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query(value = "UPDATE dbo.Seri SET trang_thai = :trangThai WHERE ID = :id", nativeQuery = true)
    void updateTrangThaiSeri(@Param("id") UUID id, @Param("trangThai") Integer trangThai);

    @Query(value = """
        SELECT CAST(s.ID AS VARCHAR(36))
        FROM dbo.Seri s
        WHERE s.id_lap_top_ct = :laptopChiTietId
        AND s.trang_thai = 1
        ORDER BY s.ID
        """, nativeQuery = true)
    List<String> findSeriIdsByLaptopChiTietId(@Param("laptopChiTietId") UUID laptopChiTietId);
    
    @Query(value = """
        SELECT COUNT(*)
        FROM dbo.Seri s
        WHERE s.id_lap_top_ct = :laptopChiTietId
        AND s.trang_thai = 1
        """, nativeQuery = true)
    int countSeriByLaptopChiTietId(@Param("laptopChiTietId") UUID laptopChiTietId);

    @Query(value = """
        SELECT TOP 1 s.trang_thai
        FROM dbo.Seri s
        WHERE s.id_lap_top_ct = :laptopChiTietId
        ORDER BY s.ID
        """, nativeQuery = true)
    Optional<Integer> findTrangThaiSeriByLaptopChiTietId(@Param("laptopChiTietId") UUID laptopChiTietId);

}