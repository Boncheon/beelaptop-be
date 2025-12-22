package com.example.sever.service.impl;

import com.example.sever.dto.request.LapTopCTAddRequestDTO;
import com.example.sever.dto.request.LapTopCTAutoGenRequestDTO;
import com.example.sever.dto.request.LapTopCTUpdateRequestDTO;
import com.example.sever.dto.response.LaptopChiTietResponseDTO;
import com.example.sever.entity.*;
import com.example.sever.mapper.LapTopCTMapper;
import com.example.sever.repository.*;


import com.example.sever.service.LapTopCTService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LapTopCTServiceImpl implements LapTopCTService {

    private final LaptopChiTietRepository laptopChiTietRepo;

    private final RamRepository ramRepo;
    private final RomRepository ssdRepo;
    private final CpuRepository cpuRepo;
    private final DoHoaRepository doHoaRepo;
    private final MauSacRepository mauSacRepo;

    private final LapTopCTMapper mapper;
    private final LapTopRepository lapTopRepository;
    private final LaptopChiTietRepository laptopChiTietRepository;

    // ====================== LIST ======================
    @Override
    public Page<LaptopChiTietResponseDTO> getAll(Pageable pageable) {
        return laptopChiTietRepository.findAllWithSeri(pageable);
    }

    // ====================== LIST BY LAPTOP ======================
    @Override
    public List<LaptopChiTietResponseDTO> getByLaptop(UUID idLaptop) {
        return laptopChiTietRepository.findDtoByLaptopWithSeri(idLaptop);
    }

    // ====================== GET DETAIL ======================
    @Override
    public LaptopChiTietResponseDTO getById(UUID id) {
        LaptopChiTiet entity = laptopChiTietRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể laptop"));

        return mapper.toResponse(entity);
    }

    // ====================== ADD ======================
    @Override
    public LaptopChiTietResponseDTO add(UUID idLaptop, LapTopCTAddRequestDTO dto) {

        Laptop laptop = lapTopRepository.findById(idLaptop)
                .orElseThrow(() -> new RuntimeException("Laptop không tồn tại"));

        LaptopChiTiet entity = mapper.toEntity(dto);
        entity.setIdLaptop(laptop);
        entity.setNgayTao(Instant.now());
        entity.setNgayCapNhat(Instant.now());

        LaptopChiTiet saved = laptopChiTietRepo.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    public List<LaptopChiTietResponseDTO> autoGenVariants(LapTopCTAutoGenRequestDTO req) {

        if (req.getIdLaptop() == null) {
            throw new IllegalArgumentException("idLaptop không được null");
        }
        if (isEmpty(req.getIdRams()) ||
                isEmpty(req.getIdSsds()) ||
                isEmpty(req.getIdCpus()) ||
                isEmpty(req.getIdDohoas()) ||
                isEmpty(req.getIdMauSacs())) {

            throw new IllegalArgumentException(
                    "Danh sách RAM/SSD/CPU/Đồ hoạ/Màu sắc không được rỗng"
            );
        }

        Laptop laptop = lapTopRepository.findById(req.getIdLaptop())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Laptop cha"));

        List<LaptopChiTiet> entities = new ArrayList<>();

        // prefix dùng để nhìn cho dễ, nếu không truyền thì tự tạo
        String baseCode = (req.getIdLaptopCT() != null && !req.getIdLaptopCT().isBlank())
                ? req.getIdLaptopCT()
                : "LAP-" + req.getIdLaptop().toString().substring(0, 8);

        for (UUID ramId : req.getIdRams()) {
            for (UUID ssdId : req.getIdSsds()) {
                for (UUID cpuId : req.getIdCpus()) {
                    for (UUID vgaId : req.getIdDohoas()) {
                        for (UUID colorId : req.getIdMauSacs()) {

                            // 🔍 1) CHECK TRÙNG BIẾN THỂ
                            boolean existed = laptopChiTietRepo.existsVariant(
                                    laptop.getId(),
                                    ramId,
                                    ssdId,
                                    cpuId,
                                    vgaId,
                                    colorId
                            );
                            if (existed) {
                                // đã có biến thể này rồi => bỏ qua, không tạo nữa
                                continue;
                            }

                            // 🔥 2) Sinh mã biến thể mới
                            String variantCode = baseCode + "-" +
                                    UUID.randomUUID().toString().substring(0, 8);

                            LapTopCTAddRequestDTO dto = LapTopCTAddRequestDTO.builder()
                                    .idLaptopCT(variantCode)
                                    .idRam(ramId)
                                    .idSsd(ssdId)
                                    .idCpu(cpuId)
                                    .idDohoa(vgaId)
                                    .idMauSac(colorId)
                                    .giaBan(req.getGiaBan())
                                    .moTa(req.getMoTa())
                                    .trangThai(req.getTrangThai())
                                    .ghiChu(req.getGhiChu())
                                    .build();

                            LaptopChiTiet entity = mapper.toEntity(dto);
                            entity.setIdLaptop(laptop);
                            entity.setNgayTao(Instant.now());
                            entity.setNgayCapNhat(Instant.now());

                            entities.add(entity);
                        }
                    }
                }
            }
        }

        // nếu tất cả tổ hợp đều trùng thì trả về list rỗng
        if (entities.isEmpty()) {
            return List.of();
        }

        List<LaptopChiTiet> saved = laptopChiTietRepo.saveAll(entities);

        // Nếu bạn muốn luôn có soLuongSeri = 0 khi mới tạo
        return saved.stream()
                .map(e -> new LaptopChiTietResponseDTO(e, 0L))
                .toList();
    }

    private boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    // ====================== UPDATE ======================
    @Override
    public LaptopChiTietResponseDTO update(UUID id, LapTopCTUpdateRequestDTO dto) {

        LaptopChiTiet entity = laptopChiTietRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể laptop"));

        // MapStruct tự xử lý field nào null thì bỏ qua
        mapper.updateEntity(entity, dto);
        entity.setNgayCapNhat(Instant.now());

        LaptopChiTiet saved = laptopChiTietRepo.save(entity);
        return mapper.toResponse(saved);
    }

    // ====================== UPDATE STATUS ======================
    @Override
    public LaptopChiTietResponseDTO updateStatus(UUID id, Integer trangThai) {

        LaptopChiTiet entity = laptopChiTietRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể laptop"));

        entity.setTrangThai(trangThai);
        entity.setNgayCapNhat(Instant.now());

        return mapper.toResponse(laptopChiTietRepo.save(entity));
    }
}
