package com.example.sever.repository;

import com.example.sever.enity.PhienBan;
import com.example.sever.enity.Cpu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface PhienBanRepository extends JpaRepository<PhienBan, UUID> {

}