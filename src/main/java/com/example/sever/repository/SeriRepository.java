package com.example.sever.repository;

import com.example.sever.entity.Seri;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.UUID;

@Repository
public interface SeriRepository extends JpaRepository<Seri, UUID> {

    boolean existsByIdSeri(String idSeri);

    List<Seri> findByIdLapTopCt_Id(UUID idLaptopCt);

    long countByIdLapTopCt_IdAndTrangThai(UUID idLaptopCt, Integer trangThai);


}