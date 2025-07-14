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
    public DoHoa addDohoa(DoHoaAddRequestDTO addrequestDTO) {
        DoHoa doHoa = doHoaMapper.toDoHoa(addrequestDTO);
        return doHoaRepository.save(doHoa);
    }

    @Override
    public DoHoa updateDohoa(DoHoaUpdateRequestDTO updateRequestDTO) {
        // 1. Lấy bản ghi hiện có từ DB
        DoHoa existing = doHoaRepository.findById(updateRequestDTO.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Đồ Họa với ID: " + updateRequestDTO.getId()));

        // 2. Cập nhật dữ liệu từ DTO vào entity cũ
        doHoaMapper.updateDoHoa(existing, updateRequestDTO);

        // 3. Lưu lại bản ghi đã cập nhật
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
