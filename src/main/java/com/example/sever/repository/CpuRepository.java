package com.example.sever.repository;

import com.example.sever.entity.Cpu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CpuRepository extends JpaRepository<Cpu, UUID>, JpaSpecificationExecutor<Cpu> {
    Page<Cpu> findByTrangThai(Integer page, Pageable pageable);
}