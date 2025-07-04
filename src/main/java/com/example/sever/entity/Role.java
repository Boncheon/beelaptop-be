package com.example.sever.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "Role", schema = "dbo")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    UUID id;

    @Size(max = 20)
    @Column(name = "id_role", length = 20)
    String idRole;

    @Size(max = 100)
    @Nationalized
    @Column(name = "ten_chuc_vu", length = 100)
    String tenChucVu;

    @OneToMany(mappedBy = "idRole")
    @JsonIgnore
    Set<TaiKhoan> taiKhoans = new LinkedHashSet<>();
}