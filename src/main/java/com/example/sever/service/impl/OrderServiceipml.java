package com.example.sever.service.impl;

import com.example.sever.dto.OrderDTO.OrderRequest;
import com.example.sever.dto.OrderDTO.OrderRespone;
import com.example.sever.entity.*;
import com.example.sever.repository.*;
import com.example.sever.service.OrderService;
import com.example.sever.service.PhieuGiamGiaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceipml implements OrderService {

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private SeriRepository seriRepository;

    @Autowired
    private OrderCTRepository orderCTRepository;

    @Autowired
    private GiamGiaHoaDonRepository giamGiaHoaDonRepo;

    @Autowired
    private PhieuGiamGiaService phieuGiamGiaService;

    @Autowired
    private PhieuGiamGiaRepository phieuGiamGiaRepository;

    // dùng để lookup user khi principal không phải TaiKhoan
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Override
    @Transactional
    public OrderRespone taoDonHang(OrderRequest request) {

        Order order = modelMapper.map(request, Order.class);

        order.setId(UUID.randomUUID());
        order.setIdOrder("ORD" + System.currentTimeMillis());
        order.setGiaTriChuaGiam(request.getTongTien());
        order.setGiaTriGiamGia(BigDecimal.ZERO);
        order.setTongTienThuHo(request.getTongTien());
        order.setMaDonHang(generateMaDonHang());
        order.setTrangThai(1);

        // 🔥 Nhân viên / admin tạo đơn online
        UUID nhanVienId = getCurrentUserId();
        if (nhanVienId != null) {
            TaiKhoan nhanVien = new TaiKhoan();
            nhanVien.setId(nhanVienId);
            order.setIdNhanVien(nhanVien);   // <-- FIX
        }

        PhieuGiamGia phieu =
                phieuGiamGiaService.findBestCouponForOrder(order.getGiaTriChuaGiam());
        BigDecimal soTienGiam = BigDecimal.ZERO;

        if (phieu != null) {
            soTienGiam =
                    phieuGiamGiaService.calculateDiscount(phieu, order.getGiaTriChuaGiam());
            order.setGiaTriGiamGia(soTienGiam);
            order.setTongTienThuHo(order.getGiaTriChuaGiam().subtract(soTienGiam));
        }

        orderRepo.save(order);

        if (phieu != null) {
            GiamGiaHoaDon giam = new GiamGiaHoaDon();
            giam.setIdGiamgiahoadon("GG" + System.currentTimeMillis());
            giam.setIdOrders(order);
            giam.setIdPhieuGiamGia(phieu);
            giam.setSoTienTruocGiam(order.getGiaTriChuaGiam());
            giam.setSoTienSauGiam(order.getTongTienThuHo());

            giamGiaHoaDonRepo.save(giam);

            // Trừ số lượng voucher cho đơn online
            if (phieu.getSoLuong() != null) {
                int current = phieu.getSoLuong();
                if (current > 0) {
                    phieu.setSoLuong(current - 1);
                    if (phieu.getSoLuong() <= 0) {
                        phieu.setTrangThai(0);
                    }
                    phieuGiamGiaRepository.save(phieu);
                }
            }
        }

        OrderRespone res = new OrderRespone();
        res.setMaDonHang(order.getMaDonHang());
        res.setTongTien(order.getGiaTriChuaGiam());
        res.setSoTienGiam(soTienGiam);
        res.setPhieuGiamGia(phieu != null ? phieu.getIdPhieugiamgia() : null);
        res.setTongPhaiTra(order.getTongTienThuHo());

        return res;
    }

    private String generateMaDonHang() {
        String lastCode = orderRepo.findLastMaDonHang();
        int next = 1;

        if (lastCode != null && lastCode.length() >= 8) {
            try {
                String numberStr = lastCode.substring(2, 5); // OD007-2024
                next = Integer.parseInt(numberStr) + 1;
            } catch (NumberFormatException ignored) {
                next = 1;
            }
        }

        return String.format("OD%03d-2024", next);
    }

    @Override
    @Transactional
    public void addItemsToOrder(UUID orderId, List<UUID> seriIds) {
        if (seriIds == null || seriIds.isEmpty()) {
            throw new IllegalArgumentException("Danh sách seri không được rỗng");
        }

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        var seriList = seriRepository.findAllById(seriIds);
        if (seriList.size() != seriIds.size()) {
            throw new RuntimeException("Có seri không tồn tại trong hệ thống");
        }

        for (Seri seri : seriList) {
            if (seri.getTrangThai() == null || seri.getTrangThai() != 1) {
                throw new RuntimeException("Seri " + seri.getIdSeri() + " không khả dụng");
            }

            var ct = seri.getIdLapTopCt();
            if (ct == null) {
                throw new RuntimeException("Seri " + seri.getIdSeri() + " chưa gắn LaptopChiTiet");
            }

            OrderCT line = new OrderCT();
            line.setIdOrder(order);
            line.setIdSeri(seri);
            line.setGiaBan(ct.getGiaBan());

            orderCTRepository.save(line);

            seri.setTrangThai(2); // ĐANG CHỜ
        }

        seriRepository.saveAll(seriList);
    }

    // lấy ID tài khoản đang đăng nhập (nhân viên / admin)
    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof TaiKhoan tk) {
            return tk.getId();
        }

        String username = auth.getName(); // getUsername()
        if (username == null) {
            return null;
        }

        return taiKhoanRepository.findBySoDienThoai(username)
                .map(TaiKhoan::getId)
                .orElse(null);
    }
}
