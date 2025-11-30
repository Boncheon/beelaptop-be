package com.example.sever.service.impl;

import com.example.sever.dto.response.ThongKeResponseDTO;
import com.example.sever.dto.response.ThongKeTongQuanResponseDTO;
import com.example.sever.dto.response.TopLaptopBanChayResponseDTO;
import com.example.sever.repository.OrderRepository;
import com.example.sever.service.ThongKeService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ThongKeServiceImpl implements ThongKeService {

    private final OrderRepository orderRepository;

    public ThongKeServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    // Helper method để convert Object[] sang TopLaptopBanChayResponseDTO (từ query riêng)
    private TopLaptopBanChayResponseDTO convertToTopLaptop(List<Object[]> result) {
        if (result == null || result.isEmpty() || result.get(0) == null) {
            return TopLaptopBanChayResponseDTO.builder()
                    .idLaptop(null)
                    .tenSanPham("Không có dữ liệu")
                    .soLuongBan(0L)
                    .build();
        }

        Object[] row = result.get(0);
        UUID idLaptop = null;
        if (row[0] != null) {
            if (row[0] instanceof UUID) {
                idLaptop = (UUID) row[0];
            } else if (row[0] instanceof String) {
                idLaptop = UUID.fromString((String) row[0]);
            }
        }
        
        return TopLaptopBanChayResponseDTO.builder()
                .idLaptop(idLaptop)
                .tenSanPham(row[1] != null ? (String) row[1] : "N/A")
                .soLuongBan(row[2] != null ? ((Number) row[2]).longValue() : 0L)
                .build();
    }

    // Helper method để convert từ row query thống kê (có thêm top laptop)
    // Format: [label, doanhSo, idLaptop, tenSanPham, soLuongBan]
    private TopLaptopBanChayResponseDTO convertToTopLaptopFromRow(Object[] row, int startIndex) {
        if (row == null || row.length <= startIndex || row[startIndex] == null) {
            return null;
        }
        
        UUID idLaptop = null;
        Object idObj = row[startIndex];
        if (idObj instanceof UUID) {
            idLaptop = (UUID) idObj;
        } else if (idObj instanceof String) {
            try {
                idLaptop = UUID.fromString((String) idObj);
            } catch (Exception e) {
                return null;
            }
        }
        
        return TopLaptopBanChayResponseDTO.builder()
                .idLaptop(idLaptop)
                .tenSanPham(row.length > startIndex + 1 && row[startIndex + 1] != null 
                    ? (String) row[startIndex + 1] : "N/A")
                .soLuongBan(row.length > startIndex + 2 && row[startIndex + 2] != null 
                    ? ((Number) row[startIndex + 2]).longValue() : 0L)
                .build();
    }

    // Helper method để convert list Object[] thành List<TopLaptopBanChayResponseDTO>
    // Hỗ trợ 2 format:
    // - 3 cột: [idLaptop, tenSanPham, soLuongBan]
    // - 5 cột: [ngay, doanhSo, idLaptop, tenSanPham, soLuongBan]
    private List<TopLaptopBanChayResponseDTO> convertToListTopLaptop(List<Object[]> results) {
        if (results == null || results.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<TopLaptopBanChayResponseDTO> list = new ArrayList<>();
        for (Object[] row : results) {
            if (row == null) {
                continue;
            }
            
            // Xác định offset dựa trên số cột
            int idLaptopIndex, tenSanPhamIndex, soLuongBanIndex;
            
            if (row.length >= 5) {
                // Format 5 cột: [ngay, doanhSo, idLaptop, tenSanPham, soLuongBan]
                idLaptopIndex = 2;
                tenSanPhamIndex = 3;
                soLuongBanIndex = 4;
            } else if (row.length >= 3) {
                // Format 3 cột: [idLaptop, tenSanPham, soLuongBan]
                idLaptopIndex = 0;
                tenSanPhamIndex = 1;
                soLuongBanIndex = 2;
            } else {
                continue; // Không đủ cột
            }
            
            UUID idLaptop = null;
            if (row[idLaptopIndex] != null) {
                if (row[idLaptopIndex] instanceof UUID) {
                    idLaptop = (UUID) row[idLaptopIndex];
                } else if (row[idLaptopIndex] instanceof String) {
                    try {
                        idLaptop = UUID.fromString((String) row[idLaptopIndex]);
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
            
            String tenSanPham = null;
            if (row[tenSanPhamIndex] != null) {
                if (row[tenSanPhamIndex] instanceof String) {
                    tenSanPham = (String) row[tenSanPhamIndex];
                } else {
                    tenSanPham = String.valueOf(row[tenSanPhamIndex]);
                }
            }
            if (tenSanPham == null) {
                tenSanPham = "N/A";
            }
            
            Long soLuongBan = 0L;
            if (row[soLuongBanIndex] != null) {
                if (row[soLuongBanIndex] instanceof Number) {
                    soLuongBan = ((Number) row[soLuongBanIndex]).longValue();
                }
            }
            
            list.add(TopLaptopBanChayResponseDTO.builder()
                    .idLaptop(idLaptop)
                    .tenSanPham(tenSanPham)
                    .soLuongBan(soLuongBan)
                    .build());
        }
        return list;
    }

    @Override
    public List<ThongKeResponseDTO> thongKe12Thang(int nam1, int nam2) {

        var dsNam1 = orderRepository.thongKeTheoNam(nam1);
        var dsNam2 = orderRepository.thongKeTheoNam(nam2);

        // Tạo map với key là tháng, value là [doanhSo, listTopLaptop]
        // Mỗi tháng có thể có nhiều rows (mỗi row là 1 laptop trong top 10)
        Map<Integer, BigDecimal> doanhSoMap1 = new HashMap<>();
        Map<Integer, List<TopLaptopBanChayResponseDTO>> topLaptopMap1 = new HashMap<>();
        
        for (Object[] row : dsNam1) {
            int thang = ((Number) row[0]).intValue();
            if (!doanhSoMap1.containsKey(thang)) {
                doanhSoMap1.put(thang, (BigDecimal) row[1]);
                topLaptopMap1.put(thang, new ArrayList<>());
            }
            // Thêm laptop vào list nếu có
            TopLaptopBanChayResponseDTO laptop = convertToTopLaptopFromRow(row, 2);
            if (laptop != null) {
                topLaptopMap1.get(thang).add(laptop);
            }
        }

        Map<Integer, BigDecimal> doanhSoMap2 = new HashMap<>();
        Map<Integer, List<TopLaptopBanChayResponseDTO>> topLaptopMap2 = new HashMap<>();
        
        for (Object[] row : dsNam2) {
            int thang = ((Number) row[0]).intValue();
            if (!doanhSoMap2.containsKey(thang)) {
                doanhSoMap2.put(thang, (BigDecimal) row[1]);
                topLaptopMap2.put(thang, new ArrayList<>());
            }
            TopLaptopBanChayResponseDTO laptop = convertToTopLaptopFromRow(row, 2);
            if (laptop != null) {
                topLaptopMap2.get(thang).add(laptop);
            }
        }

        List<ThongKeResponseDTO> result = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            result.add(
                    ThongKeResponseDTO.builder()
                            .label(i)
                            .value1(doanhSoMap1.getOrDefault(i, BigDecimal.ZERO))
                            .value2(doanhSoMap2.getOrDefault(i, BigDecimal.ZERO))
                            .topLaptop1(topLaptopMap1.getOrDefault(i, new ArrayList<>()))
                            .topLaptop2(topLaptopMap2.getOrDefault(i, new ArrayList<>()))
                            .build()
            );
        }

        return result;
    }

    @Override
    public List<ThongKeResponseDTO> thongKeTheoNgay(int nam1, int thang1, int nam2, int thang2) {

        var ds1 = orderRepository.thongKeTheoThang(nam1, thang1);
        var ds2 = orderRepository.thongKeTheoThang(nam2, thang2);

        // Tạo map với key là ngày, value là [doanhSo, listTopLaptop]
        Map<Integer, BigDecimal> doanhSoMap1 = new HashMap<>();
        Map<Integer, List<TopLaptopBanChayResponseDTO>> topLaptopMap1 = new HashMap<>();
        
        for (Object[] row : ds1) {
            int ngay = ((Number) row[0]).intValue();
            if (!doanhSoMap1.containsKey(ngay)) {
                doanhSoMap1.put(ngay, (BigDecimal) row[1]);
                topLaptopMap1.put(ngay, new ArrayList<>());
            }
            TopLaptopBanChayResponseDTO laptop = convertToTopLaptopFromRow(row, 2);
            if (laptop != null) {
                topLaptopMap1.get(ngay).add(laptop);
            }
        }

        Map<Integer, BigDecimal> doanhSoMap2 = new HashMap<>();
        Map<Integer, List<TopLaptopBanChayResponseDTO>> topLaptopMap2 = new HashMap<>();
        
        for (Object[] row : ds2) {
            int ngay = ((Number) row[0]).intValue();
            if (!doanhSoMap2.containsKey(ngay)) {
                doanhSoMap2.put(ngay, (BigDecimal) row[1]);
                topLaptopMap2.put(ngay, new ArrayList<>());
            }
            TopLaptopBanChayResponseDTO laptop = convertToTopLaptopFromRow(row, 2);
            if (laptop != null) {
                topLaptopMap2.get(ngay).add(laptop);
            }
        }

        int days = Month.of(thang1).length(Year.isLeap(nam1));

        List<ThongKeResponseDTO> result = new ArrayList<>();

        for (int day = 1; day <= days; day++) {
            result.add(
                    ThongKeResponseDTO.builder()
                            .label(day)
                            .value1(doanhSoMap1.getOrDefault(day, BigDecimal.ZERO))
                            .value2(doanhSoMap2.getOrDefault(day, BigDecimal.ZERO))
                            .topLaptop1(topLaptopMap1.getOrDefault(day, new ArrayList<>()))
                            .topLaptop2(topLaptopMap2.getOrDefault(day, new ArrayList<>()))
                            .build()
            );
        }

        return result;
    }

    @Override
    public List<ThongKeResponseDTO> soSanhHaiNgay(LocalDate ngay1, LocalDate ngay2) {
        List<Object[]> rs1 = orderRepository.thongKeMotNgay(ngay1);
        List<Object[]> rs2 = orderRepository.thongKeMotNgay(ngay2);

        // Lấy doanh số từ row đầu tiên (doanh số giống nhau cho tất cả rows)
        BigDecimal v1 = !rs1.isEmpty() && rs1.get(0).length > 1
                ? (BigDecimal) rs1.get(0)[1]
                : BigDecimal.ZERO;

        BigDecimal v2 = !rs2.isEmpty() && rs2.get(0).length > 1
                ? (BigDecimal) rs2.get(0)[1]
                : BigDecimal.ZERO;

        // Lấy top 10 laptop từ tất cả rows
        List<TopLaptopBanChayResponseDTO> topLaptopList1 = convertToListTopLaptop(rs1);
        List<TopLaptopBanChayResponseDTO> topLaptopList2 = convertToListTopLaptop(rs2);

        return List.of(
                ThongKeResponseDTO.builder()
                        .label(ngay1.getDayOfMonth())
                        .value1(v1)
                        .value2(BigDecimal.ZERO)
                        .topLaptop1(topLaptopList1)
                        .topLaptop2(new ArrayList<>())
                        .build(),

                ThongKeResponseDTO.builder()
                        .label(ngay2.getDayOfMonth())
                        .value1(BigDecimal.ZERO)
                        .value2(v2)
                        .topLaptop1(new ArrayList<>())
                        .topLaptop2(topLaptopList2)
                        .build()
        );
    }

    @Override
    public TopLaptopBanChayResponseDTO topLaptopBanChayNhat(int nam) {
        // Method này vẫn trả về top 1 để tương thích với API cũ
        List<Object[]> results = orderRepository.topLaptopBanChayNhat(nam);
        List<TopLaptopBanChayResponseDTO> list = convertToListTopLaptop(results);
        if (list.isEmpty()) {
            return TopLaptopBanChayResponseDTO.builder()
                    .idLaptop(null)
                    .tenSanPham("Không có dữ liệu")
                    .soLuongBan(0L)
                    .build();
        }
        return list.get(0);
    }

    @Override
    public ThongKeTongQuanResponseDTO thongKeTongQuan() {
        List<Object[]> results = orderRepository.thongKeTongQuan();
        
        if (results == null || results.isEmpty() || results.get(0) == null) {
            return ThongKeTongQuanResponseDTO.builder()
                    .tongDoanhThu(BigDecimal.ZERO)
                    .tongDonHang(0L)
                    .tongKhachHang(0L)
                    .tangTruong(BigDecimal.ZERO)
                    .build();
        }
        
        Object[] row = results.get(0);
        
        BigDecimal tongDoanhThu = row[0] != null ? (BigDecimal) row[0] : BigDecimal.ZERO;
        Long tongDonHang = row[1] != null ? ((Number) row[1]).longValue() : 0L;
        Long tongKhachHang = row[2] != null ? ((Number) row[2]).longValue() : 0L;
        BigDecimal tangTruong = row[3] != null ? (BigDecimal) row[3] : BigDecimal.ZERO;
        
        return ThongKeTongQuanResponseDTO.builder()
                .tongDoanhThu(tongDoanhThu)
                .tongDonHang(tongDonHang)
                .tongKhachHang(tongKhachHang)
                .tangTruong(tangTruong)
                .build();
    }
}

