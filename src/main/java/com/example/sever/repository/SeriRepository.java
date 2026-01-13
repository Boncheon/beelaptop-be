package com.example.sever.repository;

import com.example.sever.dto.response.SeriDisplayReponse;
import com.example.sever.entity.Seri;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeriRepository extends JpaRepository<Seri, UUID> {




    //------------------------------Code huy bán onl-----------/

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
        SELECT TOP 1 s.trang_thai
        FROM dbo.Seri s
        WHERE s.id_lap_top_ct = :laptopChiTietId
        ORDER BY s.ID
        """, nativeQuery = true)
    Optional<Integer> findTrangThaiSeriByLaptopChiTietId(@Param("laptopChiTietId") UUID laptopChiTietId);


    ///------------------
    boolean existsByIdSeri(String idSeri);

    List<Seri> findByIdLapTopCt_Id(UUID idLaptopCt);
    SeriDisplayReponse findByIdSeri(String idSeri);

    Optional<Seri> findByIdSeriAndTrangThai(String idSeri, Integer trangThai);


    long countByIdLapTopCt_IdAndTrangThai(UUID idLaptopCt, Integer trangThai);

    // Seri còn hoạt động cho 1 LaptopCT
    List<Seri> findByIdLapTopCt_IdAndTrangThai(UUID idLaptopCt, Integer trangThai);


    @Query("""
    SELECT COUNT(s)
    FROM Seri s
    WHERE s.idLapTopCt.idLaptop.id = :idLaptop
      AND s.trangThai = 1
""")
    long countSeriByLaptop(@Param("idLaptop") UUID idLaptop);

    @Query(value = """
    SELECT s.trang_thai
    FROM Seri s
    WHERE s.id_lap_top_ct = :laptopChiTietId
""", nativeQuery = true)
    List<Integer> findAllTrangThaiSeri(@Param("laptopChiTietId") UUID laptopChiTietId);

    // update code huy ngày 05.01
//    @Query(
//            value = "SELECT SUM(trang_thai) " +
//                    "FROM Seri " +
//                    "WHERE id_lap_top_ct = :id " +
//                    "AND trang_thai = 1",
//            nativeQuery = true
//    )
//    Integer sumTrangThai(@Param("id") UUID id);
    //    update code huy ngày 05.01
//    @Query(value = "SELECT id FROM Seri WHERE id_lap_top_ct = :laptopChiTietId AND trang_thai = 1 ORDER BY id", nativeQuery = true)
//    List<String> findSeriIdsByLaptopChiTietIdAndTrangThai(@Param("laptopChiTietId") String laptopChiTietId);
    //    update code huy ngày 05.01
//    @Modifying
//    @Query(value = "UPDATE Seri SET trang_thai = :trangThai WHERE id = :seriId", nativeQuery = true)
//    void updateTrangThaiSeri(@Param("seriId") String seriId, @Param("trangThai") Integer trangThai);


    @Query(
            value = "SELECT COUNT(*) FROM dbo.Seri WHERE id_lap_top_ct = :id AND trang_thai = 1",
            nativeQuery = true
    )
    Integer countActiveSeri(@Param("id") UUID id);

    @Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query(value = """
    UPDATE dbo.Seri
    SET trang_thai = :toStatus
    WHERE ID = :id AND trang_thai = :fromStatus
""", nativeQuery = true)
    int updateTrangThaiSeriIfCurrent(@Param("id") UUID id,
                                     @Param("fromStatus") int fromStatus,
                                     @Param("toStatus") int toStatus);

    @Query(value = """
    SELECT CAST(s.ID AS VARCHAR(36))
    FROM dbo.Seri s
    WHERE s.id_lap_top_ct = :laptopChiTietId
      AND s.trang_thai = 1
    ORDER BY s.ID
""", nativeQuery = true)
    List<String> findSeriIdsByLaptopChiTietIdAndTrangThai(@Param("laptopChiTietId") UUID laptopChiTietId);


    @org.springframework.transaction.annotation.Transactional
    @Modifying
    @Query(value = """
;WITH cte AS (
    SELECT TOP (1) s.ID
    FROM dbo.Seri s WITH (UPDLOCK, ROWLOCK, READPAST)
    WHERE s.id_lap_top_ct = :laptopCtId
      AND s.trang_thai = :fromStatus
    ORDER BY s.ID
)
UPDATE dbo.Seri
SET trang_thai = :toStatus
OUTPUT inserted.ID
WHERE ID IN (SELECT ID FROM cte)
""", nativeQuery = true)
    List<UUID> reserveOneSeriForLaptopCt(
            @Param("laptopCtId") UUID laptopCtId,
            @Param("fromStatus") int fromStatus,
            @Param("toStatus") int toStatus
    );

}