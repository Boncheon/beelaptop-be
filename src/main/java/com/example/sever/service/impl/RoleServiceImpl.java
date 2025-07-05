package com.example.sever.service.impl;

import com.example.sever.dto.request.RoleAddRequestDTO;
import com.example.sever.dto.request.RoleUpdateRequestDTO;
import com.example.sever.dto.response.RoleDisplayReponse;
import com.example.sever.entity.Role;
import com.example.sever.mapper.RoleMapper;
import com.example.sever.repository.RoleRepository;
import com.example.sever.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;

    @Override
    public Page<RoleDisplayReponse> getAllRoleforDisplay(Pageable pageable) {
        Page<Role> RolePage = roleRepository.findAll(pageable);
        List<RoleDisplayReponse> romDisplayReponses = RolePage.getContent().stream()
                .map(roleMapper::getAlldisplayRole).collect(Collectors.toList());

        return new PageImpl<>(romDisplayReponses , pageable, RolePage.getTotalElements());
    }

    @Override
    public Role addRole(RoleAddRequestDTO adddto) {
        Role Role = roleMapper.toRole(adddto);
        return roleRepository.save(Role);
    }

    @Override
    public Role updateRole(RoleUpdateRequestDTO updatedto) {
        Role  existing = roleRepository.findById(updatedto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Đồ Họa với ID: " + updatedto.getId()));

        // 2. Cập nhật dữ liệu từ DTO vào entity cũ
        roleMapper.updateRole(existing, updatedto);

        // 3. Lưu lại bản ghi đã cập nhật
        return roleRepository.save(existing);
    }
}
