package com.example.sever.enity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

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

    @ColumnDefault("newid()")
    @Column(name = "ID_Role")
    private UUID idRole;

    @Size(max = 50)
    @Nationalized
    @Column(name = "Ten", length = 50)
    private String ten;

    @Size(max = 50)
    @Nationalized
    @Column(name = "Chuc_vu", length = 50)
    private String chucVu;

}