package com.example.sever.service.impl;

import com.example.sever.dto.request.LapTopCTAddRequestDTO;
import com.example.sever.dto.request.LapTopCTUpdateRequestDTO;
import com.example.sever.dto.response.LapTopCTDisplayReponse;
import com.example.sever.entity.Cpu;
import com.example.sever.entity.DoHoa;
import com.example.sever.entity.HeDieuHanh;
import com.example.sever.entity.KichThuoc;
import com.example.sever.entity.LaptopChiTiet;
import com.example.sever.entity.ManHinh;
import com.example.sever.entity.MauSac;
import com.example.sever.entity.Pin;
import com.example.sever.entity.Ram;
import com.example.sever.entity.Rom;
import com.example.sever.mapper.LapTopCTMapper;
import com.example.sever.repository.HeDieuHanhRepository;
import com.example.sever.repository.KichThuocRepository;
import com.example.sever.repository.LaptopChiTietRepository;
import com.example.sever.repository.ManHinhRepository;
import com.example.sever.repository.PinRepository;
import com.example.sever.service.LapTopCTService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LapTopCTServiceImpl implements LapTopCTService {

    private final LaptopChiTietRepository laptopChiTietRepository;
    private final ManHinhRepository manHinhRepository;
    private final PinRepository pinRepository;
    private final KichThuocRepository kichThuocRepository;
    private final HeDieuHanhRepository heDieuHanhRepository;
    private final LapTopCTMapper lapTopCTMapper;

    @Override
    public Page<LapTopCTDisplayReponse> getAllLapTopCTforDisplay(Pageable pageable) {
        Page<LaptopChiTiet> phienBanPage = laptopChiTietRepository.findAll(pageable);
        List<LapTopCTDisplayReponse> phienBanDisplayResponses = phienBanPage.getContent()
                .stream()
                .map(lapTopCTMapper::getAlldisplayLapTopCT)
                .collect(Collectors.toList());

        return new PageImpl<>(phienBanDisplayResponses, pageable, phienBanPage.getTotalElements());
    }

    @Override
    public LapTopCTDisplayReponse addLapTopCT(LapTopCTAddRequestDTO dto) {
        LaptopChiTiet ltct = new LaptopChiTiet();
        ltct.setId(UUID.randomUUID());
        ltct.setIdLaptopChiTiet(dto.getIdLaptopChiTiet());
        ltct.setMoTa(dto.getMoTa());
        ltct.setNgayTao(LocalDateTime.now());
        ltct.setNgayCapNhat(LocalDateTime.now());
        ltct.setNguoiTao(dto.getNguoiTao());
        ltct.setGhiChu(dto.getGhiChu());
        ltct.setTrangThai(dto.getTrangThai());
        ltct.setMoTa(dto.getMoTa());

        // Lấy entity liên kết từ DB bằng id
        ManHinh manHinh = manHinhRepository.findById(dto.getIdManHinh())
                .orElseThrow(() -> new RuntimeException("ManHinh không tồn tại"));
        Pin pin = pinRepository.findById(dto.getIdPin())
                .orElseThrow(() -> new RuntimeException("ROM không tồn tại"));
        KichThuoc kichThuoc = kichThuocRepository.findById(dto.getIdKichThuoc())
                .orElseThrow(() -> new RuntimeException("CPU không tồn tại"));
        HeDieuHanh heDieuHanh = heDieuHanhRepository.findById(dto.getIdHeDieuHanh())
                .orElseThrow(() -> new RuntimeException("Đồ họa không tồn tại"));
        

        // Gán vào phiên bản
        ltct.setIdManHinh(manHinh);
        ltct.setIdPin(pin);
        ltct.setIdKichThuoc(kichThuoc);
        ltct.setIdHeDieuHanh(heDieuHanh);

        // Lưu lại
        LaptopChiTiet saved = laptopChiTietRepository.save(ltct);
        return lapTopCTMapper.getAlldisplayLapTopCT(saved);
    }

    @Override
    public LapTopCTDisplayReponse updateLapTopCT(LapTopCTUpdateRequestDTO dto) {
        LaptopChiTiet ltct = laptopChiTietRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy  lap top chi tiet"));

        ltct.setIdLaptopChiTiet(dto.getIdLaptopChiTiet());
        ltct.setMoTa(dto.getMoTa());
        ltct.setNgayCapNhat(LocalDateTime.now());
        ltct.setNguoiTao(dto.getNguoiTao());
        ltct.setGhiChu(dto.getGhiChu());
        ltct.setTrangThai(dto.getTrangThai());
        ltct.setMoTa(dto.getMoTa());

        // Lấy entity liên kết từ DB bằng id
        ManHinh manHinh = manHinhRepository.findById(dto.getIdManHinh())
                .orElseThrow(() -> new RuntimeException("ManHinh không tồn tại"));
        Pin pin = pinRepository.findById(dto.getIdPin())
                .orElseThrow(() -> new RuntimeException("ROM không tồn tại"));
        KichThuoc kichThuoc = kichThuocRepository.findById(dto.getIdKichThuoc())
                .orElseThrow(() -> new RuntimeException("CPU không tồn tại"));
        HeDieuHanh heDieuHanh = heDieuHanhRepository.findById(dto.getIdHeDieuHanh())
                .orElseThrow(() -> new RuntimeException("Đồ họa không tồn tại"));


        // Gán vào phiên bản
        ltct.setIdManHinh(manHinh);
        ltct.setIdPin(pin);
        ltct.setIdKichThuoc(kichThuoc);
        ltct.setIdHeDieuHanh(heDieuHanh);

        // Lưu lại
        LaptopChiTiet saved = laptopChiTietRepository.save(ltct);
        return lapTopCTMapper.getAlldisplayLapTopCT(saved);
    }

    @Override
    public LapTopCTDisplayReponse getById(UUID id) {
        LaptopChiTiet entity = laptopChiTietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy LaptopChiTiet với ID: " + id));

        return lapTopCTMapper.getAlldisplayLapTopCT(entity);
    }
}