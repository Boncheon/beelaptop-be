package com.example.sever.service.impl;

import com.example.sever.dto.request.PhienBanAddRequestDTO;
import com.example.sever.dto.request.PhienBanBatchCreateDTO;
import com.example.sever.dto.request.PhienBanUpdateRequestDTO;
import com.example.sever.dto.response.PhienBanDisplayReponse;
import com.example.sever.entity.*;
import com.example.sever.mapper.PhienBanMapper;
import com.example.sever.repository.*;
import com.example.sever.service.PhienBanService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PhienBanServiceImpl implements PhienBanService {

    private final PhienBanRepository phienBanRepository;
    private final RamRepository ramRepository;
    private final RomRepository romRepository;
    private final CpuRepository cpuRepository;
    private final DoHoaRepository doHoaRepository;
    private final MauSacRepository mauSacRepository;
    private final PhienBanMapper phienBanMapper;
    private final LaptopChiTietRepository laptopChiTietRepository;
    private final PhienbanLaptopctRepository phienbanLaptopctRepository;
    private final SeriRepository seriRepository;

    @Override
    public Page<PhienBanDisplayReponse> getAllPhienBanforDisplay(Pageable pageable) {
        // 1. Lấy danh sách phiên bản có phân trang
        Page<PhienBan> pageData = phienBanRepository.findAll(pageable);
        List<PhienBan> danhSachPhienBan = pageData.getContent();

        // 2. Lấy map số lượng Seri tồn kho (theo UUID id của phiên bản)
        Map<UUID, Long> mapSoLuong = seriRepository.demSoLuongSeriTheoPhienBan().stream()
                .filter(row -> row[0] != null && row[1] != null)
                .collect(Collectors.toMap(
                        row -> UUID.fromString(row[0].toString()),
                        row -> ((Number) row[1]).longValue()
                ));

        // 3. Map sang DTO và gán số lượng tồn kho tương ứng
        List<PhienBanDisplayReponse> responses = danhSachPhienBan.stream()
                .map(phienBan -> {
                    PhienBanDisplayReponse dto = phienBanMapper.getAlldisplayPhienBan(phienBan);
                    dto.setSoLuongTonKho(mapSoLuong.getOrDefault(phienBan.getId(), 0L));
                    return dto;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, pageData.getTotalElements());
    }

    @Override
    public PhienBanDisplayReponse updatePhienBan(PhienBanUpdateRequestDTO dto) {
        PhienBan entity = phienBanRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên bản với ID: " + dto.getId()));
        mapCommonFields(entity, dto);
        PhienBan saved = phienBanRepository.save(entity);
        return phienBanMapper.getAlldisplayPhienBan(saved);
    }

    //các biên thể của laptop gen ra
    @Override
    public List<PhienBanDisplayReponse> generatePhienBanBienThe(PhienBanBatchCreateDTO dto) {
        List<PhienBanDisplayReponse> result = new ArrayList<>();

        LaptopChiTiet ct = laptopChiTietRepository.findById(dto.getIdLaptopChiTiet())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy LaptopChiTiet"));

        for (PhienBanAddRequestDTO item : dto.getCombinations()) {
            PhienBan pb = new PhienBan();
            pb.setId(UUID.randomUUID());
            pb.setIdPhienBan(item.getIdPhienBan());
            mapCommonFields(pb, item);
            phienBanRepository.save(pb);

            PhienbanLaptopct map = new PhienbanLaptopct();
            map.setId(UUID.randomUUID());
            map.setIdPhienBan(pb);
            map.setIdLaptopChiTiet(ct);
            map.setIdPhienBanLaptopct("PBCT_" + UUID.randomUUID().toString().substring(0, 8));
            map.setTrangThai(item.getTrangThai());
            map.setNgayTao(Instant.now());
            map.setNgayCapNhat(Instant.now());
            map.setNguoiTao(dto.getNguoiTao());
            phienbanLaptopctRepository.save(map);

            result.add(phienBanMapper.getAlldisplayPhienBan(pb));
        }

        return result;
    }

    private void mapCommonFields(PhienBan entity, PhienBanAddRequestDTO dto) {
        entity.setIdPhienBan(dto.getIdPhienBan());
        entity.setGiaBan(dto.getGiaBan());
        entity.setMoTa(dto.getMoTa());
        entity.setTrangThai(dto.getTrangThai());

        entity.setIdRam(getRam(dto.getRamId()));
        entity.setIdSsd(getRom(dto.getRomId()));
        entity.setIdCpu(getCpu(dto.getCpuId()));
        entity.setIdDohoa(getDoHoa(dto.getDoHoaId()));
        entity.setIdMauSac(getMauSac(dto.getMauSacId()));
    }
    private void mapCommonFields(PhienBan entity, PhienBanUpdateRequestDTO dto) {
        entity.setGiaBan(dto.getGiaBan());
        entity.setMoTa(dto.getMoTa());
        entity.setTrangThai(dto.getTrangThai());

        entity.setIdRam(getRam(dto.getRamId()));
        entity.setIdSsd(getRom(dto.getRomId()));
        entity.setIdCpu(getCpu(dto.getCpuId()));
        entity.setIdDohoa(getDoHoa(dto.getDoHoaId()));
        entity.setIdMauSac(getMauSac(dto.getMauSacId()));
    }




    private Ram getRam(UUID id) {
        return ramRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy RAM với ID: " + id));
    }

    private Rom getRom(UUID id) {
        return romRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ROM với ID: " + id));
    }

    private Cpu getCpu(UUID id) {
        return cpuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy CPU với ID: " + id));
    }

    private DoHoa getDoHoa(UUID id) {
        return doHoaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Đồ họa với ID: " + id));
    }

    private MauSac getMauSac(UUID id) {
        return mauSacRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Màu sắc với ID: " + id));
    }
}
