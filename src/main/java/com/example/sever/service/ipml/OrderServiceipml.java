package com.example.sever.service.ipml;

import com.example.sever.dto.OrderDTO.OrderRequest;
import com.example.sever.dto.OrderDTO.OrderRespone;
import com.example.sever.entity.GiamGiaHoaDon;
import com.example.sever.entity.Order;
import com.example.sever.entity.PhieuGiamGia;
import com.example.sever.repository.GiamGiaHoaDonRepository;
import com.example.sever.repository.OrderRepository;
import com.example.sever.service.OrderService;
import com.example.sever.service.PhieuGiamGiaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderServiceipml implements OrderService {

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private GiamGiaHoaDonRepository giamGiaHoaDonRepo;

    @Autowired
    private PhieuGiamGiaService phieuGiamGiaService;


    @Override
    public OrderRespone taoDonHang(OrderRequest request) {

        Order order = modelMapper.map(request, Order.class);

        // Gán các giá trị bắt buộc
        order.setIdOrder("ORD" + System.currentTimeMillis()); // ✅ tránh lỗi UNIQUE NULL
        order.setGiaTriChuaGiam(request.getTongTien());
        order.setTongTienThuHo(request.getTongTien());
        order.setMaDonHang(generateMaDonHang()); // Mã đơn hiển thị
        order.setTrangThai(1); // Trạng thái mặc định

        // Tìm phiếu giảm giá phù hợp
        PhieuGiamGia phieu = phieuGiamGiaService.findBestCouponForOrder(order.getGiaTriChuaGiam());
        BigDecimal soTienGiam = BigDecimal.ZERO;

        if (phieu != null) {
            soTienGiam = phieuGiamGiaService.calculateDiscount(phieu, order.getGiaTriChuaGiam());
            order.setGiaTriGiamGia(soTienGiam);
        }

        // ✅ 1. Lưu đơn hàng trước (phải có ID mới tạo quan hệ được)
        orderRepo.save(order);

        // ✅ 2. Nếu có phiếu giảm giá → tạo bản ghi giảm giá đơn
        if (phieu != null) {
            GiamGiaHoaDon giam = new GiamGiaHoaDon();
            giam.setIdGiamgiahoadon("GG" + System.currentTimeMillis()); // ✅ fix lỗi trùng null
            giam.setIdOrders(order);
            giam.setIdPhieuGiamGia(phieu);
            giam.setSoTienGiam(soTienGiam);
            giam.setTenPhieu(phieu.getTen());

            giamGiaHoaDonRepo.save(giam);
        }

        // ✅ Tạo phản hồi trả về
        OrderRespone res = new OrderRespone();
        res.setMaDonHang(order.getMaDonHang());
        res.setTongTien(order.getGiaTriChuaGiam());
        res.setSoTienGiam(soTienGiam);
        res.setPhieuGiamGia(phieu != null ? phieu.getIdPhieugiamgia() : null);
        res.setTongPhaiTra(order.getGiaTriChuaGiam().subtract(soTienGiam));

        return res;


    }


    private String generateMaDonHang(){
        String lastCode = orderRepo.findLastMaDonHang();
        int next = 1;

        if (lastCode != null && lastCode.length() >= 8) {
            try {
                String numberStr = lastCode.substring(2, 5); // VD: OD007-2024 → "007"
                next = Integer.parseInt(numberStr) + 1;
            } catch (NumberFormatException ignored) {
                next = 1;
            }
        }

        return String.format("OD%03d-2024", next);
    }




}
