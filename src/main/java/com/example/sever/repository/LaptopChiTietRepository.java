package com.example.sever.repository;

import com.example.sever.dto.response.CustomerLaptopChiTietProject;
import com.example.sever.dto.response.GioHang.ProductCartResponse;
import com.example.sever.dto.response.ListLaptopCustomerProjection;
import com.example.sever.dto.response.Search.LaptopSearchResponse;
import com.example.sever.entity.LaptopChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LaptopChiTietRepository extends JpaRepository<LaptopChiTiet, UUID> {

    List<LaptopChiTiet> findByIdLapTop_Id(UUID idLapTopId);


//    List<LaptopChiTiet> findByIdLaptop(Laptop laptop);
//
//    List<LaptopChiTiet> findByIdLaptopId(UUID laptopId);
//
//    @Query("SELECT lct FROM LaptopChiTiet lct WHERE lct.trangThai = ?1")
//    List<LaptopChiTiet> findByTrangThai(Boolean trangThai);

//    @Query("SELECT lct FROM LaptopChiTiet lct WHERE lct.soLuong > 0")
//    List<LaptopChiTiet> findAvailableItems();

    @Query(value = """
            SELECT 
                ct.ID AS ctId,
                l.ID AS laptopId,
                l.ten_san_pham AS productName,
                ct.gia_ban AS price,
                mh.kich_thuoc AS display,
                mh.do_phan_giai AS resolution,
                ram.dung_luong_ram AS ram,
                rom.dung_luong_ssd AS ssd,
                cpu.ten AS cpu,
                dohoa.modelcard_oboard AS card,
                ms.ten AS color,
                a.ImgURL AS image , 
                l.mo_ta as description 
            FROM dbo.LaptopChiTiet ct
            INNER JOIN dbo.Laptop l ON ct.id_lap_top = l.ID
            LEFT JOIN dbo.ManHinh mh ON l.id_man_hinh = mh.ID
            LEFT JOIN dbo.RAM ram ON ct.id_ram = ram.ID
            LEFT JOIN dbo.Rom rom ON ct.id_ssd = rom.ID
            LEFT JOIN dbo.CPU cpu ON ct.id_cpu = cpu.ID
            LEFT JOIN dbo.DoHoa dohoa ON ct.id_dohoa = dohoa.ID
            LEFT JOIN dbo.MauSac ms ON ct.id_mau_sac = ms.ID
            LEFT JOIN (
                SELECT id_laptop_chi_tiet, ImgURL
                FROM dbo.Anh
                WHERE ID IN (
                    SELECT MIN(ID)
                    FROM dbo.Anh
                    GROUP BY id_laptop_chi_tiet
                )
            ) AS a ON ct.ID = a.id_laptop_chi_tiet
            WHERE l.ID = :laptopId
            ORDER BY ct.gia_ban ASC
            """, nativeQuery = true)
    List<CustomerLaptopChiTietProject> findLaptopChiTietWithAnhAndVersionsByLaptopId(@Param("laptopId") UUID laptopId);


    @Query(value = """
            SELECT 
                CASE 
                    WHEN so_luong >= :yeuCau THEN N'Trong kho đủ, bạn có thể đặt'
                    ELSE N'Số lượng yêu cầu lớn hơn tồn kho, vui lòng nhập thêm'
                END AS thongBao
            FROM LaptopChiTiet
            WHERE ID = :ctId
            """, nativeQuery = true)
    String checkTonKho(
            @Param("ctId") UUID ctId,
            @Param("yeuCau") int yeuCau
    );

    @Query(value = """
            SELECT 
                ct.ID AS LaptopChiTietID,
                l.ID AS LaptopID,
                l.ten_san_pham AS tenSanPham,
                cpu.ten AS cpu,
                ram.dung_luong_ram AS ram,
                rom.dung_luong_ssd AS ssd,
                dohoa.modelcard_oboard AS card,
                mh.kich_thuoc AS display,
                mh.do_phan_giai AS resolution,
                ct.gia_ban AS giaBan,
                ct.so_luong AS soLuong,
                a.ImgURL AS anhDaiDien
            FROM dbo.LaptopChiTiet ct
            INNER JOIN dbo.Laptop l ON ct.id_lap_top = l.ID
            LEFT JOIN dbo.CPU cpu    ON cpu.ID = ct.id_cpu
            LEFT JOIN dbo.RAM ram    ON ram.ID = ct.id_ram
            LEFT JOIN dbo.Rom rom    ON rom.ID = ct.id_ssd
            LEFT JOIN dbo.DoHoa dohoa ON dohoa.ID = ct.id_dohoa
            LEFT JOIN dbo.ManHinh mh ON mh.ID = l.id_man_hinh
            OUTER APPLY (
                SELECT TOP 1 ImgURL
                FROM dbo.Anh a2
                WHERE a2.id_laptop_chi_tiet = ct.ID
                ORDER BY a2.ID
            ) AS a
            WHERE l.ten_san_pham IS NOT NULL
            ORDER BY l.ngay_tao DESC, ct.gia_ban ASC
            """, nativeQuery = true)
    List<ListLaptopCustomerProjection> getAllLaptopDetails();

    @Query(value = "SELECT ID FROM LaptopChiTiet WHERE ID = :id", nativeQuery = true)
    Optional<String> findLaptopChiTietId(@Param("id") String id);

    @Query(
            value = """ 
                    SELECT 
                        lct.ID AS id,
                        l.ten_san_pham AS name,
                        ms.ten AS color,
                        cpu.ten AS cpu,
                        ram.dung_luong_ram AS ram,
                        rom.dung_luong_ssd AS ssd,
                        dh.modelcard_oboard AS card,
                        lct.gia_ban AS price,
                        1 AS quantity,
                        a.ImgURL AS image
                    FROM LaptopChiTiet lct
                    JOIN Laptop l        ON lct.id_lap_top = l.ID
                    JOIN MauSac ms       ON lct.id_mau_sac = ms.ID
                    JOIN CPU cpu         ON lct.id_cpu = cpu.ID
                    JOIN RAM ram         ON lct.id_ram = ram.ID
                    JOIN Rom rom         ON lct.id_ssd = rom.ID
                    JOIN DoHoa dh        ON lct.id_dohoa = dh.ID
                    OUTER APPLY (
                        SELECT TOP 1 ImgURL
                        FROM dbo.Anh a
                        WHERE a.id_laptop_chi_tiet = lct.ID
                        ORDER BY a.ID
                    ) AS a
                    WHERE lct.ID = :id
                    """,
            nativeQuery = true
    )
    ProductCartResponse getLaptopDetail(@Param("id") UUID id);

    @Query(
            value = "SELECT DISTINCT " +
                    "L.id as id ," +
                    "L.ten_san_pham AS tenSanPham, " +
                    "CT.id as idSpct , " +
                    "CT.gia_ban AS giaBan, " +
                    "CPU.ten AS cpu, " +
                    "RAM.dung_luong_ram AS ram, " +
                    "SSD.dung_luong_ssd AS ssd, " +
                    "DG.ten_day_du AS gpu, " +
                    "a.ImgURL AS image " +
                    "FROM dbo.Laptop L " +
                    "JOIN dbo.LaptopChiTiet CT ON L.ID = CT.id_lap_top " +
                    "LEFT JOIN dbo.CPU CPU ON CT.id_cpu = CPU.ID " +
                    "LEFT JOIN dbo.RAM RAM ON CT.id_ram = RAM.ID " +
                    "LEFT JOIN dbo.Rom SSD ON CT.id_ssd = SSD.ID " +
                    "LEFT JOIN dbo.DoHoa DG ON CT.id_dohoa = DG.ID " +
                    "OUTER APPLY (" +
                    "    SELECT TOP 1 ImgURL " +
                    "    FROM dbo.Anh a " +
                    "    WHERE a.id_laptop_chi_tiet = CT.ID " +
                    "    ORDER BY a.ID" +
                    ") AS a " +
                    "WHERE (CPU.ten + ' ' + RAM.dung_luong_ram + ' ' + SSD.dung_luong_ssd + ' ' + DG.ten_day_du + ' ' + L.ten_san_pham) LIKE :search",
            nativeQuery = true
    )
    List<LaptopSearchResponse> searchLaptopCustomer(@Param("search") String search);
    
    @Query(value = """
        SELECT lct.ID, lct.so_luong
        FROM dbo.Seri s
        INNER JOIN dbo.LaptopChiTiet lct ON s.id_lap_top_ct = lct.ID
        WHERE s.ID = :seriId
        """, nativeQuery = true)
    List<Object[]> findLaptopChiTietBySeriId(@Param("seriId") UUID seriId);
    
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query(value = "UPDATE dbo.LaptopChiTiet SET so_luong = so_luong - :soLuongMua WHERE ID = :id AND so_luong >= :soLuongMua", nativeQuery = true)
    int updateSoLuong(@Param("id") UUID id, @Param("soLuongMua") Integer soLuongMua);
    
    @Query(value = "SELECT so_luong FROM dbo.LaptopChiTiet WHERE ID = :id", nativeQuery = true)
    Optional<Integer> findSoLuongById(@Param("id") UUID id);
}
