package com.example.sever.repository;


import com.example.sever.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

//    @Query(value = "SELECT TOP 1 ma_don_hang FROM Orders WHERE ma_don_hang LIKE 'OD___-2024' ORDER BY ma_don_hang DESC", nativeQuery = true)
//    String findLastMaDonHang();

    @Query(value = "SELECT TOP 1 ma_don_hang FROM Orders ORDER BY ID DESC", nativeQuery = true)
    String findLastMaDonHang();

//    @Query("""
//        SELECT o
//        FROM Order o
//        LEFT JOIN o.idNhanVien nv
//        WHERE (:keyword IS NULL OR :keyword = '' OR
//               LOWER(o.maDonHang) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//               LOWER(o.tenKhachHang) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
//               o.sdtKhachHang LIKE CONCAT('%', :keyword, '%'))
//          AND (:loaiDon IS NULL OR :loaiDon = '' OR o.loaiDon = :loaiDon)
//          AND (:trangThaiDon IS NULL OR o.trangThai = :trangThaiDon)
//          AND (:trangThaiThanhToan IS NULL OR o.trangThaiThanhToan = :trangThaiThanhToan)
//          AND (:fromDate IS NULL OR o.ngayTao >= :fromDate)
//          AND (:toDate IS NULL OR o.ngayTao < :toDate)
//    """)
//    Page<Order> searchOrders(
//            @Param("keyword") String keyword,
//            @Param("loaiDon") String loaiDon,
//            @Param("trangThaiDon") Integer trangThaiDon,
//            @Param("trangThaiThanhToan") Integer trangThaiThanhToan,
//            @Param("fromDate") LocalDateTime fromDate,
//            @Param("toDate") LocalDateTime toDate,
//            Pageable pageable
//    );

    @Query("""
    SELECT o FROM Order o WHERE 1=1
    AND (:keyword IS NULL OR LOWER(o.maDonHang) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(o.tenKhachHang) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(o.sdtKhachHang) LIKE LOWER(CONCAT('%', :keyword, '%')))
    AND (:loaiDon IS NULL OR o.loaiDon = :loaiDon)
    AND (:trangThaiDon IS NULL OR o.trangThai = :trangThaiDon)
    AND (:trangThaiThanhToan IS NULL OR o.trangThaiThanhToan = :trangThaiThanhToan)
    AND (:fromDate IS NULL OR o.ngayTao >= :fromDate)
    AND (:toDate IS NULL OR o.ngayTao < :toDate)

    AND (
      :trangThaiDonForTaiQuay IS NULL 
      OR o.loaiDon != 'TAI_QUAY' 
      OR o.trangThai IN :trangThaiDonForTaiQuay
    )
    """)
    Page<Order> searchOrders(
            @Param("keyword") String keyword,
            @Param("loaiDon") String loaiDon,
            @Param("trangThaiDon") Integer trangThaiDon,
            @Param("trangThaiThanhToan") Integer trangThaiThanhToan,
            @Param("trangThaiDonForTaiQuay") List<Integer> trangThaiDonForTaiQuay,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

    @Query(
            value = "SELECT TOP 1 ma_don_hang " +
                    "FROM orders " +
                    "WHERE ma_don_hang LIKE :prefix + '%' " +
                    "ORDER BY ma_don_hang DESC",
            nativeQuery = true
    )
    String findLastMaDonHangByPrefix(@Param("prefix") String prefix);



    // Lấy tất cả đơn hàng trong khoảng thời gian
    @Query("SELECT o FROM Order o WHERE o.ngayTao BETWEEN :startDate AND :endDate")
    List<Order> findByNgayTaoBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Đếm đơn hàng theo trạng thái trong khoảng thời gian
    @Query("SELECT COUNT(o) FROM Order o WHERE o.trangThai = :status AND o.ngayTao BETWEEN :startDate AND :endDate")
    Long countByTrangThaiAndNgayTaoBetween(@Param("status") Integer status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Đếm tất cả đơn hàng trong khoảng thời gian
    @Query("SELECT COUNT(o) FROM Order o WHERE o.ngayTao BETWEEN :startDate AND :endDate")
    Long countByNgayTaoBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);



    //------------------------------Code huy bán onl-----------/


    @Query("SELECT o FROM Order o WHERE o.idTaiKhoan.id = :idTaiKhoan ORDER BY o.id DESC")
    List<Order> findByIdTaiKhoanOrderByIdDesc(@Param("idTaiKhoan") UUID idTaiKhoan);

    @Query("SELECT o FROM Order o WHERE o.maDonHang = :maDonHang AND o.sdtKhachHang = :sdt")
    List<Order> findByMaDonHangAndSdtKhachHang(@Param("maDonHang") String maDonHang, @Param("sdt") String sdt);

    @org.springframework.data.jpa.repository.Modifying
    @Query(value = "UPDATE dbo.Orders SET ngay_tao = GETDATE() WHERE ID = :id", nativeQuery = true)
    void updateNgayTaoById(@Param("id") UUID id);


    //------------------------------Code quý thống kê-----------/





    // Top 10 laptop bán chạy nhất (không filter năm - để test, dùng id_lap_top_ct trực tiếp)
    @Query(value = """
        SELECT TOP 10
            l.ID AS idLaptop,
            l.ten_san_pham AS tenSanPham,
            COUNT(*) AS soLuongBan
        FROM OrderCT oct
        JOIN [Orders] o 
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



    ///----
    // Doanh số theo tháng của 1 năm (có thêm top 10 laptop của mỗi tháng)
    @Query(value = """
        WITH ThongKeThang AS (
            SELECT 
                MONTH(o.ngay_tao) AS thang,
                SUM(o.tong_tien_thu_ho) AS doanhSo
            FROM [Orders] o
            -- ✅ Doanh thu chỉ tính đơn PAID + COMPLETED
            WHERE YEAR(o.ngay_tao) = :year
              AND o.trang_thai_thanh_toan = 1
              AND o.trang_thai = 6
            GROUP BY MONTH(o.ngay_tao)
        ),
        TopLaptopThang AS (
            SELECT 
                MONTH(o.ngay_tao) AS thang,
                l.ID AS idLaptop,
                l.ten_san_pham AS tenSanPham,
                -- ✅ Gom theo Laptop (không bị tách theo LaptopChiTiet)
                MIN(img.ImgURL) AS hinhAnh,
                COUNT(*) AS soLuongBan,
                -- ✅ Tránh double-count doanh thu do join OrderCT: dùng tổng giá dòng
                COALESCE(SUM(oct.gia_ban), 0) AS tongTienThuHo,
                ROW_NUMBER() OVER (PARTITION BY MONTH(o.ngay_tao) ORDER BY COUNT(*) DESC) AS rn
            FROM OrderCT oct
            JOIN [Orders] o ON o.ID = oct.id_order
            JOIN Seri s ON s.ID = oct.id_seri
            JOIN LaptopChiTiet lct ON lct.ID = s.id_lap_top_ct
            JOIN Laptop l ON l.ID = lct.id_lap_top
            OUTER APPLY (
                SELECT TOP 1 a.ImgURL
                FROM Anh a
                WHERE a.id_laptop_chi_tiet = lct.ID
                ORDER BY a.ID
            ) img
            WHERE YEAR(o.ngay_tao) = :year
              AND o.trang_thai_thanh_toan = 1
              AND o.trang_thai = 6
            GROUP BY MONTH(o.ngay_tao), l.ID, l.ten_san_pham
        )
        SELECT 
            tkt.thang,
            tkt.doanhSo,
            tlt.idLaptop,
            tlt.tenSanPham,
            tlt.hinhAnh,
            tlt.soLuongBan,
            tlt.tongTienThuHo
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
        FROM [Orders] o
        WHERE YEAR(o.ngay_tao) = :year
          AND MONTH(o.ngay_tao) = :month
          AND o.trang_thai_thanh_toan = 1
          AND o.trang_thai = 6
        GROUP BY DAY(o.ngay_tao)
    ),
    TopLaptopNgay AS (
        SELECT 
            DAY(o.ngay_tao) AS ngay,
            l.ID AS idLaptop,
            l.ten_san_pham AS tenSanPham,
            MIN(img.ImgURL) AS hinhAnh,
            COUNT(*) AS soLuongBan,
            COALESCE(SUM(oct.gia_ban), 0) AS tongTienThuHo,
            ROW_NUMBER() OVER (PARTITION BY DAY(o.ngay_tao) ORDER BY COUNT(*) DESC) AS rn
        FROM OrderCT oct
        JOIN [Orders] o ON o.ID = oct.id_order
        JOIN Seri s ON s.ID = oct.id_seri
        JOIN LaptopChiTiet lct ON lct.ID = s.id_lap_top_ct
        JOIN Laptop l ON l.ID = lct.id_lap_top
        OUTER APPLY (
            SELECT TOP 1 a.ImgURL
            FROM Anh a
            WHERE a.id_laptop_chi_tiet = lct.ID
            ORDER BY a.ID
        ) img
        WHERE YEAR(o.ngay_tao) = :year
          AND MONTH(o.ngay_tao) = :month
          AND o.trang_thai_thanh_toan = 1
          AND o.trang_thai = 6
        GROUP BY DAY(o.ngay_tao), l.ID, l.ten_san_pham
    )
    SELECT 
        tkn.ngay,
        tkn.doanhSo,
        tln.idLaptop,
        tln.tenSanPham,
        tln.hinhAnh,
        tln.soLuongBan,
        tln.tongTienThuHo
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
            FROM [Orders] o
            WHERE CAST(o.ngay_tao AS date) = :ngay
              AND o.trang_thai_thanh_toan = 1
              AND o.trang_thai = 6
        ),
        TopLaptop AS (
            SELECT TOP 10
                l.ID AS idLaptop,
                l.ten_san_pham AS tenSanPham,
                MIN(img.ImgURL) AS hinhAnh,
                COUNT(*) AS soLuongBan
            FROM OrderCT oct
            JOIN [Orders] o ON o.ID = oct.id_order
            JOIN Seri s ON s.ID = oct.id_seri
            JOIN LaptopChiTiet lct ON lct.ID = s.id_lap_top_ct
            JOIN Laptop l ON l.ID = lct.id_lap_top
            OUTER APPLY (
                SELECT TOP 1 a.ImgURL
                FROM Anh a
                WHERE a.id_laptop_chi_tiet = lct.ID
                ORDER BY a.ID
            ) img
            WHERE CAST(o.ngay_tao AS date) = :ngay
              AND o.trang_thai_thanh_toan = 1
              AND o.trang_thai = 6
            GROUP BY l.ID, l.ten_san_pham
            ORDER BY COUNT(*) DESC
        )
        SELECT 
            tk.ngay,
            tk.doanhSo,
            tl.idLaptop,
            tl.tenSanPham,
            tl.hinhAnh,
            tl.soLuongBan
        FROM ThongKe tk
        LEFT JOIN TopLaptop tl ON 1=1
        ORDER BY tl.soLuongBan DESC
        """, nativeQuery = true)
    List<Object[]> thongKeMotNgay(@Param("ngay") LocalDate ngay);

    // Top 10 laptop bán chạy nhất theo năm (dùng id_lap_top_ct trực tiếp từ Seri)
    @Query(value = """
        SELECT TOP 10
            l.ID AS idLaptop,
            l.ten_san_pham AS tenSanPham,
            MIN(img.ImgURL) AS hinhAnh,
            COUNT(*) AS soLuongBan,
            COALESCE(SUM(oct.gia_ban), 0) AS tongTienThuHo
        FROM OrderCT oct
        JOIN [Orders] o 
            ON o.ID = oct.id_order    
        JOIN Seri s 
            ON s.ID = oct.id_seri      
        JOIN LaptopChiTiet lct 
            ON lct.ID = s.id_lap_top_ct
        JOIN Laptop l 
            ON l.ID = lct.id_lap_top
        OUTER APPLY (
            SELECT TOP 1 a.ImgURL
            FROM Anh a
            WHERE a.id_laptop_chi_tiet = lct.ID
            ORDER BY a.ID
        ) img
        WHERE YEAR(o.ngay_tao) = :year
          AND o.trang_thai_thanh_toan = 1
          AND o.trang_thai = 6
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
        FROM [Orders] o
        WHERE YEAR(o.ngay_tao) = :year
        """, nativeQuery = true)
    Long countDonHangTheoNam(@Param("year") int year);

    // Debug: Kiểm tra số OrderCT có trong năm
    @Query(value = """
        SELECT COUNT(*) 
        FROM OrderCT oct
        JOIN [Orders] o ON o.ID = oct.id_order
        WHERE YEAR(o.ngay_tao) = :year
        """, nativeQuery = true)
    Long countOrderCTTheoNam(@Param("year") int year);

    // Debug: Kiểm tra số Seri có liên kết
    @Query(value = """
        SELECT COUNT(*) 
        FROM OrderCT oct
        JOIN [Orders] o ON o.ID = oct.id_order
        JOIN Seri s ON s.ID = oct.id_seri
        WHERE YEAR(o.ngay_tao) = :year
        """, nativeQuery = true)
    Long countSeriLienKet(@Param("year") int year);

    // Debug: Kiểm tra số PhienBan có liên kết
    @Query(value = """
        SELECT COUNT(*) 
        FROM OrderCT oct
        JOIN [Orders] o ON o.ID = oct.id_order
        JOIN Seri s ON s.ID = oct.id_seri
        JOIN PhienBan pb ON pb.ID = s.id_phien_ban
        WHERE YEAR(o.ngay_tao) = :year
        """, nativeQuery = true)
    Long countPhienBanLienKet(@Param("year") int year);

    // Debug: Kiểm tra số Seri có id_lap_top_ct (đường ngắn)
    @Query(value = """
        SELECT COUNT(*) 
        FROM OrderCT oct
        JOIN [Orders] o ON o.ID = oct.id_order
        JOIN Seri s ON s.ID = oct.id_seri
        JOIN LaptopChiTiet lct ON lct.ID = s.id_lap_top_ct
        WHERE YEAR(o.ngay_tao) = :year
        """, nativeQuery = true)
    Long countSeriCoLaptopChiTiet(@Param("year") int year);

    // Debug: Kiểm tra các năm có dữ liệu
    @Query(value = """
        SELECT DISTINCT YEAR(ngay_tao) AS nam
        FROM [Orders]
        WHERE ngay_tao IS NOT NULL
        ORDER BY nam DESC
        """, nativeQuery = true)
    List<Integer> getCacNamCoDuLieu();

    // Top 10 laptop bán chạy nhất (không filter năm - để test, dùng id_lap_top_ct trực tiếp)
    @Query(value = """
    SELECT TOP 10
        l.ID AS idLaptop,
        l.ten_san_pham AS tenSanPham,
        COUNT(*) AS soLuongBan,
        COALESCE(SUM(oct.gia_ban), 0) AS tongTienThuHo
    FROM OrderCT oct
    JOIN Orders o ON o.ID = oct.id_order
    JOIN Seri s ON s.ID = oct.id_seri
    JOIN LaptopChiTiet lct ON lct.ID = s.id_lap_top_ct
    JOIN Laptop l ON l.ID = lct.id_lap_top
    WHERE o.ngay_tao >= DATEFROMPARTS(:nam, :thang, 1)
      AND o.ngay_tao <  DATEADD(MONTH, 1, DATEFROMPARTS(:nam, :thang, 1))
      AND o.trang_thai_thanh_toan = 1
      AND o.trang_thai = 6
    GROUP BY
        l.ID,
        l.ten_san_pham
    ORDER BY
        soLuongBan DESC
""", nativeQuery = true)
    List<Object[]> topLaptopBanChayTheoThangNam(
            @Param("thang") int thang,
            @Param("nam") int nam
    );



    // Top 10 laptop bán chạy nhất theo tháng
    @Query(value = """
    SELECT TOP 10
        l.ID AS idLaptop,
        l.ten_san_pham AS tenSanPham,
        MIN(img.ImgURL) AS hinhAnh,
        COUNT(*) AS soLuongBan,
        COALESCE(SUM(oct.gia_ban), 0) AS tongTienThuHo
    FROM OrderCT oct
    JOIN [Orders] o 
        ON o.ID = oct.id_order    
    JOIN Seri s 
        ON s.ID = oct.id_seri      
    JOIN LaptopChiTiet lct 
        ON lct.ID = s.id_lap_top_ct
    JOIN Laptop l 
        ON l.ID = lct.id_lap_top
    OUTER APPLY (
        SELECT TOP 1 a.ImgURL
        FROM Anh a
        WHERE a.id_laptop_chi_tiet = lct.ID
        ORDER BY a.ID
    ) img
    WHERE YEAR(o.ngay_tao) = :year
      AND MONTH(o.ngay_tao) = :month
      AND o.trang_thai_thanh_toan = 1
      AND o.trang_thai = 6
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
            MIN(img.ImgURL) AS hinhAnh,
            COUNT(*) AS soLuongBan,
            COALESCE(SUM(oct.gia_ban), 0) AS tongTienThuHo
        FROM OrderCT oct
        JOIN [Orders] o 
            ON o.ID = oct.id_order    
        JOIN Seri s 
            ON s.ID = oct.id_seri      
        JOIN LaptopChiTiet lct 
            ON lct.ID = s.id_lap_top_ct
        JOIN Laptop l 
            ON l.ID = lct.id_lap_top
        OUTER APPLY (
            SELECT TOP 1 a.ImgURL
            FROM Anh a
            WHERE a.id_laptop_chi_tiet = lct.ID
            ORDER BY a.ID
        ) img
        WHERE CAST(o.ngay_tao AS date) = :ngay
          AND o.trang_thai_thanh_toan = 1
          AND o.trang_thai = 6
        GROUP BY 
            l.ID,
            l.ten_san_pham
        ORDER BY 
            soLuongBan DESC
        """, nativeQuery = true)
    List<Object[]> topLaptopBanChayNhatTheoNgay(@Param("ngay") LocalDate ngay);

    // Thống kê tổng quan (doanh thu, đơn hàng, tăng trưởng)
    @Query(value = """
    DECLARE 
        @today DATE = CAST(GETDATE() AS DATE),
        @yesterday DATE = DATEADD(DAY, -1, CAST(GETDATE() AS DATE));

    ;WITH DoanhThuToday AS (
        SELECT COALESCE(SUM(o.tong_tien_thu_ho), 0) AS revenue
        FROM [dbo].[Orders] o
        WHERE CAST(o.ngay_tao AS DATE) = @today
          AND o.trang_thai_thanh_toan = 1
          AND o.trang_thai = 6
    ),
    DoanhThuYesterday AS (
        SELECT COALESCE(SUM(o.tong_tien_thu_ho), 0) AS revenue
        FROM [dbo].[Orders] o
        WHERE CAST(o.ngay_tao AS DATE) = @yesterday
          AND o.trang_thai_thanh_toan = 1
          AND o.trang_thai = 6
    ),
    DonHangToday AS (
        SELECT COUNT(*) AS countOrder
        FROM [dbo].[Orders] o 
        WHERE CAST(o.ngay_tao AS DATE) = @today
          AND o.trang_thai_thanh_toan = 1
          AND o.trang_thai = 6
    )

    SELECT
        COALESCE(dtT.revenue, 0) AS TongDoanhThu,
        COALESCE(dhT.countOrder, 0) AS TongDonHang,
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
    """, nativeQuery = true)
    List<Object[]> thongKeTongQuan();

    // Thống kê đơn hàng theo từng trạng thái
    @Query(value = """
        SELECT 
            -- ✅ Mapping đúng theo OrderStatus enum
            COALESCE(SUM(CASE WHEN o.trang_thai = 1 THEN 1 ELSE 0 END), 0) AS donHangChoXacNhan,
            COALESCE(SUM(CASE WHEN o.trang_thai IN (2, 3) THEN 1 ELSE 0 END), 0) AS donHangDangXuLy,
            COALESCE(SUM(CASE WHEN o.trang_thai = 4 THEN 1 ELSE 0 END), 0) AS donHangDangGiao,
            COALESCE(SUM(CASE WHEN o.trang_thai = 6 THEN 1 ELSE 0 END), 0) AS donHangHoanThanh,
            COALESCE(SUM(CASE WHEN o.trang_thai = 7 THEN 1 ELSE 0 END), 0) AS donHangDaHuy
        FROM [Orders] o
        """, nativeQuery = true)
    List<Object[]> thongKeDonHangTheoTrangThai();

    @Query("""
    SELECT o
    FROM Order o
    WHERE o.idNhanVien IS NULL
      AND o.ngayTao < :cutoff
      AND o.trangThai = :pendingConfirmStatus
      AND LOWER(o.loaiDon) <> LOWER(:taiQuay)
""")
    List<Order> findExpiredCodPendingConfirmOrders(
            @Param("cutoff") Instant cutoff,
            @Param("taiQuay") String taiQuay,
            @Param("pendingConfirmStatus") int pendingConfirmStatus
    );

    @Query("""
        SELECT o
        FROM Order o
        WHERE o.idNhanVien IS NULL
          AND o.ngayTao < :cutoff
          AND o.trangThaiThanhToan = :paymentUnpaid
          AND o.trangThai < :shippingStatus
          AND o.trangThai <> :canceledStatus
          AND o.trangThai <> :completedStatus
          AND LOWER(o.loaiDon) <> LOWER(:taiQuay)
    """)
    List<Order> findExpiredUnpaidOnlineOrders(
            @Param("cutoff") Instant cutoff,
            @Param("paymentUnpaid") int paymentUnpaid,
            @Param("shippingStatus") int shippingStatus,
            @Param("canceledStatus") int canceledStatus,
            @Param("completedStatus") int completedStatus,
            @Param("taiQuay") String taiQuay
    );

    // Tổng khách đã mua (distinct id_tai_khoan) theo điều kiện PAID + COMPLETED
    @Query(value = """
    SELECT COUNT(DISTINCT o.id_tai_khoan)
    FROM [Orders] o
    WHERE o.id_tai_khoan IS NOT NULL
      AND o.trang_thai_thanh_toan = 1
      AND o.trang_thai = 6
""", nativeQuery = true)
    Long countDistinctCustomersPurchased();

    // Mỗi khách đã mua bao nhiêu đơn + tổng chi tiêu
    @Query(value = """
    SELECT 
        o.id_tai_khoan          AS idTaiKhoan,
        MAX(o.ten_khach_hang)   AS tenKhachHang,
        MAX(o.sdt_khach_hang)   AS sdtKhachHang,
        COUNT(*)                AS soDonDaMua,
        COALESCE(SUM(o.tong_tien_thu_ho), 0) AS tongChiTieu
    FROM [Orders] o
    WHERE o.id_tai_khoan IS NOT NULL
      AND o.trang_thai_thanh_toan = 1
      AND o.trang_thai = 6
    GROUP BY o.id_tai_khoan
    ORDER BY soDonDaMua DESC
""", nativeQuery = true)
    List<Object[]> thongKeSoDonTheoKhachHang();


}