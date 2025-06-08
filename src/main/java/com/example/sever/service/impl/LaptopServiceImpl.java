package com.example.sever.service.impl;

import com.example.sever.dto.response.LaptopDisplayResponseDTO;
import com.example.sever.enity.Laptop;
import com.example.sever.enity.LaptopChiTiet;
import com.example.sever.enity.ThuongHieu;
import com.example.sever.service.LaptopService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LaptopServiceImpl implements LaptopService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<LaptopDisplayResponseDTO> getAllLaptopsForDisplayPaged(Pageable pageable) {
        // Sửa lại truy vấn JPQL để tránh so sánh UUID với ThuongHieu
        String jpql = "SELECT l, lct, SUM(lct.soLuong), th FROM Laptop l " +
                "LEFT JOIN LaptopChiTiet lct ON lct.idLaptop = l " +
                "LEFT JOIN ThuongHieu th ON th.id = l.idThuongHieu.id " +
                "GROUP BY l, lct, th " +
                "ORDER BY l.ngayTao DESC";
        
        // JPQL để đếm tổng số bản ghi
        String countJpql = "SELECT COUNT(DISTINCT l) FROM Laptop l";
        
        // Thực hiện truy vấn đếm
        Query countQuery = entityManager.createQuery(countJpql);
        Long total = (Long) countQuery.getSingleResult();
        
        // Thực hiện truy vấn chính với phân trang
        Query query = entityManager.createQuery(jpql)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());
        
        List<Object[]> results = query.getResultList();
        List<LaptopDisplayResponseDTO> dtos = toLaptopDisplayResponseDTOList(results);
        
        // Đánh số thứ tự lại dựa trên trang hiện tại
        AtomicInteger counter = new AtomicInteger((int) pageable.getOffset() + 1);
        dtos.forEach(dto -> dto.setStt(counter.getAndIncrement()));
        
        return new PageImpl<>(dtos, pageable, total);
    }
    

    @Override
    public LaptopDisplayResponseDTO getLaptopByIdForDisplay(UUID id) {
        String jpql = "SELECT l, lct, SUM(lct.soLuong), th FROM Laptop l " +
                "LEFT JOIN LaptopChiTiet lct ON lct.idLaptop = l " +
                "LEFT JOIN ThuongHieu th ON th.id = l.idThuongHieu.id " +
                "WHERE l.id = :id " +
                "GROUP BY l, lct, th";
        
        Query query = entityManager.createQuery(jpql)
                .setParameter("id", id);
        
        try {
            Object[] result = (Object[]) query.getSingleResult();
            return createLaptopDisplayResponseDTO(result, 1);
        } catch (Exception e) {
            return null;
        }
    }


    private List<LaptopDisplayResponseDTO> toLaptopDisplayResponseDTOList(List<Object[]> results) {
        List<LaptopDisplayResponseDTO> dtos = new ArrayList<>();
        
        for (int i = 0; i < results.size(); i++) {
            Object[] result = results.get(i);
            dtos.add(createLaptopDisplayResponseDTO(result, i + 1));
        }
        
        return dtos;
    }

    private LaptopDisplayResponseDTO createLaptopDisplayResponseDTO(Object[] result, int stt) {
        Laptop laptop = (Laptop) result[0];
        LaptopChiTiet laptopChiTiet = result.length > 1 ? (LaptopChiTiet) result[1] : null;
        Long soLuong = result.length > 2 ? (Long) result[2] : 0L;
        ThuongHieu thuongHieu = result.length > 3 ? (ThuongHieu) result[3] : null;
        
        return LaptopDisplayResponseDTO.builder()
                .stt(stt)
                .id(laptop.getId())
                .tenSanPham(laptop.getTenSanPham())
                .thuongHieuTen(thuongHieu != null ? thuongHieu.getTen() : null)
                .soLuong(soLuong != null ? soLuong.intValue() : 0)
                .trangThai(laptopChiTiet != null ? laptopChiTiet.getTrangThai() : null)
                .build();
    }
    @Override
    public Page<LaptopDisplayResponseDTO> searchLaptopsByFilters(
            String keyword, Boolean trangThai, String thuongHieuTen, String danhMucTen, Pageable pageable) {

        boolean isUuidSearch = false;
        UUID uuid = null;
        try {
            uuid = UUID.fromString(keyword);
            isUuidSearch = true;
        } catch (Exception ignored) {}

        String baseSelect = "SELECT l, lct, SUM(lct.soLuong), th FROM Laptop l " +
                "LEFT JOIN LaptopChiTiet lct ON lct.idLaptop = l " +
                "LEFT JOIN ThuongHieu th ON th.id = l.idThuongHieu.id " +
                "LEFT JOIN DanhMuc dm ON dm.id = l.idDanhMuc.id ";

        StringBuilder where = new StringBuilder("WHERE LOWER(l.tenSanPham) LIKE :keyword ");
        if (isUuidSearch) {
            where.append(" OR l.id = :uuid ");
        }
        if (trangThai != null) {
            where.append(" AND lct.trangThai = :trangThai ");
        }
        if (thuongHieuTen != null && !thuongHieuTen.trim().isEmpty()) {
            where.append(" AND LOWER(th.ten) LIKE :thuongHieuTen ");
        }
        if (danhMucTen != null && !danhMucTen.trim().isEmpty()) {
            where.append(" AND LOWER(dm.ten) LIKE :danhMucTen ");
        }

        String groupOrder = " GROUP BY l, lct, th ORDER BY l.ngayTao DESC";
        String jpql = baseSelect + where + groupOrder;

        String countJpql = "SELECT COUNT(DISTINCT l) FROM Laptop l " +
                "LEFT JOIN LaptopChiTiet lct ON lct.idLaptop = l " +
                "LEFT JOIN ThuongHieu th ON th.id = l.idThuongHieu.id " +
                "LEFT JOIN DanhMuc dm ON dm.id = l.idDanhMuc.id " +
                where.toString();

        String search = "%" + keyword.toLowerCase() + "%";

        Query countQuery = entityManager.createQuery(countJpql).setParameter("keyword", search);
        Query query = entityManager.createQuery(jpql)
                .setParameter("keyword", search)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        if (isUuidSearch) {
            countQuery.setParameter("uuid", uuid);
            query.setParameter("uuid", uuid);
        }
        if (trangThai != null) {
            countQuery.setParameter("trangThai", trangThai);
            query.setParameter("trangThai", trangThai);
        }
        if (thuongHieuTen != null && !thuongHieuTen.trim().isEmpty()) {
            String value = "%" + thuongHieuTen.toLowerCase() + "%";
            countQuery.setParameter("thuongHieuTen", value);
            query.setParameter("thuongHieuTen", value);
        }
        if (danhMucTen != null && !danhMucTen.trim().isEmpty()) {
            String value = "%" + danhMucTen.toLowerCase() + "%";
            countQuery.setParameter("danhMucTen", value);
            query.setParameter("danhMucTen", value);
        }

        Long total = (Long) countQuery.getSingleResult();
        List<Object[]> results = query.getResultList();
        List<LaptopDisplayResponseDTO> dtos = toLaptopDisplayResponseDTOList(results);

        AtomicInteger counter = new AtomicInteger((int) pageable.getOffset() + 1);
        dtos.forEach(dto -> dto.setStt(counter.getAndIncrement()));

        return new PageImpl<>(dtos, pageable, total);
    }

}
