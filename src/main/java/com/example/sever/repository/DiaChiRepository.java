package com.example.sever.repository;

import com.example.sever.dto.response.DiaChi.DiaChiProjection;
import com.example.sever.entity.DiaChi;
import com.example.sever.entity.TaiKhoan;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DiaChiRepository extends JpaRepository<DiaChi, UUID> {
    Optional<DiaChi> findByIdTaiKhoan(TaiKhoan taiKhoan);

    @Query("SELECT MAX(CAST(SUBSTRING(d.idDiaChi, 3) AS int)) FROM DiaChi d WHERE d.idDiaChi LIKE 'DC%'")
    Integer findMaxDiaChiCode();
    @Query(value = """
                SELECT 
                    d.ID AS id,
                    d.id_dia_chi AS idDiaChi,
                    d.id_tai_khoan AS idTaiKhoan,
                    d.quoc_gia AS quocGia,
                    d.tinh_thanh AS tinhThanh,
                    d.quan_huyen AS quanHuyen,
                    d.phuong_xa AS phuongXa,
                    d.dia_chi_chi_tiet AS diaChiChiTiet,
                    d.mac_dinh AS macDinh ,
                    d.ho_ten AS hoTen , 
                    d.so_dien_thoai as soDienThoai
                FROM DiaChi d
                WHERE d.id_tai_khoan = :idTaiKhoan
                ORDER BY d.mac_dinh DESC
            """, nativeQuery = true)
    List<DiaChiProjection> findAllByTaiKhoanProjection(@Param("idTaiKhoan") UUID idTaiKhoan);

    @Modifying
    @Transactional
    @Query(value = """
                UPDATE DiaChi
                SET mac_dinh = 0
                WHERE id_tai_khoan = :idTaiKhoan
            """, nativeQuery = true)
    void clearDefault(UUID idTaiKhoan);

    void deleteById(UUID id);
}
