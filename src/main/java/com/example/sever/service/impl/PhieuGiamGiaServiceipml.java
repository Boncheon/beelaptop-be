package com.example.sever.service.impl;

import com.example.sever.KieuGiamGia;
import com.example.sever.TrangThaiVoucher;
import com.example.sever.dto.PhieuGiamGiaDTO.PhieuGiamGiaDto;
import com.example.sever.entity.PhieuGiamGia;
import com.example.sever.exception.ResourceNotFoundException;
import com.example.sever.mapper.PhieuGiamGiaMapper;
import com.example.sever.repository.PhieuGiamGiaRepository;
import com.example.sever.service.PhieuGiamGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhieuGiamGiaServiceipml implements PhieuGiamGiaService {

    @Autowired
    private PhieuGiamGiaRepository phieuGiamGiaRepo;




    @Override
    public List<PhieuGiamGiaDto> getAllVoucher() {

        capNhatTrangThaiTuDong();

        List<PhieuGiamGia> pg = phieuGiamGiaRepo.findAll();

        return pg.stream().map((phieuGiamGia) -> PhieuGiamGiaMapper.mapTopVoucher(phieuGiamGia)).collect(Collectors.toList());
    }


    @Override
    public PhieuGiamGiaDto addVoucher(PhieuGiamGiaDto phieuGiamGiaDto) {

        PhieuGiamGia phieuGiamGia = PhieuGiamGiaMapper.mapTopVoucher(phieuGiamGiaDto);

        int trangThai = tinhTrangThai(phieuGiamGia.getNgayBatDau(), phieuGiamGia.getNgayKetThuc());
        phieuGiamGia.setTrangThai(trangThai);

        PhieuGiamGia addpg = phieuGiamGiaRepo.save(phieuGiamGia);

        return PhieuGiamGiaMapper.mapTopVoucher(addpg);

    }


    @Override
    public PhieuGiamGiaDto detailVoucher(String voucherId) {

        PhieuGiamGia phieuGiamGia = phieuGiamGiaRepo.findByIdPhieugiamgia(voucherId).orElseThrow(()-> new ResourceNotFoundException("Voucher khong ton tai:" + voucherId));

        return PhieuGiamGiaMapper.mapTopVoucher(phieuGiamGia);
    }



    @Override
    public PhieuGiamGiaDto updateVoucher(String voucherId, PhieuGiamGiaDto phieuGiamGiaDto) {

        PhieuGiamGia phieuGiamGia = phieuGiamGiaRepo.findByIdPhieugiamgia(voucherId).orElseThrow(()-> new ResourceNotFoundException("Voucher khong ton tai:" + voucherId));

        phieuGiamGia.setIdPhieugiamgia(phieuGiamGiaDto.getIdPhieugiamgia());
        phieuGiamGia.setTen(phieuGiamGiaDto.getTen());
        phieuGiamGia.setSoLuong(phieuGiamGiaDto.getSoLuong());
        phieuGiamGia.setKieuGiamGia(KieuGiamGia.valueOf(phieuGiamGiaDto.getKieuGiamGia()));
        phieuGiamGia.setGiaTriGiam(phieuGiamGiaDto.getGiaTriGiam());
        phieuGiamGia.setNgayBatDau(phieuGiamGiaDto.getNgayBatDau());
        phieuGiamGia.setNgayKetThuc(phieuGiamGiaDto.getNgayKetThuc());
        phieuGiamGia.setGiaTriMin(phieuGiamGiaDto.getGiaTriMin());
        phieuGiamGia.setGiaTriMax(phieuGiamGiaDto.getGiaTriMax());
        phieuGiamGia.setMoTa(phieuGiamGiaDto.getMoTa());

        if (phieuGiamGiaDto.getTrangThai() != null) {
            phieuGiamGia.setTrangThai(phieuGiamGiaDto.getTrangThai());
        }



        PhieuGiamGia update =phieuGiamGiaRepo.save(phieuGiamGia);

        return PhieuGiamGiaMapper.mapTopVoucher(update);
    }


    @Override
    public void deleteVoucher(String voucherId) {

        PhieuGiamGia phieuGiamGia = phieuGiamGiaRepo.findByIdPhieugiamgia(voucherId).orElseThrow(()-> new ResourceNotFoundException("Voucher khong ton tai:" + voucherId));
        phieuGiamGiaRepo.delete(phieuGiamGia);

    }

    @Override
    public List<PhieuGiamGiaDto> searchVoucherByIdOrTen(String keyword) {

            List<PhieuGiamGia> res = phieuGiamGiaRepo.findByIdPhieugiamgiaContainingIgnoreCaseOrTenContainingIgnoreCase(keyword,keyword);
            return res.stream().map(PhieuGiamGiaMapper::mapTopVoucher).collect(Collectors.toList());

    }

    @Override
    public List<PhieuGiamGiaDto> filterVoucher(String keyword, LocalDate startDate, LocalDate endDate, Integer trangThai, String sortBy) {

        return phieuGiamGiaRepo.findAll().stream()
                .filter(v -> keyword == null || v.getIdPhieugiamgia().toLowerCase().contains(keyword.toLowerCase()) || v.getTen().toLowerCase().contains(keyword.toLowerCase()))
                .filter(v -> startDate == null || !v.getNgayBatDau().isBefore(startDate))
                .filter(v -> endDate == null || !v.getNgayKetThuc().isAfter(endDate))
                .filter(v -> trangThai == null || v.getTrangThai().equals(trangThai))
                .sorted(getComparator(sortBy))
                .map(PhieuGiamGiaMapper::mapTopVoucher)
                .collect(Collectors.toList());


    }




    // test


    @Override
    public PhieuGiamGia findBestCouponForOrder(BigDecimal total) {
        LocalDate today = LocalDate.now();

        return phieuGiamGiaRepo.findAllValidCoupons(today).stream()
                .filter(p -> p.getGiaTriMin() != null && total.compareTo(p.getGiaTriMin()) >= 0)
                .filter(p -> p.getGiaTriMax() != null && total.compareTo(p.getGiaTriMax()) <= 0)
                .max(Comparator.comparing(p -> calculateDiscount(p, total)))
                .orElse(null);
    }


    @Override
    public BigDecimal calculateDiscount(PhieuGiamGia coupon, BigDecimal total) {
        if (coupon == null || total == null) return BigDecimal.ZERO;

        // Kiểm tra điều kiện áp dụng theo đơn tối thiểu
        if (coupon.getGiaTriMin() != null &&
                total.compareTo(coupon.getGiaTriMin()) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = BigDecimal.ZERO;

        if (coupon.getKieuGiamGia() == KieuGiamGia.GIAM_PHAN_TRAM) {
            discount = total.multiply(coupon.getGiaTriGiam())
                    .divide(BigDecimal.valueOf(100));

            if (coupon.getGiaTriMax() != null &&
                    discount.compareTo(coupon.getGiaTriMax()) > 0) {
                discount = coupon.getGiaTriMax();
            }

        } else if (coupon.getKieuGiamGia() == KieuGiamGia.GIAM_CO_DINH) {
            discount = coupon.getGiaTriGiam();
        }

        return discount.min(total); // Không vượt quá tổng đơn
    }



    // auto updateStatus


    @Override
    public void capNhatTrangThaiTuDong() {

        LocalDate homNay = LocalDate.now();
        List<PhieuGiamGia> danhSach = phieuGiamGiaRepo.findAll();

        for (PhieuGiamGia pg : danhSach) {

            if (pg.getTrangThai() != null && pg.getTrangThai().equals(TrangThaiVoucher.NGUNG_HOAT_DONG.getValue())) {
                continue;
            }

            if (pg.getNgayBatDau() != null && pg.getNgayKetThuc() != null) {
                if (homNay.isBefore(pg.getNgayBatDau())) {
                    pg.setTrangThai(TrangThaiVoucher.CHUA_KICH_HOAT.getValue());
                } else if (!homNay.isAfter(pg.getNgayKetThuc())) {
                    pg.setTrangThai(TrangThaiVoucher.DANG_HOAT_DONG.getValue());
                } else {
                    pg.setTrangThai(TrangThaiVoucher.HET_HAN.getValue());
                }
            }
        }

        phieuGiamGiaRepo.saveAll(danhSach);

    }

    @Override
    public Page<PhieuGiamGiaDto> getVouchersPaging(int page, int size) {

        capNhatTrangThaiTuDong();

        // Đảm bảo page không âm
        int validPage = Math.max(page, 0);
        Pageable pageable = PageRequest.of(validPage, size, Sort.by("ngayBatDau").descending());
        Page<PhieuGiamGia> entityPage = phieuGiamGiaRepo.findAll(pageable);
        return entityPage.map(PhieuGiamGiaMapper::mapTopVoucher);


    }

    @Override
    public boolean checkMaTrung(String idPhieugiamgia) {
        return phieuGiamGiaRepo.existsByIdPhieugiamgia(idPhieugiamgia);
    }

    @Override
    public PhieuGiamGiaDto doiTrangThaiNgungHoatDong(String idPhieuGiamGia) {

        PhieuGiamGia phieu = phieuGiamGiaRepo.findByIdPhieugiamgia(idPhieuGiamGia)
                .orElseThrow(() -> new ResourceNotFoundException("Phiếu giảm giá không tồn tại: " + idPhieuGiamGia));

        if (phieu.getTrangThai() != TrangThaiVoucher.NGUNG_HOAT_DONG.getValue()) {
            phieu.setTrangThai(TrangThaiVoucher.NGUNG_HOAT_DONG.getValue());
            phieu = phieuGiamGiaRepo.save(phieu);
        }

        return PhieuGiamGiaMapper.mapTopVoucher(phieu);

    }



    private int tinhTrangThai(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        LocalDate today = LocalDate.now();

        if (today.isBefore(ngayBatDau)) {
            return 0; // Chưa kích hoạt
        } else if (!today.isAfter(ngayKetThuc)) {
            return 1; // Đang hoạt động
        } else {
            return 2; // Hết hạn
        }
    }


    private Comparator<PhieuGiamGia> getComparator(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) return (a, b) -> 0;

        String[] parts = sortBy.split("_");
        String field = parts[0];
        boolean isAsc = parts.length < 2 || parts[1].equalsIgnoreCase("asc");

        Comparator<PhieuGiamGia> comparator;

        switch (field) {
            case "ten":
                comparator = Comparator.comparing(PhieuGiamGia::getTen, String.CASE_INSENSITIVE_ORDER);
                break;
            case "ngayBatDau":
                comparator = Comparator.comparing(PhieuGiamGia::getNgayBatDau);
                break;
            case "ngayKetThuc":
                comparator = Comparator.comparing(PhieuGiamGia::getNgayKetThuc);
                break;
            case "trangThai":
                comparator = Comparator.comparing(PhieuGiamGia::getTrangThai);
                break;
            default:
                return (a, b) -> 0;
        }

        return isAsc ? comparator : comparator.reversed();
    }





}
