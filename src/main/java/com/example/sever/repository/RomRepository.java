package com.example.sever.repository;

import com.example.sever.enity.Rom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RomRepository extends JpaRepository<Rom, UUID> {
    

}