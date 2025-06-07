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
@Table(name = "DiaChi", schema = "dbo")
public class DiaChi {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Size(max = 100)
    @Nationalized
    @Column(name = "tinh", length = 100)
    private String tinh;

    @Size(max = 100)
    @Nationalized
    @Column(name = "huyen", length = 100)
    private String huyen;

    @Size(max = 100)
    @Nationalized
    @Column(name = "xa", length = 100)
    private String xa;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ghi_chu")
    private String ghiChu;

}