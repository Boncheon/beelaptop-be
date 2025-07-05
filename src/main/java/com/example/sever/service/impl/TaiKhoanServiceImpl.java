package com.example.sever.service.impl;

import com.example.sever.dto.request.TaiKhoanAddRequestDTO;
import com.example.sever.dto.request.TaiKhoanUpdateRequestDTO;
import com.example.sever.dto.response.TaiKhoanDisplayReponse;
import com.example.sever.entity.Role;
import com.example.sever.entity.TaiKhoan;
import com.example.sever.mapper.TaiKhoanMapper;
import com.example.sever.repository.RoleRepository;
import com.example.sever.repository.TaiKhoanRepository;
import com.example.sever.service.TaiKhoanService;
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
public class TaiKhoanServiceImpl implements TaiKhoanService {
    RoleRepository roleRepository;
    TaiKhoanRepository taikhoanRepository;
    TaiKhoanMapper taikhoanMapper;;

    @Override
    public Page<TaiKhoanDisplayReponse> getAllTaiKhoanforDisplay(Pageable pageable) {
        Page<TaiKhoan> tkPage = taikhoanRepository.findAll(pageable);
        List<TaiKhoanDisplayReponse> romDisplayReponses = tkPage.getContent().stream()
                .map(taikhoanMapper::getAlldisplayTaiKhoan).collect(Collectors.toList());

        return new PageImpl<>(romDisplayReponses , pageable, tkPage.getTotalElements());
    }

    @Override
    public TaiKhoanDisplayReponse addTaiKhoan(TaiKhoanAddRequestDTO updatedto) {

        TaiKhoan tk = new TaiKhoan();
        tk.setId(UUID.randomUUID());
        tk.setIdTaiKhoan(updatedto.getIdTaiKhoan());
        tk.setTen(updatedto.getTen());
        tk.setTrangThai(updatedto.getTrangThai());
        tk.setAnh(updatedto.getAnh());
        tk.setGioiTinh(updatedto.getGioiTinh());
        tk.setMatKhau(updatedto.getMatKhau());
        tk.setNgaySinh(updatedto.getNgaySinh());
        tk.setSoDienThoai(updatedto.getSoDienThoai());
        // Lấy entity liên kết từ DB
        Role role = roleRepository.findById(updatedto.getRole().getId())
                .orElseThrow(() -> new RuntimeException("RAM không tồn tại"));
        // Gán vào phiên bản
        tk.setIdRole(role);

        // Lưu lại
        TaiKhoan saved = taikhoanRepository.save(tk);
        return taikhoanMapper.getAlldisplayTaiKhoan(saved);
    }

    @Override
    public TaiKhoanDisplayReponse updateTaiKhoan(TaiKhoanUpdateRequestDTO updatedto) {
        TaiKhoan tk = taikhoanRepository.findById(updatedto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên bản"));
        tk.setIdTaiKhoan(updatedto.getIdTaiKhoan());
        tk.setTen(updatedto.getTen());
        tk.setTrangThai(updatedto.getTrangThai());
        tk.setAnh(updatedto.getAnh());
        tk.setGioiTinh(updatedto.getGioiTinh());
        tk.setMatKhau(updatedto.getMatKhau());
        tk.setNgaySinh(updatedto.getNgaySinh());
        tk.setSoDienThoai(updatedto.getSoDienThoai());
        // Lấy entity liên kết từ DB
        Role role = roleRepository.findById(updatedto.getRole().getId())
                .orElseThrow(() -> new RuntimeException("RAM không tồn tại"));
        // Gán vào phiên bản
        tk.setIdRole(role);

        // Lưu lại
        TaiKhoan saved = taikhoanRepository.save(tk);
        return taikhoanMapper.getAlldisplayTaiKhoan(saved);
    }


}
