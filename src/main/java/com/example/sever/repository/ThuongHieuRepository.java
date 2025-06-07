package com.example.sever.repository;

import com.example.sever.enity.ThuongHieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ThuongHieuRepository extends JpaRepository<ThuongHieu, UUID> {
    
    List<ThuongHieu> findByTen(String ten);
    
    @Query("SELECT t FROM ThuongHieu t WHERE t.moTa LIKE %?1%")
    List<ThuongHieu> findByMoTaContaining(String moTa);
    
    @Query("SELECT t FROM ThuongHieu t WHERE t.ngayTao BETWEEN ?1 AND ?2")
    List<ThuongHieu> findByNgayTaoBetween(Instant startDate, Instant endDate);
    
    @Query("SELECT t FROM ThuongHieu t ORDER BY t.ngayTao DESC")
    List<ThuongHieu> findAllOrderByNgayTaoDesc();
}