package com.example.sever.repository;

import com.example.sever.entity.Rom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RomRepository extends JpaRepository<Rom, UUID>, JpaSpecificationExecutor<Rom> {


//    @Query("SELECT r FROM Rom r " +
//            "WHERE (:trangThai IS NULL OR r.trangThai = :trangThai) " +
//            "AND (:keyword IS NULL OR " +
//            "   LOWER(r.idSsd) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "   LOWER(r.dungLuongSsd) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "   LOWER(r.loaiSsd) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
//            ")")
//    Page<Rom> findByFilter(
//            @Param("trangThai") Integer trangThai,
//            @Param("keyword") String keyword,
//            Pageable pageable);

}