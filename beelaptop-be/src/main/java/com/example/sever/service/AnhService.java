package com.example.sever.service;

import com.example.sever.dto.request.AnhAddRequestDTO;
import com.example.sever.dto.request.AnhUpdateRequestDTO;
import com.example.sever.dto.request.TaiKhoanAddRequestDTO;
import com.example.sever.dto.request.TaiKhoanUpdateRequestDTO;
import com.example.sever.dto.response.AnhDisplayReponse;
import com.example.sever.dto.response.TaiKhoanDisplayReponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;


public interface AnhService {
    AnhDisplayReponse uploadImageAndSave(AnhAddRequestDTO request) throws IOException;
    AnhDisplayReponse updateImage(UUID id, AnhUpdateRequestDTO request) throws IOException;

}
