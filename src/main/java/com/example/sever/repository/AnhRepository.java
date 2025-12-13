package com.example.sever.repository;

import com.example.sever.entity.Anh;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnhRepository extends JpaRepository<Anh, UUID> {

    List<Anh> findByIdLaptopChiTiet_Id(UUID idLaptopChiTiet);

    // dùng để sinh id_anh: anh0001, anh0002...
    @Query("select max(a.idAnh) from Anh a")
    String findMaxIdAnh();

//    @Query(value = "SELECT TOP 1 id_anh FROM Anh ORDER BY id_anh DESC", nativeQuery = true)
//    String findMaxIdAnh();

    @Query(value = """
            SELECT ImgURL 
            FROM dbo.Anh 
            WHERE id_laptop_chi_tiet = :ctId 
            ORDER BY ID
            """, nativeQuery = true)
    List<String> findAllImgUrlByLaptopChiTietId(@Param("ctId") UUID ctId);

}