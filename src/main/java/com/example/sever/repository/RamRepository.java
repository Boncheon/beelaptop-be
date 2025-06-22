package com.example.sever.repository;

import com.example.sever.enity.Ram;
import com.example.sever.enity.Rom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RamRepository extends JpaRepository<Ram, UUID> , JpaSpecificationExecutor<Ram> {
//    @Query("SELECT r FROM Ram r " +
//            "WHERE (:dungLuongRam IS NULL OR r.dungLuongRam LIKE CONCAT('%', :dungLuongRam, '%')) " +
//            "AND (:idLoaiRam IS NULL OR r.idLoaiRam LIKE CONCAT('%', :idLoaiRam, '%')) " +
//            "AND (:trangThai IS NULL OR r.trangThai = :trangThai)")
//    Page<Ram> findByFilter(
//            @Param("dungLuongRam") String dungLuongRam,
//            @Param("trangThai") Integer trangThai,
//            @Param("idLoaiRam") String idLoaiRam,
//            Pageable pageable);

}