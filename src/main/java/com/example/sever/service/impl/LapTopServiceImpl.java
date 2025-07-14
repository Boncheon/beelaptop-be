package com.example.sever.service.impl;


import com.example.sever.dto.request.LaptopAddRequestDTO;
import com.example.sever.dto.request.LaptopUpdateRequestDTO;
import com.example.sever.dto.request.PhienBanAddRequestDTO;
import com.example.sever.dto.request.SanPhamFullCreateDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.LapTopCTDisplayReponse;
import com.example.sever.dto.response.LapTopDisplayReponse;
import com.example.sever.dto.response.PhienBanDisplayReponse;
import com.example.sever.dto.response.RomDisplayReponse;
import com.example.sever.entity.Cpu;
import com.example.sever.entity.DoHoa;
import com.example.sever.entity.Laptop;
import com.example.sever.entity.LaptopChiTiet;
import com.example.sever.entity.MauSac;
import com.example.sever.entity.PhienBan;
import com.example.sever.entity.PhienbanLaptopct;
import com.example.sever.entity.Ram;
import com.example.sever.entity.Rom;
import com.example.sever.mapper.LapTopCTMapper;
import com.example.sever.mapper.LapTopMapper;
import com.example.sever.mapper.PhienBanMapper;
import com.example.sever.repository.CpuRepository;
import com.example.sever.repository.DoHoaRepository;
import com.example.sever.repository.HeDieuHanhRepository;
import com.example.sever.repository.KichThuocRepository;
import com.example.sever.repository.LapTopRepository;;
import com.example.sever.repository.LaptopChiTietRepository;
import com.example.sever.repository.ManHinhRepository;
import com.example.sever.repository.MauSacRepository;
import com.example.sever.repository.PhienBanRepository;
import com.example.sever.repository.PhienbanLaptopctRepository;
import com.example.sever.repository.PinRepository;
import com.example.sever.repository.RamRepository;
import com.example.sever.repository.RomRepository;
import com.example.sever.repository.SeriRepository;
import com.example.sever.service.LaptopService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LapTopServiceImpl implements LaptopService {

    private final LapTopRepository laptopRepository;
    private final LapTopMapper lapTopMapper;
    private final ManHinhRepository manHinhRepository;
    private final PinRepository pinRepository;
    private final KichThuocRepository kichThuocRepository;
    private final HeDieuHanhRepository heDieuHanhRepository;
    private final LaptopChiTietRepository laptopChiTietRepository;
    private final PhienBanRepository phienBanRepository;
    private final PhienbanLaptopctRepository phienbanLaptopctRepository;
    private final RamRepository ramRepository;
    private final RomRepository romRepository;
    private final CpuRepository cpuRepository;
    private final DoHoaRepository doHoaRepository;
    private final MauSacRepository mauSacRepository;
    private final LapTopCTMapper lapTopCTMapper;
    private final PhienBanMapper phienBanMapper;
    private final SeriRepository seriRepository;

    @Override
    public Page<LapTopDisplayReponse> getAllLapTopforDisplay(Pageable pageable) {
        Page<Laptop> LaptopPage = laptopRepository.findAll(pageable);

        // lấy dữ liệu số lượng từ repo
        Map<UUID, Long> soLuongMap = demSoLuongSeriTheoLaptop();

        List<LapTopDisplayReponse> lapTopDisplayReponses = LaptopPage.getContent().stream()
                .map(laptop -> {
                    LapTopDisplayReponse dto = lapTopMapper.getAlldisplayLapTop(laptop);
                    dto.setSoLuongTon(soLuongMap.getOrDefault(laptop.getId(), 0L)); // gán số lượng
                    return dto;
                }).collect(Collectors.toList());

        return new PageImpl<>(lapTopDisplayReponses, pageable, LaptopPage.getTotalElements());
    }


    @Override
    public Laptop addLapTop(LaptopAddRequestDTO adddto) {
        Laptop laptop = lapTopMapper.toDolaptop(adddto);
        return laptopRepository.save(laptop);
    }

    @Override
    public Laptop updateLapTop(LaptopUpdateRequestDTO updatedto) {
        return null;
    }

    @Override
    public List<LapTopCTDisplayReponse> getDetailedLapTop(UUID idLaptop) {
        // Lấy toàn bộ danh sách LaptopChiTiet theo id của Laptop
        List<LaptopChiTiet> chiTietList = laptopChiTietRepository.findByIdLapTop_Id(idLaptop);

        if (chiTietList.isEmpty()) {
            throw new RuntimeException("Không tìm thấy LaptopChiTiet nào cho laptop ID: " + idLaptop);
        }

        // Mapping sang danh sách DTO kết quả
        List<LapTopCTDisplayReponse> result = new ArrayList<>();

        for (LaptopChiTiet chiTiet : chiTietList) {
            LapTopCTDisplayReponse response = lapTopCTMapper.getAlldisplayLapTopCT(chiTiet);

            List<PhienbanLaptopct> mappings = phienbanLaptopctRepository.findByIdLaptopChiTiet_Id(chiTiet.getId());

            List<PhienBanDisplayReponse> danhSachPhienBan = mappings.stream()
                    .map(mapping -> phienBanMapper.getAlldisplayPhienBan(mapping.getIdPhienBan()))
                    .collect(Collectors.toList());

            response.setDanhSachPhienBan(danhSachPhienBan);

            result.add(response);
        }

        return result;
    }


    @Override
    public UUID createSanPhamHoanChinh(SanPhamFullCreateDTO dto) {
        Laptop laptop;
        UUID idLaptop;

        // ✅ Sửa lỗi dấu ngoặc tại đây
        Optional<Laptop> existingLaptopOpt = laptopRepository.findByTenSanPham(dto.getLaptop().getTenSanPham());

        if (existingLaptopOpt.isPresent()) {
            // Nếu laptop đã tồn tại → dùng lại
            laptop = existingLaptopOpt.get();
            idLaptop = laptop.getId();
        } else {
            // Tạo mới nếu chưa có
            laptop = lapTopMapper.toDolaptop(dto.getLaptop());
            idLaptop = UUID.randomUUID();
            laptop.setId(idLaptop);
            laptop.setIdLaptop("LAP" + idLaptop.toString().substring(0, 8));
            laptop.setNgayTao(Instant.now());

            laptopRepository.save(laptop);
        }

        // Luôn tạo mới LaptopChiTiet
        LaptopChiTiet chiTiet = new LaptopChiTiet();
        UUID idChiTiet = UUID.randomUUID();
        chiTiet.setId(idChiTiet);
        chiTiet.setIdLaptopChiTiet("LCT_" + idChiTiet.toString().substring(0, 8));
        chiTiet.setIdLapTop(laptop);
        chiTiet.setIdManHinh(manHinhRepository.findById(dto.getLaptopChiTiet().getIdManHinh()).orElseThrow());
        chiTiet.setIdPin(pinRepository.findById(dto.getLaptopChiTiet().getIdPin()).orElseThrow());
        chiTiet.setIdKichThuoc(kichThuocRepository.findById(dto.getLaptopChiTiet().getIdKichThuoc()).orElseThrow());
        chiTiet.setIdHeDieuHanh(heDieuHanhRepository.findById(dto.getLaptopChiTiet().getIdHeDieuHanh()).orElseThrow());
        chiTiet.setTrangThai(dto.getLaptopChiTiet().getTrangThai());
        chiTiet.setMoTa(dto.getLaptopChiTiet().getMoTa());
        chiTiet.setNguoiTao(dto.getNguoiTao());
        chiTiet.setNgayTao(Instant.now());
        chiTiet.setNgayCapNhat(Instant.now());

        laptopChiTietRepository.save(chiTiet);

        // Thêm danh sách phiên bản
        for (PhienBanAddRequestDTO item : dto.getCombinations()) {
            PhienBan pb = new PhienBan();
            pb.setId(UUID.randomUUID());
            pb.setIdPhienBan(item.getIdPhienBan());

            mapPhienBanFields(pb, item);
            phienBanRepository.save(pb);

            PhienbanLaptopct mapping = new PhienbanLaptopct();
            mapping.setId(UUID.randomUUID());
            mapping.setIdPhienBan(pb);
            mapping.setIdLaptopChiTiet(chiTiet);
            mapping.setIdPhienBanLaptopct("PBCT_" + UUID.randomUUID().toString().substring(0, 8));
            mapping.setTrangThai(item.getTrangThai());
            mapping.setNguoiTao(dto.getNguoiTao());
            mapping.setNgayTao(Instant.now());
            mapping.setNgayCapNhat(Instant.now());

            phienbanLaptopctRepository.save(mapping);
        }

        return idLaptop;
    }



    private Map<UUID, Long> demSoLuongSeriTheoLaptop() {
        List<Object[]> rawData = seriRepository.demSoLuongSeriTheoLaptop();
        Map<UUID, Long> result = new HashMap<>();
        for (Object[] row : rawData) {
            UUID laptopId = (UUID) row[0];
            Long soLuong = ((Number) row[1]).longValue();
            result.put(laptopId, soLuong);
        }
        return result;
    }


    private void mapPhienBanFields(PhienBan pb, PhienBanAddRequestDTO dto) {
        pb.setGiaBan(dto.getGiaBan());
        pb.setMoTa(dto.getMoTa());
        pb.setTrangThai(dto.getTrangThai());

        pb.setIdRam(getRam(dto.getRamId()));
        pb.setIdSsd(getRom(dto.getRomId()));
        pb.setIdCpu(getCpu(dto.getCpuId()));
        pb.setIdDohoa(getDoHoa(dto.getDoHoaId()));
        pb.setIdMauSac(getMauSac(dto.getMauSacId()));
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