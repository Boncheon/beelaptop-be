package com.example.sever.service;

import com.example.sever.dto.AccountDTO.DiaChiCreateRequest;
import com.example.sever.dto.AccountDTO.DiaChiUpdateRequest;
import com.example.sever.dto.AccountDTO.UpdateProfileCustomerRequest;
import com.example.sever.dto.AccountDTO.UpdateProfileCustomerResponse;
import com.example.sever.dto.request.TaiKhoanAddRequestDTO;
import com.example.sever.dto.request.TaiKhoanUpdateRequestDTO;
import com.example.sever.dto.response.DiaChi.DiaChiProjection;
import com.example.sever.dto.response.DiaChi.DiaChiResponse;
import com.example.sever.dto.response.TaiKhoanDisplayReponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface TaiKhoanService {
    Page<TaiKhoanDisplayReponse> getAllTaiKhoanforDisplay(Pageable pageable);
    TaiKhoanDisplayReponse addTaiKhoan(TaiKhoanAddRequestDTO adddto);
    TaiKhoanDisplayReponse updateTaiKhoan(TaiKhoanUpdateRequestDTO updatedto);
    List<DiaChiProjection> findAllAdress(UUID id);
    DiaChiResponse createAddressCustomer(DiaChiCreateRequest request);
    DiaChiResponse updateAddressCustomer(UUID id , DiaChiUpdateRequest request);
    DiaChiResponse setAddressDefaultCustomer(UUID id);
    void deleteAddressCustomer(UUID id);
    UpdateProfileCustomerResponse updateProfileCustomer(UUID id, UpdateProfileCustomerRequest request);
}
