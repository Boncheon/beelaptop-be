package com.example.sever.service;

import com.example.sever.dto.Pos.OrderListDTO;
import com.example.sever.dto.Pos.PageResult;
import com.example.sever.entity.Order;
import com.example.sever.entity.TaiKhoan;
import com.example.sever.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderManagementService {

    private final OrderRepository ordersRepository;

    public PageResult<OrderListDTO> searchOrders(
            String keyword,
            String loaiDon,
            Integer trangThaiDon,
            Integer trangThaiThanhToan,
            List<Integer> trangThaiDonForTaiQuay, // THAM SỐ MỚI
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable) {

        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.plusDays(1).atStartOfDay() : null;

        Page<Order> page = ordersRepository.searchOrders(
                normalize(keyword),
                normalize(loaiDon),
                trangThaiDon,
                trangThaiThanhToan,
                trangThaiDonForTaiQuay,
                fromDateTime,
                toDateTime,
                pageable
        );

        Page<OrderListDTO> mapped = page.map(this::toDTO);

        return PageResult.<OrderListDTO>builder()
                .content(mapped.getContent())
                .page(mapped.getNumber())
                .size(mapped.getSize())
                .totalElements(mapped.getTotalElements())
                .totalPages(mapped.getTotalPages())
                .build();
    }

    private String normalize(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private OrderListDTO toDTO(Order o) {
        // ✅ FIX: idNhanVien có thể null
        String maNhanVien = null;

        TaiKhoan nv = o.getIdNhanVien();
        if (nv != null) {
            // Nếu TaiKhoan của bạn có field String idTaiKhoan (mã nhân viên) thì dùng cái này
            // Còn nếu không có, đổi sang nv.getId().toString() hoặc nv.getEmail()/nv.getTenDangNhap()
            maNhanVien = nv.getIdTaiKhoan(); // giữ đúng theo code bạn đang dùng
            // nếu nv.getIdTaiKhoan() có thể null thì vẫn ok => maNhanVien null
        }

        return OrderListDTO.builder()
                .id(o.getId())
                .maDonHang(o.getMaDonHang())
                .maNhanVien(maNhanVien)
                .tenKhachHang(o.getTenKhachHang())
                .sdtKhachHang(o.getSdtKhachHang())
                .tongTien(o.getTongTienThuHo())
                .loaiDon(o.getLoaiDon())
                .trangThaiDon(o.getTrangThai())
                .trangThaiThanhToan(o.getTrangThaiThanhToan())
                .ngayTao(o.getNgayTao())
                .ngayCapNhat(o.getNgayCapNhat())
                .build();
    }
}
