package com.example.sever.service.impl;

import com.example.sever.dto.request.PhienBanAddRequestDTO;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PhienBanServiceImpl implements PhienBanService {

    private final PhienBanRepository phienbanRepository;
    private final RamRepository ramRepository;
    private final RomRepository romRepository;
    private final CpuRepository cpuRepository;
    private final DoHoaRepository doHoaRepository;
    private final MauSacRepository mauSacRepository;
    private final PhienBanMapper phienBanMapper;

    @Override
    public Page<PhienBanDisplayReponse> getAllPhienBanforDisplay(Pageable pageable) {
        Page<PhienBan> phienBanPage = phienbanRepository.findAll(pageable);
        List<PhienBanDisplayReponse> phienBanDisplayResponses = phienBanPage.getContent()
                .stream()
                .map(phienBanMapper::getAlldisplayPhienBan)
                .collect(Collectors.toList());

        return new PageImpl<>(phienBanDisplayResponses, pageable, phienBanPage.getTotalElements());
    }

    @Override
    public PhienBanDisplayReponse addPhienBan(PhienBanAddRequestDTO adddto) {
        PhienBan phienBan = new PhienBan();
        phienBan.setId(UUID.randomUUID());
        phienBan.setIdPhienBan(adddto.getIdPhienBan());
        phienBan.setGiaBan(adddto.getGiaBan());
        phienBan.setTrangThai(adddto.getTrangThai());
        phienBan.setMoTa(adddto.getMoTa());

        // Lấy entity liên kết từ DB
        Ram ram = ramRepository.findById(adddto.getRam().getId())
                .orElseThrow(() -> new RuntimeException("RAM không tồn tại"));
        Rom rom = romRepository.findById(adddto.getRom().getId())
                .orElseThrow(() -> new RuntimeException("ROM không tồn tại"));
        Cpu cpu = cpuRepository.findById(adddto.getCpu().getId())
                .orElseThrow(() -> new RuntimeException("CPU không tồn tại"));
        DoHoa doHoa = doHoaRepository.findById(adddto.getDoHoa().getId())
                .orElseThrow(() -> new RuntimeException("Đồ họa không tồn tại"));
        MauSac mauSac = mauSacRepository.findById(adddto.getMauSac().getId())
                .orElseThrow(() -> new RuntimeException("Màu sắc không tồn tại"));

        // Gán vào phiên bản
        phienBan.setIdRam(ram);
        phienBan.setIdSsd(rom);
        phienBan.setIdCpu(cpu);
        phienBan.setIdDohoa(doHoa);
        phienBan.setIdMauSac(mauSac);

        // Lưu lại
        PhienBan saved = phienbanRepository.save(phienBan);
        return phienBanMapper.getAlldisplayPhienBan(saved);
}

    @Override
    public PhienBanDisplayReponse updatePhienBan(PhienBanUpdateRequestDTO dto) {
        PhienBan phienBan = phienbanRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên bản"));

        phienBan.setIdPhienBan(dto.getIdPhienBan());
        phienBan.setGiaBan(dto.getGiaBan());
        phienBan.setMoTa(dto.getMoTa());
        phienBan.setTrangThai(dto.getTrangThai());

        phienBan.setIdRam(ramRepository.findById(dto.getRam().getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy RAM")));
        phienBan.setIdSsd(romRepository.findById(dto.getRom().getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ROM")));
        phienBan.setIdCpu(cpuRepository.findById(dto.getCpu().getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy CPU")));
        phienBan.setIdDohoa(doHoaRepository.findById(dto.getDoHoa().getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Đồ hoạ")));
        phienBan.setIdMauSac(mauSacRepository.findById(dto.getMauSac().getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Màu sắc")));

        phienbanRepository.save(phienBan);

        return phienBanMapper.getAlldisplayPhienBan(phienBan);
    }


}