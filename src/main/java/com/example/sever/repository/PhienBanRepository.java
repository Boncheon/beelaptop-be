package com.example.sever.repository;

import com.example.sever.entity.PhienBan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PhienBanRepository extends JpaRepository<PhienBan, UUID> {

}