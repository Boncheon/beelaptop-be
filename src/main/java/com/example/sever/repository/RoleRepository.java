package com.example.sever.repository;

import com.example.sever.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByIdRole(String idRole);
    Optional<Role> findByTenChucVu(String tenChucVu);
}