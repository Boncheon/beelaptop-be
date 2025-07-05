package com.example.sever.repository;

import com.example.sever.entity.MauSac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository


public interface MauSacRepository extends JpaRepository<MauSac, UUID>, JpaSpecificationExecutor<MauSac> {


}