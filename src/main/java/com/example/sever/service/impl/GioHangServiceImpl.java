package com.example.sever.service.impl;

import com.example.sever.dto.GioHangDTO.AddProductRequest;
import com.example.sever.dto.GioHangDTO.UpdateQuantityProductRequest;
import com.example.sever.dto.request.AddCartRequestDTO;
import com.example.sever.dto.response.GioHang.CartResponse;
import com.example.sever.dto.response.GioHang.ProductCartResponse;
import com.example.sever.dto.response.GioHang.ProductInCart;
import com.example.sever.entity.GioHang;
import com.example.sever.entity.GioHangChiTiet;
import com.example.sever.entity.LaptopChiTiet;
import com.example.sever.entity.TaiKhoan;
import com.example.sever.repository.GioHangChiTietRepository;
import com.example.sever.repository.GioHangRepository;
import com.example.sever.repository.LaptopChiTietRepository;
import com.example.sever.repository.TaiKhoanRepository;
import com.example.sever.service.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service

public class GioHangServiceImpl implements GioHangService {
    @Autowired
    private GioHangRepository gioHangRepository;
    @Autowired
    private GioHangChiTietRepository gioHangChiTietRepository;
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired
    private LaptopChiTietRepository laptopChiTietRepository;

    @Override
    public CartResponse addProduct(AddProductRequest request) {
        Random random = new Random();
        int codeRamDom = 1000 + random.nextInt(9000);
        GioHang gioHang = gioHangRepository.findByIdTaiKhoan_Id(request.getIdTaiKhoan())
                .orElseGet(() -> {
                    System.out.println(">>> Tạo giỏ hàng mới");
                    GioHang gh = new GioHang();
                    gh.setId(UUID.randomUUID());
                    gh.setIdGioHang("GH" + codeRamDom);
                    gh.setNgayTao(Instant.now());
                    TaiKhoan tk = new TaiKhoan();
                    tk.setId(request.getIdTaiKhoan());
                    gh.setIdTaiKhoan(tk);
                    GioHang saved = gioHangRepository.save(gh);
                    System.out.println(">>> Đã lưu giỏ hàng ID: " + saved.getId());
                    return saved;
                });
        Optional<GioHangChiTiet> existing = gioHangChiTietRepository.findByIdGioHangAndIdSpct(
                gioHang, request.getIdSpct());

        if (existing.isPresent()) {
            throw new IllegalArgumentException("Sản phẩm đã có trong giỏ hàng");
        } else {
            System.out.println(">>> Tạo chi tiết giỏ hàng mới");
            GioHangChiTiet ct = new GioHangChiTiet();
            ct.setId(UUID.randomUUID());
            ct.setIdGioHang(gioHang);
            ct.setIdSpct(request.getIdSpct());
            ct.setSoLuong(request.getSoLuong());
            ct.setIsSelected(1);
            System.out.println("Chi tiết - ID: " + ct.getId());
            System.out.println("Chi tiết - idGioHang: " + ct.getIdGioHang().getId());
            System.out.println("Chi tiết - idSpct: " + ct.getIdSpct());
            System.out.println("Chi tiết - soLuong: " + ct.getSoLuong());

            try {
                GioHangChiTiet saved = gioHangChiTietRepository.save(ct);
                System.out.println(">>> ĐÃ LƯU CHI TIẾT THÀNH CÔNG: " + saved.getId());
            } catch (Exception e) {
                System.err.println(">>> LỖI KHI LƯU CHI TIẾT: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        }

        gioHangChiTietRepository.flush();

        System.out.println("=== LAY THONG TIN GIO HANG ===");
        return getProductUser(request.getIdTaiKhoan());
    }

    @Override
    public CartResponse getProductUser(UUID idUser) {
        GioHang gioHang = gioHangRepository.findByIdTaiKhoan_Id(idUser)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));

        List<GioHangChiTiet> chiTiet = gioHangChiTietRepository.findByIdGioHang(gioHang);

        List<ProductInCart> sanPhams = chiTiet.stream()
                .map(ct -> {
                    ProductCartResponse sp = laptopChiTietRepository.getLaptopDetail(ct.getIdSpct());
                    return ProductInCart.builder()
                            .idChiTiet(ct.getId())
                            .idSpct(ct.getIdSpct())
                            .soLuong(ct.getSoLuong())
                            .isSelected(ct.getIsSelected())
                            .productInfo(sp)
                            .build();
                })
                .collect(Collectors.toList());

        Integer tongSoLuong = chiTiet.stream()
                .mapToInt(GioHangChiTiet::getSoLuong)
                .sum();

        return CartResponse.builder()
                .idGioHang(gioHang.getId())
                .maGioHang(gioHang.getIdGioHang())
                .ngayTao(gioHang.getNgayTao())
                .sanPhams(sanPhams)
                .tongSoLuong(tongSoLuong)
                .build();
    }

    @Override
    public void updateQuantityProduct(UpdateQuantityProductRequest request) {
        GioHangChiTiet ct = gioHangChiTietRepository.findById(request.getIdGioHangCT())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));
        ct.setSoLuong(request.getQuantity());
        gioHangChiTietRepository.save(ct);
    }

    @Override
    public void deleteProductCart(UUID idCartDetail) {
        if (!gioHangChiTietRepository.existsById(idCartDetail)) {
            throw new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng");
        }
        gioHangChiTietRepository.deleteById(idCartDetail);
    }

    @Override
    public void updateStatus(UUID idCartDetail, Integer isSelected) {
        GioHangChiTiet ct = gioHangChiTietRepository.findById(idCartDetail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));
        ct.setIsSelected(isSelected);
        gioHangChiTietRepository.save(ct);
    }
}
