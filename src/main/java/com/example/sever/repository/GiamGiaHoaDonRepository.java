package com.example.sever.repository;

import com.example.sever.enity.GiamGiaHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface GiamGiaHoaDonRepository extends JpaRepository<GiamGiaHoaDon, UUID> {

}