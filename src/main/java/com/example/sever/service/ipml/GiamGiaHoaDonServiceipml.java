package com.example.sever.service.ipml;

import com.example.sever.dto.GiamGiaHoaDonDTO.GiamGiaHoaDonRespone;
import com.example.sever.enity.GiamGiaHoaDon;
import com.example.sever.mapper.GiamGiaHoaDonMapper;
import com.example.sever.repository.GiamGiaHoaDonRepository;
import com.example.sever.repository.PhieuGiamGiaRepository;
import com.example.sever.service.GiamGiaHoaDonService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class GiamGiaHoaDonServiceipml implements GiamGiaHoaDonService {

    @Autowired
    private GiamGiaHoaDonRepository giamGiaHoaDonRepo;

    @Autowired
    private PhieuGiamGiaRepository phieuGiamGiaRepo;




    @Override
    public List<GiamGiaHoaDonRespone> getAllGiamGia() {

        List<GiamGiaHoaDon> ghd = giamGiaHoaDonRepo.findAll();

        return ghd.stream().map((giamGiaHoaDon) -> GiamGiaHoaDonMapper.mapToSale(giamGiaHoaDon)).collect(Collectors.toList());
    }





}
