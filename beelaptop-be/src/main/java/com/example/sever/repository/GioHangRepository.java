package com.example.sever.repository;

import com.example.sever.entity.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, UUID> {
    

}