package com.example.sever.enity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "Role", schema = "dbo")
public class Role {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 20)
    @Column(name = "id_role", length = 20)
    private String idRole;

    @Size(max = 100)
    @Nationalized
    @Column(name = "ten_chuc_vu", length = 100)
    private String tenChucVu;

    @OneToMany(mappedBy = "idRole")
    private Set<TaiKhoan> taiKhoans = new LinkedHashSet<>();

}