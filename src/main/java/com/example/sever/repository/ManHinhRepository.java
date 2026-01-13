package com.example.sever.repository;

import com.example.sever.entity.ManHinh;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManHinhRepository extends JpaRepository<ManHinh, UUID>, JpaSpecificationExecutor<ManHinh> {
    Page<ManHinh> findByTrangThai(Integer page, Pageable pageable);
}