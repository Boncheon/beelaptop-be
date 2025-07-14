package com.example.sever.service.impl;

import com.example.sever.dto.request.SeriAddRequestDTO;
import com.example.sever.dto.request.SeriUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.SeriDisplayReponse;
import com.example.sever.entity.PhienBan;
import com.example.sever.entity.Seri;
import com.example.sever.mapper.SeriMapper;
import com.example.sever.repository.PhienBanRepository;
import com.example.sever.repository.SeriRepository;
import com.example.sever.service.SeriService;
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
public class SeriServiceImpl implements SeriService {
    private final PhienBanRepository phienBanRepository;
    SeriRepository seriRepository;
    SeriMapper seriMapper;

    @Override
    public Page<SeriDisplayReponse> getAllSeriforDisplay(Pageable pageable) {
        Page<Seri> SeriPage = seriRepository.findAll(pageable);
        List<SeriDisplayReponse> romDisplayReponses = SeriPage.getContent().stream()
                .map(seriMapper::getAlldisplaySeri).collect(Collectors.toList());

        return new PageImpl<>(romDisplayReponses , pageable, SeriPage.getTotalElements());
    }

    @Override
    public SeriDisplayReponse getDetailedSeri(UUID id) {
        Seri seri = seriRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy CPU với ID: " + id));
        return seriMapper.getAlldisplaySeri(seri); // hoặc dùng getDetailSeri nếu bạn cần thông tin chi tiết hơn
    }

    @Override
    public SeriDisplayReponse addSeri(SeriAddRequestDTO adddto) {
        Seri seri = seriMapper.toSeri(adddto);

        // lấy entity PhienBan từ id
        PhienBan phienBan = phienBanRepository.findById(adddto.getIdPhienBan())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên bản"));

        seri.setPhienBan(phienBan);

        Seri saved = seriRepository.save(seri);

        // 🔁 Chuyển đổi sang DTO để trả về
        return seriMapper.getAlldisplaySeri(saved);
    }



    @Override
    public Seri updateSeri(SeriUpdateRequestDTO updatedto) {
        Seri  existing = seriRepository.findById(updatedto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Đồ Họa với ID: " + updatedto.getId()));

        // 2. Cập nhật dữ liệu từ DTO vào entity cũ
        seriMapper.updateSeri(existing, updatedto);

        // 3. Lưu lại bản ghi đã cập nhật
        return seriRepository.save(existing);
    }

    @Override
    public Seri updateStatus(StatusRequestDTO updatedto) {
        Seri existing = seriRepository.findById(updatedto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Đồ Họa với ID: " + updatedto.getId()));
        //cap nhap stattus
        if(existing.getTrangThai()==0){
            updatedto.setTrangThai(1);
        }else {
            updatedto.setTrangThai(0);
        }
        // 2. Cập nhật dữ liệu từ DTO vào entity cũ
        seriMapper.updateStatusSeri(existing, updatedto);

        // 3. Lưu lại bản ghi đã cập nhật
        return seriRepository.save(existing);
    }

//    @Override
//    public Page<SeriDisplayReponse> getSeriByFilter(Integer trangThai, String keyword, Pageable pageable) {
//        Specification<Seri> spec = SeriSpecification.filterByKeywordAndTrangThai(keyword, trangThai);
//        Page<Seri> seriPage = seriRepository.findAll(spec, pageable);
//
//        List<SeriDisplayReponse> result = seriPage.getContent().stream()
//                .map(seriMapper::getAlldisplaySeri)
//                .collect(Collectors.toList());
//
//        return new PageImpl<>(result, pageable, seriPage.getTotalElements());
//    }
}