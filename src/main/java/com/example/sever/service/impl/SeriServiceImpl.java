package com.example.sever.service.impl;

import com.example.sever.dto.request.SeriAddRequestDTO;
import com.example.sever.dto.request.SeriUpdateRequestDTO;
import com.example.sever.dto.response.SeriDisplayReponse;
import com.example.sever.entity.LaptopChiTiet;
import com.example.sever.entity.Seri;
import com.example.sever.mapper.SeriMapper;
import com.example.sever.repository.LaptopChiTietRepository;
import com.example.sever.repository.SeriRepository;
import com.example.sever.service.SeriService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SeriServiceImpl implements SeriService {

    private final SeriRepository seriRepository;
    private final LaptopChiTietRepository laptopChiTietRepository;
    private final SeriMapper seriMapper; // tạm vẫn giữ, nếu không dùng chỗ nào thì có thể xoá sau

    /**
     * Thêm nhiều seri cho một biến thể LaptopChiTiet
     */
    @Override
    @Transactional
    public void addListSeri(SeriAddRequestDTO dto) {
        // lấy biến thể
        LaptopChiTiet ct = laptopChiTietRepository.findById(dto.getIdLaptopCt())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể LaptopChiTiet"));

        List<Seri> entities = new ArrayList<>();

        if (dto.getList() != null) {
            dto.getList().forEach(item -> {   // item = SeriAddRequestDTO.SeriItemDTO
                if (item == null) return;

                String seriStr = item.getIdSeri();
                if (seriStr == null) return;

                seriStr = seriStr.trim();
                if (seriStr.isEmpty()) return;

                // tránh trùng seri
                if (seriRepository.existsByIdSeri(seriStr)) return;

                // tạo Seri mới
                Seri seri = new Seri();
                seri.setIdSeri(seriStr);
                seri.setIdLapTopCt(ct);

                // lấy trangThai từ body, nếu null thì default = 1 (ACTIVE)
                Integer tt = item.getTrangThai();
                if (tt == null) {
                    tt = 1;
                }
                seri.setTrangThai(tt);

                entities.add(seri);
            });
        }

        if (!entities.isEmpty()) {
            seriRepository.saveAll(entities);
        }

        // hiện tại KHÔNG cập nhật soLuongTon vì LaptopChiTiet chưa có field này
        // nếu sau này bạn thêm cột so_luong_ton:
        // long soLuong = seriRepository.countByIdLapTopCt_IdAndTrangThai(ct.getId(), 1);
        // ct.setSoLuongTon((int) soLuong);
        // laptopChiTietRepository.save(ct);
    }


    /**
     * Lấy toàn bộ seri của một biến thể
     */
    @Override
    @Transactional(readOnly = true)
    public List<SeriDisplayReponse> getByLaptopCt(UUID idLaptopCt) {
        List<Seri> list = seriRepository.findByIdLapTopCt_Id(idLaptopCt);

        // ⚠ Map tay để đảm bảo trangThai không bị null do mapper
        return list.stream()
                .map(s -> SeriDisplayReponse.builder()
                        .id(s.getId())
                        .idSeri(s.getIdSeri())
                        .trangThai(s.getTrangThai())
                        .build()
                )
                .collect(Collectors.toList());
    }
    /**
     * Sửa thông tin 1 Seri (mã seri + trạng thái)
     */
    @Override
    @Transactional
    public void updateSeri(SeriUpdateRequestDTO dto) {
        if (dto.getId() == null) {
            throw new RuntimeException("Thiếu id của Seri cần sửa");
        }

        // lấy Seri hiện có
        Seri seri = seriRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Seri với id = " + dto.getId()));

        // --- xử lý đổi mã seri (nếu có truyền lên) ---
        if (dto.getIdSeri() != null) {
            String newSeri = dto.getIdSeri().trim();
            if (newSeri.isEmpty()) {
                throw new RuntimeException("Mã seri không được để trống");
            }

            // nếu mã mới khác mã cũ thì kiểm tra trùng
            if (!newSeri.equals(seri.getIdSeri())
                    && seriRepository.existsByIdSeri(newSeri)) {
                throw new RuntimeException("Mã seri '" + newSeri + "' đã tồn tại");
            }

            seri.setIdSeri(newSeri);
        }

        // --- xử lý đổi trạng thái (nếu có truyền lên) ---
        if (dto.getTrangThai() != null) {
            seri.setTrangThai(dto.getTrangThai());   // ví dụ: 0 = INACTIVE, 1 = ACTIVE
        }

        // lưu lại
        seriRepository.save(seri);
    }


    @Override
    @Transactional(readOnly = true)
    public List<SeriDisplayReponse> getAll() {
        List<Seri> list = seriRepository.findAll();
        return seriMapper.toResponseList(list);
    }

    @Override
    @Transactional(readOnly = true)
    public SeriDisplayReponse getDetail(UUID id) {
        Seri seri = seriRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Seri với id = " + id));
        return seriMapper.toResponse(seri);
    }

    @Override
    @Transactional(readOnly = true)
    public SeriDisplayReponse findByIdSeri(String idSeri) {
        Seri seri = seriRepository.findByIdSeriAndTrangThai(idSeri, 1)  // Chỉ tìm nếu ACTIVE (1)
                .orElseThrow(() -> new RuntimeException("Seri không tồn tại hoặc không khả dụng"));
        return seriMapper.toResponse(seri);
    }

}
