package com.example.sever.repository;


import com.example.sever.entity.DotGiamGia;
import com.example.sever.entity.DotGiamGiaChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DotGiamGiaChiTietRepository extends JpaRepository<DotGiamGiaChiTiet, UUID> {
    Optional<DotGiamGiaChiTiet> findByDotGiamGia(DotGiamGia dotGiamGia);

    @Query("SELECT d FROM DotGiamGiaChiTiet d WHERE " +
            "(:keyword IS NULL OR LOWER(d.dotGiamGia.tenDotGiamGia) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:start IS NULL OR d.dotGiamGia.ngayBatDau >= :start) AND " +
            "(:end IS NULL OR d.dotGiamGia.ngayKetThuc <= :end) AND " +
            "(:status IS NULL OR d.dotGiamGia.trangThai = :status)")
    Page<DotGiamGiaChiTiet> filterDot(@Param("keyword") String keyword,
                                      @Param("start") LocalDate start,
                                      @Param("end") LocalDate end,
                                      @Param("status") Integer status,
                                      Pageable pageable);


    List<DotGiamGiaChiTiet> findByDotGiamGia_IdDotGiamGiaContainingIgnoreCaseOrDotGiamGia_TenDotGiamGiaContainingIgnoreCase(String id, String ten);




}
