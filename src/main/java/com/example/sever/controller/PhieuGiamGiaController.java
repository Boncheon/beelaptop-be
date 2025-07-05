package com.example.sever.controler;

import com.example.sever.dto.PhieuGiamGiaDTO.PhieuGiamGiaDto;
import com.example.sever.service.PhieuGiamGiaService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/phieu-giam-gia")
@PreAuthorize("hasRole('ADMIN')")
public class PhieuGiamGiaController {

    @Autowired
    private PhieuGiamGiaService phieuGiamGiaService;



    @GetMapping
    public ResponseEntity<List<PhieuGiamGiaDto>> getAll(){

        List<PhieuGiamGiaDto> ghd = phieuGiamGiaService.getAllVoucher();
        return ResponseEntity.ok(ghd);

    }


    @PostMapping
    public ResponseEntity<PhieuGiamGiaDto> add(@RequestBody PhieuGiamGiaDto phieuGiamGiaDto){

        PhieuGiamGiaDto addVC = phieuGiamGiaService.addVoucher(phieuGiamGiaDto);
        return new ResponseEntity<>(addVC, HttpStatus.CREATED);

    }


    @GetMapping("{id}")
    public ResponseEntity<PhieuGiamGiaDto> detail(@PathVariable("id") String voucherId){

        PhieuGiamGiaDto getAll = phieuGiamGiaService.detailVoucher(voucherId);
        return ResponseEntity.ok(getAll);

    }


    @PostMapping("{id}")
    public ResponseEntity<PhieuGiamGiaDto> update(@PathVariable("id") String voucherId,@RequestBody PhieuGiamGiaDto phieuGiamGiaDto){


        PhieuGiamGiaDto updatedto = phieuGiamGiaService.updateVoucher(voucherId,phieuGiamGiaDto);
        return ResponseEntity.ok(updatedto);

    }


    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String voucherId){

        phieuGiamGiaService.deleteVoucher(voucherId);
        return ResponseEntity.ok("Xoa thanh cong");

    }


    @GetMapping("/search")
    public ResponseEntity<List<PhieuGiamGiaDto>> searchVoucherId(@RequestParam("p") String keyword){

        List<PhieuGiamGiaDto> rs = phieuGiamGiaService.searchVoucherByIdOrTen(keyword);
        return ResponseEntity.ok(rs);

    }


    @GetMapping("/filter")
    public ResponseEntity<List<PhieuGiamGiaDto>> filterVouchers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) String sortBy
    ) {
        List<PhieuGiamGiaDto> result = phieuGiamGiaService.filterVoucher(keyword, startDate, endDate, trangThai, sortBy);
        return ResponseEntity.ok(result);
    }



    @GetMapping("/phan-trang")
    public ResponseEntity<Map<String, Object>> getVouchersByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<PhieuGiamGiaDto> pageResult = phieuGiamGiaService.getVouchersPaging(page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("vouchers", pageResult.getContent());
        response.put("currentPage", pageResult.getNumber());
        response.put("totalItems", pageResult.getTotalElements());
        response.put("totalPages", pageResult.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/check-ma")
    public ResponseEntity<Boolean> checkMaPhieuTrung(@RequestParam String ma) {
        boolean exists = phieuGiamGiaService.checkMaTrung(ma);
        return ResponseEntity.ok(exists);
    }


    @PutMapping("/ngung-hoat-dong/{id}")
    public ResponseEntity<?> ngungHoatDong(@PathVariable("id") String id) {
        try {
            PhieuGiamGiaDto dto = phieuGiamGiaService.doiTrangThaiNgungHoatDong(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy hoặc lỗi khác.");
        }
    }




}
