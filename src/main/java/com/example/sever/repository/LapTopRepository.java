package com.example.sever.repository;

import com.example.sever.entity.Laptop;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.util.UUID;
@Repository
public interface LapTopRepository extends JpaRepository<Laptop, UUID> {

    Page<Laptop> findAllByOrderByNgayTaoDesc(Pageable pageable);

    boolean existsByIdLaptop(String idLaptop);
    @EntityGraph(attributePaths = {"idThuonghieu"})
    Optional<Laptop> findByTenSanPham(@Size(max = 255) String tenSanPham);


}


