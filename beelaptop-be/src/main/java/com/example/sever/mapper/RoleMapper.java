package com.example.sever.mapper;

import com.example.sever.dto.request.RoleAddRequestDTO;
import com.example.sever.dto.request.RoleUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.RoleDisplayReponse;
import com.example.sever.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role toRole(RoleAddRequestDTO request);

    RoleDisplayReponse getAlldisplayRole(Role role);

    void updateRole(@MappingTarget Role role, RoleUpdateRequestDTO request);

    void updateStatusRole(@MappingTarget Role role, StatusRequestDTO request);
}
