package com.example.sever.repository;

import com.example.sever.entity.GiamGiaHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GiamGiaHoaDonRepository extends JpaRepository<GiamGiaHoaDon, UUID> {

}