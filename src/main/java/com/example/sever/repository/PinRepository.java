package com.example.sever.repository;

import com.example.sever.entity.Pin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PinRepository extends JpaRepository<Pin, UUID> {
    Page<Pin> findByTrangThai(Integer page, Pageable pageable);
}