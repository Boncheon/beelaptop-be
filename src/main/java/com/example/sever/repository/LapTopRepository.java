package com.example.sever.repository;

import com.example.sever.dto.response.CustomerLaptopProjection;
import com.example.sever.dto.response.Search.LaptopSearchBrandProjection;
import com.example.sever.entity.Laptop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Optional;

import java.util.UUID;

@Repository
public interface LapTopRepository extends JpaRepository<Laptop, UUID> {


    Optional<Laptop> findByTenSanPham(@Size(max = 255) String tenSanPham);

    @Query(value = """
            SELECT 
                l.ID AS LaptopID,
                l.ten_san_pham as productName,
                cpu.ten as cpu,
                ram.dung_luong_ram as memory,
                rom.dung_luong_ssd AS ssd,
                dohoa.modelcard_oboard AS card,
                mh.kich_thuoc AS display,
                mh.do_phan_giai as resolution,
                ct.gia_ban as price,
                a.ImgURL AS image
            FROM dbo.Laptop l
            OUTER APPLY (
                SELECT TOP 1 ctt.*
                FROM dbo.LaptopChiTiet ctt
                WHERE ctt.id_lap_top = l.ID
                ORDER BY ctt.gia_ban ASC
            ) AS ct
            LEFT JOIN dbo.CPU cpu    ON cpu.ID = ct.id_cpu
            LEFT JOIN dbo.RAM ram    ON ram.ID = ct.id_ram
            LEFT JOIN dbo.Rom rom    ON rom.ID = ct.id_ssd
            LEFT JOIN dbo.DoHoa dohoa ON dohoa.ID = ct.id_dohoa
            LEFT JOIN dbo.ManHinh mh ON mh.ID = l.id_man_hinh
            OUTER APPLY (
                SELECT TOP 1 ImgURL 
                FROM dbo.Anh a
                WHERE a.id_laptop_chi_tiet = ct.ID
                ORDER BY a.ID
            ) AS a
            WHERE l.ten_san_pham IS NOT NULL
            ORDER BY NEWID();
            """, nativeQuery = true)
    List<CustomerLaptopProjection> getLaptopsForHome();

    @Query(value = """
            SELECT TOP 5
                l.ID AS LaptopID,
                l.ten_san_pham as productName,
                cpu.ten as cpu,
                ram.dung_luong_ram as memory,
                rom.dung_luong_ssd AS ssd,
                dohoa.modelcard_oboard AS card,
                mh.kich_thuoc AS display,
                mh.do_phan_giai as resolution,
                ct.gia_ban as price,
                a.ImgURL AS image
            FROM dbo.Laptop l
            OUTER APPLY (
                SELECT TOP 1 ctt.*
                FROM dbo.LaptopChiTiet ctt
                WHERE ctt.id_lap_top = l.ID
                ORDER BY ctt.gia_ban ASC
            ) AS ct
            LEFT JOIN dbo.CPU cpu    ON cpu.ID = ct.id_cpu
            LEFT JOIN dbo.RAM ram    ON ram.ID = ct.id_ram
            LEFT JOIN dbo.Rom rom    ON rom.ID = ct.id_ssd
            LEFT JOIN dbo.DoHoa dohoa ON dohoa.ID = ct.id_dohoa
            LEFT JOIN dbo.ManHinh mh ON mh.ID = l.id_man_hinh
            OUTER APPLY (
                SELECT TOP 1 ImgURL 
                FROM dbo.Anh a
                WHERE a.id_laptop_chi_tiet = ct.ID
                ORDER BY a.ID
            ) AS a
            WHERE l.ten_san_pham IS NOT NULL
            ORDER BY l.ngay_tao DESC;
            """, nativeQuery = true)
    List<CustomerLaptopProjection> getLatestLaptops();

    @Query(value = """
                SELECT 
                    l.ID AS laptopID,
                    l.ten_san_pham AS tenSanPham,
                    l.id_laptop AS idLaptop,
                    th.ten AS thuongHieu,
                    mh.kich_thuoc AS manHinh,
                    hdh.ten AS heDieuHanh,
                    ct.ID AS chiTietID,
                    ram.dung_luong_ram AS RAM,
                    ssd.dung_luong_ssd AS SSD,
                    cpu.ten AS CPU,
                    dg.ten_day_du AS GPU,
                    ms.ten AS mauSac,
                    ct.gia_ban AS giaBan,
                    a.ImgURL AS image
                FROM dbo.Laptop l
                LEFT JOIN dbo.ThuongHieu th ON l.id_thuonghieu = th.ID
                LEFT JOIN dbo.ManHinh mh ON l.id_man_hinh = mh.ID
                LEFT JOIN dbo.HeDieuHanh hdh ON l.id_dieu_hanh = hdh.ID
                LEFT JOIN dbo.LaptopChiTiet ct ON l.ID = ct.id_lap_top
                LEFT JOIN dbo.RAM ram ON ct.id_ram = ram.ID
                LEFT JOIN dbo.Rom ssd ON ct.id_ssd = ssd.ID
                LEFT JOIN dbo.CPU cpu ON ct.id_cpu = cpu.ID
                LEFT JOIN dbo.DoHoa dg ON ct.id_dohoa = dg.ID
                LEFT JOIN dbo.MauSac ms ON ct.id_mau_sac = ms.ID
                OUTER APPLY (
                    SELECT TOP 1 ImgURL 
                    FROM dbo.Anh a
                    WHERE a.id_laptop_chi_tiet = ct.ID
                    ORDER BY a.ID
                ) AS a
                WHERE l.id_thuonghieu = :brandId
                ORDER BY l.ten_san_pham, ct.gia_ban
            """, nativeQuery = true)
    List<LaptopSearchBrandProjection> findAllByBrandId(@Param("brandId") UUID brandId);

}


