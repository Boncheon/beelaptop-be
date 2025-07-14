package com.example.sever.repository;

import com.example.sever.entity.DiaChi;
import com.example.sever.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DiaChiRepository extends JpaRepository<DiaChi, UUID> {
    Optional<DiaChi> findByIdTaiKhoan(TaiKhoan taiKhoan);

    @Query("SELECT MAX(CAST(SUBSTRING(d.idDiaChi, 3) AS int)) FROM DiaChi d WHERE d.idDiaChi LIKE 'DC%'")
    Integer findMaxDiaChiCode();
}
