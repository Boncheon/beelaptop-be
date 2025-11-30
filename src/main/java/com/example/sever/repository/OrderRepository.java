package com.example.sever.repository;


import com.example.sever.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query(value = "SELECT TOP 1 ma_don_hang FROM Orders WHERE ma_don_hang LIKE 'OD___-2024' ORDER BY ma_don_hang DESC", nativeQuery = true)
    String findLastMaDonHang();

    // Lấy tất cả đơn hàng trong khoảng thời gian
    @Query("SELECT o FROM Order o WHERE o.ngayTao BETWEEN :startDate AND :endDate")
    List<Order> findByNgayTaoBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Đếm đơn hàng theo trạng thái trong khoảng thời gian
    @Query("SELECT COUNT(o) FROM Order o WHERE o.trangThai = :status AND o.ngayTao BETWEEN :startDate AND :endDate")
    Long countByTrangThaiAndNgayTaoBetween(@Param("status") Integer status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Đếm tất cả đơn hàng trong khoảng thời gian
    @Query("SELECT COUNT(o) FROM Order o WHERE o.ngayTao BETWEEN :startDate AND :endDate")
    Long countByNgayTaoBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);



    // Doanh số theo tháng của 1 năm (có thêm top 10 laptop của mỗi tháng)
    @Query(value = """
        WITH ThongKeThang AS (
            SELECT 
                MONTH(o.ngay_tao) AS thang,
                SUM(o.tong_tien_thu_ho) AS doanhSo
            FROM [Order] o
            WHERE YEAR(o.ngay_tao) = :year
            GROUP BY MONTH(o.ngay_tao)
        ),
        TopLaptopThang AS (
            SELECT 
                MONTH(o.ngay_tao) AS thang,
                l.ID AS idLaptop,
                l.ten_san_pham AS tenSanPham,
                COUNT(*) AS soLuongBan,
                ROW_NUMBER() OVER (PARTITION BY MONTH(o.ngay_tao) ORDER BY COUNT(*) DESC) AS rn
            FROM OrderCT oct
            JOIN [Order] o ON o.ID = oct.id_order
            JOIN Seri s ON s.ID = oct.id_seri
            JOIN LaptopChiTiet lct ON lct.ID = s.id_lap_top_ct
            JOIN Laptop l ON l.ID = lct.id_lap_top
            WHERE YEAR(o.ngay_tao) = :year
            GROUP BY MONTH(o.ngay_tao), l.ID, l.ten_san_pham
        )
        SELECT 
            tkt.thang,
            tkt.doanhSo,
            tlt.idLaptop,
            tlt.tenSanPham,
            tlt.soLuongBan
        FROM ThongKeThang tkt
        LEFT JOIN TopLaptopThang tlt ON tkt.thang = tlt.thang AND tlt.rn <= 10
        ORDER BY tkt.thang, tlt.rn
        """, nativeQuery = true)
    List<Object[]> thongKeTheoNam(@Param("year") int year);


    // Doanh số theo ngày (so sánh 2 tháng) - có thêm top 10 laptop của mỗi ngày
    @Query(value = """
        WITH ThongKeNgay AS (
            SELECT 
                DAY(o.ngay_tao) AS ngay,
                SUM(o.tong_tien_thu_ho) AS doanhSo
            FROM [Order] o
            WHERE YEAR(o.ngay_tao) = :year
              AND MONTH(o.ngay_tao) = :month
            GROUP BY DAY(o.ngay_tao)
        ),
        TopLaptopNgay AS (
            SELECT 
                DAY(o.ngay_tao) AS ngay,
                l.ID AS idLaptop,
                l.ten_san_pham AS tenSanPham,
                COUNT(*) AS soLuongBan,
                ROW_NUMBER() OVER (PARTITION BY DAY(o.ngay_tao) ORDER BY COUNT(*) DESC) AS rn
            FROM OrderCT oct
            JOIN [Order] o ON o.ID = oct.id_order
            JOIN Seri s ON s.ID = oct.id_seri
            JOIN LaptopChiTiet lct ON lct.ID = s.id_lap_top_ct
            JOIN Laptop l ON l.ID = lct.id_lap_top
            WHERE YEAR(o.ngay_tao) = :year
              AND MONTH(o.ngay_tao) = :month
            GROUP BY DAY(o.ngay_tao), l.ID, l.ten_san_pham
        )
        SELECT 
            tkn.ngay,
            tkn.doanhSo,
            tln.idLaptop,
            tln.tenSanPham,
            tln.soLuongBan
        FROM ThongKeNgay tkn
        LEFT JOIN TopLaptopNgay tln ON tkn.ngay = tln.ngay AND tln.rn <= 10
        ORDER BY tkn.ngay, tln.rn
        """, nativeQuery = true)
    List<Object[]> thongKeTheoThang(
            @Param("year") int year,
            @Param("month") int month
    );
    // Thống kê 1 ngày - có thêm top 10 laptop của ngày đó
    @Query(value = """
        WITH ThongKe AS (
            SELECT 
                CAST(:ngay AS date) AS ngay,
                COALESCE(SUM(o.tong_tien_thu_ho), 0) AS doanhSo
            FROM [Order] o
            WHERE CAST(o.ngay_tao AS date) = :ngay
        ),
        TopLaptop AS (
            SELECT TOP 10
                l.ID AS idLaptop,
                l.ten_san_pham AS tenSanPham,
                COUNT(*) AS soLuongBan
            FROM OrderCT oct
            JOIN [Order] o ON o.ID = oct.id_order
            JOIN Seri s ON s.ID = oct.id_seri
            JOIN LaptopChiTiet lct ON lct.ID = s.id_lap_top_ct
            JOIN Laptop l ON l.ID = lct.id_lap_top
            WHERE CAST(o.ngay_tao AS date) = :ngay
            GROUP BY l.ID, l.ten_san_pham
            ORDER BY COUNT(*) DESC
        )
        SELECT 
            tk.ngay,
            tk.doanhSo,
            tl.idLaptop,
            tl.tenSanPham,
            tl.soLuongBan
        FROM ThongKe tk
        CROSS JOIN TopLaptop tl
        ORDER BY tl.soLuongBan DESC
        """, nativeQuery = true)
    List<Object[]> thongKeMotNgay(@Param("ngay") LocalDate ngay);

    // Top 10 laptop bán chạy nhất theo năm (dùng id_lap_top_ct trực tiếp từ Seri)
    @Query(value = """
        SELECT TOP 10
            l.ID AS idLaptop,
            l.ten_san_pham AS tenSanPham,
            COUNT(*) AS soLuongBan
        FROM OrderCT oct
        JOIN [Order] o 
            ON o.ID = oct.id_order    
        JOIN Seri s 
            ON s.ID = oct.id_seri      
        JOIN LaptopChiTiet lct 
            ON lct.ID = s.id_lap_top_ct
        JOIN Laptop l 
            ON l.ID = lct.id_lap_top
        WHERE YEAR(o.ngay_tao) = :year
        GROUP BY 
            l.ID,
            l.ten_san_pham
        ORDER BY 
            soLuongBan DESC
        """, nativeQuery = true)
    List<Object[]> topLaptopBanChayNhat(@Param("year") int year);

    // Kiểm tra dữ liệu - đếm số đơn hàng có trong năm
    @Query(value = """
        SELECT COUNT(*) 
        FROM [Order] o
        WHERE YEAR(o.ngay_tao) = :year
        """, nativeQuery = true)
    Long countDonHangTheoNam(@Param("year") int year);

    // Debug: Kiểm tra số OrderCT có trong năm
    @Query(value = """
        SELECT COUNT(*) 
        FROM OrderCT oct
        JOIN [Order] o ON o.ID = oct.id_order
        WHERE YEAR(o.ngay_tao) = :year
        """, nativeQuery = true)
    Long countOrderCTTheoNam(@Param("year") int year);

    // Debug: Kiểm tra số Seri có liên kết
    @Query(value = """
        SELECT COUNT(*) 
        FROM OrderCT oct
        JOIN [Order] o ON o.ID = oct.id_order
        JOIN Seri s ON s.ID = oct.id_seri
        WHERE YEAR(o.ngay_tao) = :year
        """, nativeQuery = true)
    Long countSeriLienKet(@Param("year") int year);

    // Debug: Kiểm tra số PhienBan có liên kết
    @Query(value = """
        SELECT COUNT(*) 
        FROM OrderCT oct
        JOIN [Order] o ON o.ID = oct.id_order
        JOIN Seri s ON s.ID = oct.id_seri
        JOIN PhienBan pb ON pb.ID = s.id_phien_ban
        WHERE YEAR(o.ngay_tao) = :year
        """, nativeQuery = true)
    Long countPhienBanLienKet(@Param("year") int year);

    // Debug: Kiểm tra số Seri có id_lap_top_ct (đường ngắn)
    @Query(value = """
        SELECT COUNT(*) 
        FROM OrderCT oct
        JOIN [Order] o ON o.ID = oct.id_order
        JOIN Seri s ON s.ID = oct.id_seri
        JOIN LaptopChiTiet lct ON lct.ID = s.id_lap_top_ct
        WHERE YEAR(o.ngay_tao) = :year
        """, nativeQuery = true)
    Long countSeriCoLaptopChiTiet(@Param("year") int year);

    // Debug: Kiểm tra các năm có dữ liệu
    @Query(value = """
        SELECT DISTINCT YEAR(ngay_tao) AS nam
        FROM [Order]
        WHERE ngay_tao IS NOT NULL
        ORDER BY nam DESC
        """, nativeQuery = true)
    List<Integer> getCacNamCoDuLieu();

    // Top 10 laptop bán chạy nhất (không filter năm - để test, dùng id_lap_top_ct trực tiếp)
    @Query(value = """
        SELECT TOP 10
            l.ID AS idLaptop,
            l.ten_san_pham AS tenSanPham,
            COUNT(*) AS soLuongBan
        FROM OrderCT oct
        JOIN [Order] o 
            ON o.ID = oct.id_order    
        JOIN Seri s 
            ON s.ID = oct.id_seri      
        JOIN LaptopChiTiet lct 
            ON lct.ID = s.id_lap_top_ct
        JOIN Laptop l 
            ON l.ID = lct.id_lap_top
        GROUP BY 
            l.ID,
            l.ten_san_pham
        ORDER BY 
            soLuongBan DESC
        """, nativeQuery = true)
    List<Object[]> topLaptopBanChayNhatTatCa();

    // Top 10 laptop bán chạy nhất theo tháng
    @Query(value = """
        SELECT TOP 10
            l.ID AS idLaptop,
            l.ten_san_pham AS tenSanPham,
            COUNT(*) AS soLuongBan
        FROM OrderCT oct
        JOIN [Order] o 
            ON o.ID = oct.id_order    
        JOIN Seri s 
            ON s.ID = oct.id_seri      
        JOIN LaptopChiTiet lct 
            ON lct.ID = s.id_lap_top_ct
        JOIN Laptop l 
            ON l.ID = lct.id_lap_top
        WHERE YEAR(o.ngay_tao) = :year
          AND MONTH(o.ngay_tao) = :month
        GROUP BY 
            l.ID,
            l.ten_san_pham
        ORDER BY 
            soLuongBan DESC
        """, nativeQuery = true)
    List<Object[]> topLaptopBanChayNhatTheoThang(@Param("year") int year, @Param("month") int month);

    // Top 10 laptop bán chạy nhất theo ngày
    @Query(value = """
        SELECT TOP 10
            l.ID AS idLaptop,
            l.ten_san_pham AS tenSanPham,
            COUNT(*) AS soLuongBan
        FROM OrderCT oct
        JOIN [Order] o 
            ON o.ID = oct.id_order    
        JOIN Seri s 
            ON s.ID = oct.id_seri      
        JOIN LaptopChiTiet lct 
            ON lct.ID = s.id_lap_top_ct
        JOIN Laptop l 
            ON l.ID = lct.id_lap_top
        WHERE CAST(o.ngay_tao AS date) = :ngay
        GROUP BY 
            l.ID,
            l.ten_san_pham
        ORDER BY 
            soLuongBan DESC
        """, nativeQuery = true)
    List<Object[]> topLaptopBanChayNhatTheoNgay(@Param("ngay") LocalDate ngay);

    // Thống kê tổng quan (doanh thu, đơn hàng, khách hàng, tăng trưởng)
    @Query(value = """
        DECLARE 
            @today DATE = CAST(GETDATE() AS DATE),
            @yesterday DATE = DATEADD(DAY, -1, CAST(GETDATE() AS DATE));

        ;WITH DoanhThuToday AS (
            SELECT COALESCE(SUM(o.tong_tien_thu_ho), 0) AS revenue
            FROM [dbo].[Order] o
            WHERE CAST(o.ngay_tao AS DATE) = @today
        ),
        DoanhThuYesterday AS (
            SELECT COALESCE(SUM(o.tong_tien_thu_ho), 0) AS revenue
            FROM [dbo].[Order] o
            WHERE CAST(o.ngay_tao AS DATE) = @yesterday
        ),
        DonHangToday AS (
            SELECT COUNT(*) AS countOrder
            FROM [dbo].[Order] o 
            WHERE CAST(o.ngay_tao AS DATE) = @today
        ),
        KhachToday AS (
            SELECT COUNT(DISTINCT o.sdt_khach_hang) AS countCustomer
            FROM [dbo].[Order] o
            WHERE CAST(o.ngay_tao AS DATE) = @today
        )

        SELECT
            COALESCE(dtT.revenue, 0) AS TongDoanhThu,
            COALESCE(dhT.countOrder, 0) AS TongDonHang,
            COALESCE(khT.countCustomer, 0) AS TongKhachHang,
            CASE 
                -- Nếu hôm qua = 0 và hôm nay > 0: tăng trưởng 100%
                WHEN COALESCE(dtY.revenue, 0) = 0 AND COALESCE(dtT.revenue, 0) > 0 THEN 100.0
                -- Nếu cả hai đều = 0: tăng trưởng 0%
                WHEN COALESCE(dtY.revenue, 0) = 0 AND COALESCE(dtT.revenue, 0) = 0 THEN 0.0
                -- Nếu hôm qua > 0: tính tăng trưởng (có thể âm nếu hôm nay < hôm qua)
                WHEN COALESCE(dtY.revenue, 0) > 0 THEN 
                    CAST(((CAST(COALESCE(dtT.revenue, 0) AS FLOAT) - CAST(COALESCE(dtY.revenue, 0) AS FLOAT)) / CAST(COALESCE(dtY.revenue, 0) AS FLOAT)) * 100 AS DECIMAL(18, 2))
                ELSE 0.0
            END AS TangTruong
        FROM DoanhThuToday dtT
        CROSS JOIN DoanhThuYesterday dtY
        CROSS JOIN DonHangToday dhT
        CROSS JOIN KhachToday khT
        """, nativeQuery = true)
    List<Object[]> thongKeTongQuan();

}
