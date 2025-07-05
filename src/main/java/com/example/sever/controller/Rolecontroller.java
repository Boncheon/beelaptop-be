package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.RoleAddRequestDTO;
import com.example.sever.dto.request.RoleUpdateRequestDTO;
import com.example.sever.dto.response.RoleDisplayReponse;
import com.example.sever.entity.Role;
import com.example.sever.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin("*")
@RestController
@RequestMapping("/api/role")
@AllArgsConstructor
public class Rolecontroller {
    RoleService roleService;

    @GetMapping()
    public ResponseEntity<Page<RoleDisplayReponse>> getAllramforDisplay(@RequestParam(defaultValue = "1") int page,
                                                                        @RequestParam(defaultValue = "4") int size) {
        int perPage = page - 1;
        if (perPage < 0) perPage = 0;
        Pageable pageable = PageRequest.of(perPage, size);
        Page<RoleDisplayReponse> RolePage = roleService.getAllRoleforDisplay(pageable);
        return ResponseEntity.ok(RolePage);
    }

    @PostMapping("/them-role")
    public ApiResponse<Role> addRole(@RequestBody RoleAddRequestDTO RoleAddRequestDTO) {
        Role add = roleService.addRole(RoleAddRequestDTO);
        return ApiResponse.<Role>builder()
                .message("them thanh cong")
                .data(add)
                .build();
    }
    @PostMapping("/sua-role")
    public ApiResponse<Role> updateRole(@RequestBody RoleUpdateRequestDTO RoleUpdateRequestDTO) {
        Role  updated = roleService.updateRole(RoleUpdateRequestDTO);
        return ApiResponse.<Role>builder()
                .message("cap nhap thanh cong")
                .data(updated)
                .build();
    }
}
