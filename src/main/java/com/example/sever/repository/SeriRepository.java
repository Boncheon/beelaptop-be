package com.example.sever.repository;

import com.example.sever.entity.Seri;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeriRepository extends JpaRepository<Seri, UUID> {
    @Query(value = """
    SELECT pb.ID, COUNT(s.id_seri) AS so_luong_ton_kho
    FROM PhienBan pb
    LEFT JOIN Seri s ON pb.ID = s.id_phien_ban AND s.trang_thai = 1
    GROUP BY pb.ID
""", nativeQuery = true)
    List<Object[]> demSoLuongSeriTheoPhienBan();

    @Query("""
    SELECT lct.idLapTop.id, COUNT(s.id)
    FROM Seri s
    JOIN s.phienBan pb
    JOIN PhienbanLaptopct map ON pb.id = map.idPhienBan.id
    JOIN LaptopChiTiet lct ON map.idLaptopChiTiet.id = lct.id
    WHERE s.trangThai = 1
    GROUP BY lct.idLapTop.id
""")
    List<Object[]> demSoLuongSeriTheoLaptop();



}