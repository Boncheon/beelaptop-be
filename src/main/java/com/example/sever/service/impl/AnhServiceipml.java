package com.example.sever.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.sever.dto.request.AnhAddRequestDTO;
import com.example.sever.dto.request.AnhUpdateRequestDTO;
import com.example.sever.dto.response.AnhDisplayReponse;
import com.example.sever.entity.Anh;
import com.example.sever.mapper.AnhMapper;
import com.example.sever.repository.AnhRepository;
import com.example.sever.service.AnhService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnhServiceipml implements AnhService {

    private final Cloudinary cloudinary;
    private final AnhRepository anhRepository;
    private final AnhMapper anhMapper;

    @Override
    public AnhDisplayReponse uploadImageAndSave(AnhAddRequestDTO request) throws IOException {
        MultipartFile file = request.getFile();
        String originalName = file.getOriginalFilename();
        assert originalName != null;

        // Sinh idAnh theo kiểu anh0001, anh0002...
        String idAnh = StringUtils.isNotBlank(request.getIdAnh())
                ? request.getIdAnh()
                : generateNextIdAnh();

        String extension = getFileExtension(originalName);
        File fileUpload = convert(file, idAnh, extension);

        // Upload lên Cloudinary
        cloudinary.uploader().upload(fileUpload, ObjectUtils.asMap("public_id", idAnh));
        cleanDisk(fileUpload);

        String imageUrl = cloudinary.url().generate(idAnh + "." + extension);

        // Tạo entity và lưu
        Anh anh = anhMapper.toEntity(request);
        anh.setId(UUID.randomUUID());
        anh.setIdAnh(idAnh);
        anh.setImgURL(imageUrl);

        anhRepository.save(anh);

        return anhMapper.toResponse(anh);
    }

    @Override
    public AnhDisplayReponse updateImage(UUID id, AnhUpdateRequestDTO request) throws IOException {
        Anh anh = anhRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ảnh với ID: " + id));

        MultipartFile file = request.getFile();
        String fileName = file.getOriginalFilename();
        assert fileName != null;

        // Dùng idAnh mới nếu có, còn không giữ nguyên
        String newIdAnh = StringUtils.isNotBlank(request.getIdAnh())
                ? request.getIdAnh()
                : anh.getIdAnh();

        String extension = getFileExtension(fileName);
        File fileUpload = convert(file, newIdAnh, extension);

        // Upload lại lên Cloudinary
        cloudinary.uploader().upload(fileUpload, ObjectUtils.asMap("public_id", newIdAnh));
        cleanDisk(fileUpload);

        // Update entity
        anh.setIdAnh(newIdAnh);
        anh.setImgURL(cloudinary.url().generate(newIdAnh + "." + extension));

        anhRepository.save(anh);
        return anhMapper.toResponse(anh);
    }

    // Sinh idAnh kiểu: anh0001, anh0002, ...
    private String generateNextIdAnh() {
        String lastId = anhRepository.findMaxIdAnh(); // ví dụ "anh0025"
        int next = 1;

        if (lastId != null && lastId.startsWith("anh")) {
            try {
                next = Integer.parseInt(lastId.substring(3)) + 1;
            } catch (NumberFormatException e) {
                log.warn("Failed to parse idAnh number: {}", lastId);
            }
        }

        return String.format("anh%04d", next); // => "anh0026"
    }

    private File convert(MultipartFile file, String name, String extension) throws IOException {
        File convFile = new File(name + "." + extension);
        try (InputStream is = file.getInputStream()) {
            Files.copy(is, convFile.toPath());
        }
        return convFile;
    }

    private void cleanDisk(File file) {
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            log.warn("Cannot delete temp file: {}", file.getAbsolutePath());
        }
    }

    private String getFileExtension(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
    }
}
