package com.example.sever.service;

import com.example.sever.dto.request.RoleAddRequestDTO;
import com.example.sever.dto.request.RoleUpdateRequestDTO;
import com.example.sever.dto.response.RoleDisplayReponse;
import com.example.sever.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    Page<RoleDisplayReponse> getAllRoleforDisplay(Pageable pageable);
    Role addRole(RoleAddRequestDTO adddto);
    Role updateRole(RoleUpdateRequestDTO updatedto);
}
