package com.example.sever.service.impl;

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
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;

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
        Random random = new Random();
        StringBuilder ord = new StringBuilder("ORD");
        for (int i = 0; i < 6; i++) {
            ord.append(random.nextInt(10));
        }
        StringBuilder mdh = new StringBuilder("MDH");
        for (int i = 0; i < 6; i++) {
            mdh.append(random.nextInt(10));
        }
        order.setIdOrder(ord.toString());
        order.setGiaTriChuaGiam(request.getTongTien());
        order.setTongTienThuHo(request.getTongTien());
        order.setMaDonHang(mdh.toString());
        order.setTrangThai(1);
        order.setNgayTao(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());

        PhieuGiamGia phieu = phieuGiamGiaService.findBestCouponForOrder(order.getGiaTriChuaGiam());
        BigDecimal soTienGiam = BigDecimal.ZERO;

        if (phieu != null) {
            soTienGiam = phieuGiamGiaService.calculateDiscount(phieu, order.getGiaTriChuaGiam());
            order.setGiaTriGiamGia(soTienGiam);
        }

        orderRepo.save(order);
        StringBuilder gg = new StringBuilder("GG");
        for (int i = 0; i < 6; i++) {
            gg.append(random.nextInt(10));
        }
        if (phieu != null) {
            GiamGiaHoaDon giam = new GiamGiaHoaDon();
            giam.setIdGiamgiahoadon(gg.toString());
            giam.setIdOrders(order);
            giam.setIdPhieuGiamGia(phieu);
            giam.setSoTienTruocGiam(soTienGiam);

            giamGiaHoaDonRepo.save(giam);
        }

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
                String numberStr = lastCode.substring(2, 5);
                next = Integer.parseInt(numberStr) + 1;
            } catch (NumberFormatException ignored) {
                next = 1;
            }
        }

        return String.format("OD%03d-2024", next);
    }




}
