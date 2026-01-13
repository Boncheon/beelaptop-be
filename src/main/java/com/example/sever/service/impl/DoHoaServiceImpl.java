package com.example.sever.service.impl;

import com.example.sever.dto.request.DoHoaAddRequestDTO;

import com.example.sever.dto.request.DoHoaUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;

import com.example.sever.dto.response.DoHoaDisplayReponse;
import com.example.sever.entity.DoHoa;
import com.example.sever.mapper.DoHoaMapper;
import com.example.sever.repository.DoHoaRepository;
import com.example.sever.service.DoHoaService;
import com.example.sever.specification.DoHoaSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DoHoaServiceImpl implements DoHoaService {
    DoHoaRepository doHoaRepository;
    DoHoaMapper doHoaMapper;

    @Override
    public Page<DoHoaDisplayReponse> getAllDoHoaforDisplayPage(Pageable pageable) {
       Page<DoHoa> doHoaPage = doHoaRepository.findAll(pageable);
       List<DoHoaDisplayReponse> doHoaDisplayReponses = doHoaPage.getContent().stream()
               .map(doHoaMapper::toDoHoaDisplayReponse).collect(Collectors.toList());

       return new PageImpl<>(doHoaDisplayReponses , pageable, doHoaPage.getTotalElements());
    }
    @Override
    public Page<DoHoaDisplayReponse> getTrangThaiCpuforDisplay(Pageable pageable) {
        Page<DoHoa> doHoaPage = doHoaRepository.findByTrangThai(1, pageable);

        List<DoHoaDisplayReponse> responses = doHoaPage.getContent()
                .stream()
                .map(doHoaMapper::toDoHoaDisplayReponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable,  doHoaPage.getTotalElements());
    }

    @Override
    public DoHoa addDohoa(DoHoaAddRequestDTO addrequestDTO) {
        // map từ DTO -> entity
        DoHoa doHoa = doHoaMapper.toDoHoa(addrequestDTO);

        // set mặc định trạng thái nếu null
        if (doHoa.getTrangThai() == null) {
            doHoa.setTrangThai(1); // 1 = Kinh doanh (active)
        }

        // set thời gian tạo & sửa
        Instant now = Instant.now();          // nếu field là Instant
        doHoa.setNgayTao(now);
        doHoa.setNgaySua(now);

        // lưu DB
        return doHoaRepository.save(doHoa);

    }

    @Override
    @Transactional
    public DoHoa updateDohoa(DoHoaUpdateRequestDTO dto) {

        // 1. Lấy entity cũ
        DoHoa existing = doHoaRepository.findById(dto.getId())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy Đồ Họa với ID: " + dto.getId()));

        // 2. Map các field từ DTO -> entity (trừ id)
        doHoaMapper.updateDoHoa(existing, dto);

        // 3. Ép set lại trạng thái (đảm bảo chắc chắn)
        if (dto.getTrangThai() != null) {
            existing.setTrangThai(dto.getTrangThai());
        }

        // 4. Cập nhật ngày sửa nếu bạn có cột này
        existing.setNgaySua(Instant.now());

        // 5. Lưu lại
        return doHoaRepository.save(existing);
    }

    @Override
    public DoHoa updateStatus(StatusRequestDTO updatedto) {
        DoHoa existing = doHoaRepository.findById(updatedto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Đồ Họa với ID: " + updatedto.getId()));
        //cap nhap stattus
        if(existing.getTrangThai()==0){
            updatedto.setTrangThai(1);
        }else {
            updatedto.setTrangThai(0);
        }
        // 2. Cập nhật dữ liệu từ DTO vào entity cũ
        doHoaMapper.updateStatusDoHoa(existing, updatedto);

        // 3. Lưu lại bản ghi đã cập nhật
        return doHoaRepository.save(existing);
    }

    @Override
    public Page<DoHoaDisplayReponse> getDoHoaByFilter(Integer trangThai, String keyword, Pageable pageable) {
        Specification<DoHoa> spec = DoHoaSpecification.filterByKeywordAndTrangThai(keyword, trangThai);
        Page<DoHoa> dohoaPage = doHoaRepository.findAll(spec, pageable);

        List<DoHoaDisplayReponse> result = dohoaPage.getContent().stream()
                .map(doHoaMapper::toDoHoaDisplayReponse)
                .collect(Collectors.toList());

        return new PageImpl<>(result, pageable, dohoaPage.getTotalElements());
    }

    @Override
    public DoHoaDisplayReponse getDetailedDoHoa(UUID id) {
        DoHoa dh = doHoaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy do hoa với ID: " + id));
        return doHoaMapper.toDoHoaDisplayReponse(dh); // hoặc dùng getDetailCpu nếu bạn cần thông tin chi tiết hơn
    }
}
