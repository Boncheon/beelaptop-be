package com.example.sever.repository;

import com.example.sever.entity.PhienbanLaptopct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PhienbanLaptopctRepository extends JpaRepository<PhienbanLaptopct, UUID> {

}