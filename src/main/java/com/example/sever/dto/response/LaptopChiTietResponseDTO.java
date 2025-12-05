package com.example.sever.dto.response;

import com.example.sever.entity.LaptopChiTiet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaptopChiTietResponseDTO {

    private UUID id;          // ID biến thể
    private UUID idLaptop;    // FK đến Laptop base
    private String idLaptopCT;

    // ===== ID cấu hình =====
    private UUID idRam;
    private UUID idSsd;
    private UUID idCpu;
    private UUID idDohoa;
    private UUID idMauSac;

    // ===== Tên hiển thị =====
    private String tenRam;
    private String tenSsd;
    private String tenCpu;
    private String tenDohoa;
    private String tenMauSac;

    private BigDecimal giaBan;
    private Long soLuongSeri;
    private String moTa;
    private Integer trangThai;

    private Instant ngayTao;
    private Instant ngayCapNhat;
    public LaptopChiTietResponseDTO(LaptopChiTiet l, Long soLuongSeri) {
        this.id = l.getId();
        this.idLaptop = l.getIdLaptop().getId();;
        this.idLaptopCT = l.getIdLaptopCT();

        // ID cấu hình
        this.idRam = l.getIdRam() != null ? l.getIdRam().getId() : null;
        this.idSsd = l.getIdSsd() != null ? l.getIdSsd().getId() : null;
        this.idCpu = l.getIdCpu() != null ? l.getIdCpu().getId() : null;
        this.idDohoa = l.getIdDohoa() != null ? l.getIdDohoa().getId() : null;
        this.idMauSac = l.getIdMauSac() != null ? l.getIdMauSac().getId() : null;

        // Tên hiển thị
        this.tenRam = l.getIdRam() != null ? l.getIdRam().getDungLuongRam() : null;
        this.tenSsd = l.getIdSsd() != null ? l.getIdSsd().getDungLuongSsd() : null;
        this.tenCpu = l.getIdCpu() != null ? l.getIdCpu().getTen() : null;
        this.tenDohoa = l.getIdDohoa() != null ? l.getIdDohoa().getTenDayDu() : null;
        this.tenMauSac = l.getIdMauSac() != null ? l.getIdMauSac().getTen() : null;

        this.giaBan = l.getGiaBan();
        this.soLuongSeri = soLuongSeri;
        this.moTa = l.getMoTa();
        this.trangThai = l.getTrangThai();

        // nếu entity cũng dùng Instant thì gán thẳng, nếu là LocalDateTime thì convert lại
        this.ngayTao = l.getNgayTao();
        this.ngayCapNhat = l.getNgayCapNhat();
    }
}
