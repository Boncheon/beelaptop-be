package com.example.sever.repository;

import com.example.sever.dto.response.LaptopChiTietResponseDTO;
import com.example.sever.entity.LaptopChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

import java.util.UUID;

@Repository
public interface LaptopChiTietRepository extends JpaRepository<LaptopChiTiet, UUID> {

    List<LaptopChiTiet> findByIdLaptop_Id(UUID idLaptopId);

    @Query("""
        SELECT new com.example.sever.dto.response.LaptopChiTietResponseDTO(
            l,
            COUNT(s)
        )
        FROM LaptopChiTiet l
        LEFT JOIN Seri s ON s.idLapTopCt = l
        WHERE l.idLaptop.id = :idLaptop
        GROUP BY l
    """)
    List<LaptopChiTietResponseDTO> findDtoByLaptopWithSeri(@Param("idLaptop") UUID idLaptop);

    @Query(
            value = """
            SELECT new com.example.sever.dto.response.LaptopChiTietResponseDTO(
                l,
                COUNT(s)
            )
            FROM LaptopChiTiet l
            LEFT JOIN Seri s ON s.idLapTopCt = l
            GROUP BY l
        """,
            countQuery = """
            SELECT COUNT(l)
            FROM LaptopChiTiet l
        """
    )
    Page<LaptopChiTietResponseDTO> findAllWithSeri(Pageable pageable);

    long countByIdLaptop_Id(UUID idLaptopId);
    @Query("""
        SELECT COUNT(l) > 0
        FROM LaptopChiTiet l
        WHERE l.idLaptop.id = :idLaptop
          AND l.idRam.id     = :idRam
          AND l.idSsd.id     = :idSsd
          AND l.idCpu.id     = :idCpu
          AND l.idDohoa.id   = :idDohoa
          AND l.idMauSac.id  = :idMauSac
    """)
    boolean existsVariant(
            @Param("idLaptop") UUID idLaptop,
            @Param("idRam") UUID idRam,
            @Param("idSsd") UUID idSsd,
            @Param("idCpu") UUID idCpu,
            @Param("idDohoa") UUID idDohoa,
            @Param("idMauSac") UUID idMauSac
    );

//    @Query("SELECT lct FROM LaptopChiTiet lct WHERE lct.soLuong > 0")
//    List<LaptopChiTiet> findAvailableItems();
}
