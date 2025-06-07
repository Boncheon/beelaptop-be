package com.example.sever.repository;

import com.example.sever.enity.LaptopChiTiet;
import com.example.sever.enity.Ram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RamRepository extends JpaRepository<Ram, UUID> {

}