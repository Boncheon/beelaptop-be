package com.example.sever.repository;

import com.example.sever.enity.DoHoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DoHoaRepository extends JpaRepository<DoHoa, UUID>, JpaSpecificationExecutor<DoHoa> {
//    @Query("SELECT d FROM DoHoa d " +
//            "WHERE (:ram IS NULL OR d.boNhoRam = :ram) " +
//            "AND (:status IS NULL OR d.trangThai = :status) " +
//            "AND (:keyword IS NULL OR d.tenDayDu LIKE %:keyword% OR d.idDohoa LIKE %:keyword%)")
//    Page<DoHoa> findByFilter(
//            @Param("ram") String ram,
//            @Param("status") Integer status,
//            @Param("keyword") String keyword,
//            Pageable pageable);

}