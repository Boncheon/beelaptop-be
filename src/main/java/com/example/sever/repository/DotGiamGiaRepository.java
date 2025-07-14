package com.example.sever.repository;


import com.example.sever.entity.DotGiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DotGiamGiaRepository extends JpaRepository<DotGiamGia, UUID> {

    List<DotGiamGia> findAll();

    Optional<DotGiamGia> findByIdDotGiamGia(String idDotGiamGia);

    Page<DotGiamGia> findAll(Pageable pageable);



}
