package com.example.sever.controller.customer;

import com.example.sever.dto.AccountDTO.DiaChiCreateRequest;
import com.example.sever.dto.AccountDTO.DiaChiUpdateRequest;
import com.example.sever.dto.AccountDTO.UpdateProfileCustomerRequest;
import com.example.sever.dto.AccountDTO.UpdateProfileCustomerResponse;
import com.example.sever.dto.response.DiaChi.DiaChiProjection;
import com.example.sever.dto.response.DiaChi.DiaChiResponse;
import com.example.sever.service.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/laptops")
@CrossOrigin(origins = "*")
public class AccountCustomerController {
    @Autowired
    private TaiKhoanService taiKhoanService;
    
    @GetMapping("/get-all-address/{id}")
    public ResponseEntity<List<DiaChiProjection>> getAllAddress(@PathVariable UUID id) {
        List<DiaChiProjection> list = taiKhoanService.findAllAdress(id);
        return ResponseEntity.ok(list);
    }
    
    @PostMapping("/address/add")
    public ResponseEntity<DiaChiResponse> addAddressCustomer(@RequestBody DiaChiCreateRequest request) {
        return ResponseEntity.ok(taiKhoanService.createAddressCustomer(request));
    }
    
    @PutMapping("/address/update/{id}")
    public ResponseEntity<DiaChiResponse> updateAddressCustomer(
            @PathVariable UUID id,
            @RequestBody DiaChiUpdateRequest request) {

        return ResponseEntity.ok(taiKhoanService.updateAddressCustomer(id, request));
    }
    
    @PutMapping("/address/set-default/{id}")
    public ResponseEntity<DiaChiResponse> setDefault(@PathVariable UUID id) {
        return ResponseEntity.ok(taiKhoanService.setAddressDefaultCustomer(id));
    }
    
    @DeleteMapping("/address/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        taiKhoanService.deleteAddressCustomer(id);
        return ResponseEntity.ok("Đã xóa địa chỉ thành công!");
    }

    @PutMapping("/account/update/{id}")
    public ResponseEntity<UpdateProfileCustomerResponse> updateProfileCustomer(
            @PathVariable UUID id,
            @RequestBody UpdateProfileCustomerRequest request) {
        UpdateProfileCustomerResponse response = taiKhoanService.updateProfileCustomer(id, request);
        return ResponseEntity.ok(response);
    }
    
    // Endpoint riêng cho form-data (upload file)
    @PutMapping("/account/update-form/{id}")
    public ResponseEntity<UpdateProfileCustomerResponse> updateProfileCustomerWithFile(
            @PathVariable UUID id,
            @ModelAttribute UpdateProfileCustomerRequest request) {
        UpdateProfileCustomerResponse response = taiKhoanService.updateProfileCustomer(id, request);
        return ResponseEntity.ok(response);
    }

}