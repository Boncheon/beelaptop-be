package com.example.sever.repository;

import com.example.sever.entity.Anh;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnhRepository extends JpaRepository<Anh, UUID> {

    @Query(value = "SELECT TOP 1 id_anh FROM Anh ORDER BY id_anh DESC", nativeQuery = true)
    String findMaxIdAnh();


}