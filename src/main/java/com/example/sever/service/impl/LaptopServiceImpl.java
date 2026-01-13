package com.example.sever.service.impl;

import com.example.sever.dto.request.LaptopAddRequestDTO;
import com.example.sever.dto.request.LaptopUpdateRequestDTO;
import com.example.sever.dto.response.CustomerLaptopProjection;
import com.example.sever.dto.response.LapTopDisplayReponse;
import com.example.sever.dto.response.LaptopResponseDTO;
import com.example.sever.dto.response.Search.BrandSearchResponse;
import com.example.sever.dto.response.Search.LaptopSearchBrandProjection;
import com.example.sever.entity.Laptop;
import com.example.sever.mapper.LapTopMapper;
import com.example.sever.mapper.LapTopCTMapper;
import com.example.sever.repository.LapTopRepository;
import com.example.sever.repository.LaptopChiTietRepository;
import com.example.sever.repository.SeriRepository;
import com.example.sever.repository.ThuongHieuRepository;
import com.example.sever.service.LaptopService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaptopServiceImpl implements LaptopService {

    private final LapTopRepository laptopRepository;
    private final LapTopMapper lapTopMapper;
    private final LapTopCTMapper lapTopCTMapper;
    private final LaptopChiTietRepository laptopChiTietRepository;
    private final SeriRepository seriRepository;

    private final ThuongHieuRepository thuongHieuRepository;

    /**
     * Lấy danh sách tất cả Laptop base (trang quản trị)
     */
    @Override
    @jakarta.transaction.Transactional(jakarta.transaction.Transactional.TxType.SUPPORTS)
    public Page<LapTopDisplayReponse> getAllLapTopForDisplay(Pageable pageable) {
        // Lấy page Laptop
        Page<Laptop> laptopPage = laptopRepository.findAllByOrderByNgayTaoDesc(pageable);

        // Map + gán thêm 2 số lượng
        List<LapTopDisplayReponse> responses = laptopPage.getContent()
                .stream()
                .map(lap -> {
                    // map các field cơ bản
                    LapTopDisplayReponse dto = lapTopMapper.getAlldisplayLapTop(lap);

                    // 1️⃣ số lượng biến thể (LaptopChiTiet) của laptop này
                    long soBienThe = laptopChiTietRepository.countByIdLaptop_Id(lap.getId());

                    // 2️⃣ tổng số seri của TẤT CẢ biến thể thuộc laptop này
                    long tongSeri = seriRepository.countSeriByLaptop(lap.getId());

                    dto.setSoLuongBienThe(soBienThe);
                    dto.setTongSoLuongSeri(tongSeri);      // nếu bạn dùng tên khác thì set field tương ứng

                    return dto;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, laptopPage.getTotalElements());
    }



    /**
     * Tạo mới Laptop (bước 1 - base)
     */
    @Override
    @Transactional
    public LaptopResponseDTO addLaptop(LaptopAddRequestDTO dto) {
        Laptop laptop = lapTopMapper.toEntity(dto);

        // ✅ Mặc định trạng thái = 1 nếu chưa set
        if (laptop.getTrangThai() == null) {
            laptop.setTrangThai(1);
        }

        if (laptop.getId() == null) {
            laptop.setId(UUID.randomUUID());
        }

        if (laptop.getIdLaptop() != null && !laptop.getIdLaptop().isBlank()) {
            String provided = laptop.getIdLaptop().trim().toUpperCase();
            if (laptopRepository.existsByIdLaptop(provided)) {
                throw new IllegalArgumentException("Mã idLaptop đã tồn tại: " + provided);
            }
            laptop.setIdLaptop(provided);
        } else {
            String sku;
            do {
                sku = "LAP_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            } while (laptopRepository.existsByIdLaptop(sku));
            laptop.setIdLaptop(sku);
        }

        laptop.setNgayTao(Instant.now());
        laptop.setNgaySua(Instant.now());

        laptopRepository.save(laptop);
        return lapTopMapper.toResponse(laptop);
    }



    /**
     * Cập nhật Laptop base
     */
    @Override
    @Transactional
    public LaptopResponseDTO updateLaptop(UUID id, LaptopUpdateRequestDTO dto) {
        Laptop laptop = laptopRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy Laptop với ID: " + id));

        lapTopMapper.updateEntity(laptop, dto);
        laptop.setNgaySua(Instant.now());
        laptopRepository.save(laptop);

        return lapTopMapper.toResponse(laptop);
    }

    @Override
    public LapTopDisplayReponse getDetailedLapTop(UUID id) {
        Laptop lt = laptopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy do hoa với ID: " + id));
        return lapTopMapper.getAlldisplayLapTop(lt);
    }

///------------------/////////
@Override
public List<CustomerLaptopProjection> getCustomerLaptop() {
    return laptopRepository.getLaptopsForHome();
}

    @Override
    public List<CustomerLaptopProjection> getLatestLaptops() {
        return laptopRepository.getLatestLaptops();
    }

    @Override
    public List<LaptopSearchBrandProjection> getSearchBrand(UUID idBrand) {
        return laptopRepository.findAllByBrandId(idBrand);
    }

    @Override
    public List<BrandSearchResponse> getAllBrand() {
        return thuongHieuRepository.listBrandSearch();
    }

    //Update code huy 05.01
    @Override
    public Integer getAllStatusIs1Laptop(UUID idLaptopChiTiet) {
        return seriRepository.countActiveSeri(idLaptopChiTiet);
    }
}
