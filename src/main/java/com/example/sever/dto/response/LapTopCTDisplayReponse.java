package com.example.sever.dto.response;

import com.example.sever.entity.HeDieuHanh;
import com.example.sever.entity.KichThuoc;
import com.example.sever.entity.Laptop;
import com.example.sever.entity.ManHinh;
import com.example.sever.entity.Pin;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LapTopCTDisplayReponse {
     private UUID id;
     private UUID idLaptop;
     private String idLaptopChiTiet;
//     private Laptop idLapTop;
     private String manHinh;       // VD: "15.6 inch 144Hz - 1920x1080"
     private String pin;           // VD: "4-cell"
     private String kichThuoc;     // VD: "30.4 x 21.2 x 1.56 cm - 1.25kg"
     private String heDieuHanh;    // VD: "Windows 10"
     private String moTa;
     private Instant ngayTao;
     private Instant ngayCapNhat;
     private String nguoiTao;
     private String ghiChu;
     private Integer trangThai;
     private List<PhienBanDisplayReponse> danhSachPhienBan;
}
