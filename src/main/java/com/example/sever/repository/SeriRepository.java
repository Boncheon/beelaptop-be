package com.example.sever.repository;

import com.example.sever.dto.response.SeriDisplayReponse;
import com.example.sever.entity.Seri;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeriRepository extends JpaRepository<Seri, UUID> {

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
    """)
    long countSeriByLaptop(@Param("idLaptop") UUID idLaptop);
}