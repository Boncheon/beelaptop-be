package com.example.sever.repository;

import com.example.sever.entity.Laptop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.util.UUID;
@Repository
public interface LapTopRepository extends JpaRepository<Laptop, UUID> {


    Optional<Laptop> findByTenSanPham(@Size(max = 255) String tenSanPham);

}


