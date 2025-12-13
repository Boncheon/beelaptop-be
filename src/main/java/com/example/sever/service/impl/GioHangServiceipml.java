package com.example.sever.service.impl;

import com.example.sever.dto.GioHangDTO.AddProductRequest;
import com.example.sever.dto.GioHangDTO.UpdateQuantityProductRequest;
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
import com.example.sever.service.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GioHangServiceipml implements GioHangService {

    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private GioHangChiTietRepository gioHangChiTietRepository;

    @Autowired
    private LaptopChiTietRepository laptopChiTietRepository;

    // ✅ tạo mã CT unique, ngắn, không trùng
    private String genCartDetailCode() {
        return "CT" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    @Override
    @Transactional
    public CartResponse addProduct(AddProductRequest request) {

        GioHang gioHang = gioHangRepository.findByIdTaiKhoan_Id(request.getIdTaiKhoan())
                .orElseGet(() -> {
                    GioHang gh = new GioHang();

                    gh.setId(UUID.randomUUID()); // ✅ THÊM DÒNG NÀY (FIX LỖI IDENTIFIER)

                    gh.setIdGioHang("GH" + (1000 + new Random().nextInt(9000)));
                    gh.setNgayTao(Instant.now());

                    TaiKhoan tk = new TaiKhoan();
                    tk.setId(request.getIdTaiKhoan());
                    gh.setIdTaiKhoan(tk);

                    return gioHangRepository.save(gh);
                });

        // ✅ Check tồn tại cùng SPCT trong giỏ
        Optional<GioHangChiTiet> existing =
                gioHangChiTietRepository.findByIdGioHangAndIdSpct_Id(
                        gioHang, request.getIdSpct()
                );

        int qty = request.getSoLuong() == null || request.getSoLuong() <= 0 ? 1 : request.getSoLuong();

        if (existing.isPresent()) {
            GioHangChiTiet ctOld = existing.get();
            int oldQty = ctOld.getSoLuong() == null ? 0 : ctOld.getSoLuong();
            ctOld.setSoLuong(oldQty + qty);
            gioHangChiTietRepository.save(ctOld);
            return getProductUser(request.getIdTaiKhoan());
        }

        // ✅ Load entity LaptopChiTiet
        LaptopChiTiet spct = laptopChiTietRepository.findById(request.getIdSpct())
                .orElseThrow(() -> new RuntimeException("LaptopChiTiet không tồn tại"));

        // ✅ Tạo chi tiết giỏ
        GioHangChiTiet ct = new GioHangChiTiet();

        // ✅ CỰC QUAN TRỌNG: KHÔNG setId() vì GioHangChiTiet có @GeneratedValue/@UuidGenerator
        // ct.setId(UUID.randomUUID()); ❌ KHÔNG ĐƯỢC LÀM

        // ✅ Fix lỗi unique index UX_GioHangCT_id_gio_hang_ct (NULL -> duplicate)
        ct.setIdGiohangchitiet(genCartDetailCode());

        ct.setIdGioHang(gioHang);
        ct.setIdSpct(spct);
        ct.setSoLuong(qty);
        ct.setIsSelected(1);

        gioHangChiTietRepository.save(ct);

        return getProductUser(request.getIdTaiKhoan());
    }

    @Override
    public CartResponse getProductUser(UUID idUser) {

        GioHang gioHang = gioHangRepository.findByIdTaiKhoan_Id(idUser)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));

        List<GioHangChiTiet> chiTiet =
                gioHangChiTietRepository.findByIdGioHang(gioHang);

        List<ProductInCart> sanPhams = chiTiet.stream()
                .map(ct -> {
                    ProductCartResponse sp =
                            laptopChiTietRepository.getLaptopDetail(ct.getIdSpct().getId());

                    return ProductInCart.builder()
                            .idChiTiet(ct.getId())
                            .idSpct(ct.getIdSpct().getId())
                            .soLuong(ct.getSoLuong())
                            .isSelected(ct.getIsSelected())
                            .productInfo(sp)
                            .build();
                })
                .collect(Collectors.toList());

        int tongSoLuong = chiTiet.stream()
                .mapToInt(x -> x.getSoLuong() == null ? 0 : x.getSoLuong())
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
    @Transactional
    public void updateQuantityProduct(UpdateQuantityProductRequest request) {

        GioHangChiTiet ct = gioHangChiTietRepository.findById(request.getIdGioHangCT())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        ct.setSoLuong(request.getQuantity());
        gioHangChiTietRepository.save(ct);
    }

    @Override
    @Transactional
    public void deleteProductCart(UUID idCartDetail) {
        gioHangChiTietRepository.deleteById(idCartDetail);
    }

    @Override
    @Transactional
    public void updateStatus(UUID idCartDetail, Integer isSelected) {

        GioHangChiTiet ct = gioHangChiTietRepository.findById(idCartDetail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        ct.setIsSelected(isSelected);
        gioHangChiTietRepository.save(ct);
    }
}
