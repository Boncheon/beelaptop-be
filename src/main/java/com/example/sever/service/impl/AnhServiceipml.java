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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnhServiceipml implements AnhService {

    private final Cloudinary cloudinary;
    private final AnhRepository anhRepository;
    private final AnhMapper anhMapper;

    // ================== ADD ==================
    @Override
    public AnhDisplayReponse uploadImageAndSave(AnhAddRequestDTO request) throws IOException {
        MultipartFile file = request.getFile();
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File ảnh không được trống");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            throw new IllegalArgumentException("Tên file không hợp lệ");
        }

        // Sinh idAnh theo kiểu anh0001, anh0002...
        String idAnh = StringUtils.isNotBlank(request.getIdAnh())
                ? request.getIdAnh().trim()
                : generateNextIdAnh();

        String extension = getFileExtension(originalName);
        File fileUpload = convert(file, idAnh, extension);

        try {
            // Upload lên Cloudinary với public_id = idAnh
            cloudinary.uploader().upload(
                    fileUpload,
                    ObjectUtils.asMap("public_id", idAnh, "resource_type", "image")
            );
        } finally {
            cleanDisk(fileUpload);
        }

        String imageUrl = cloudinary.url().generate(idAnh + "." + extension);

        // Tạo entity & gắn LaptopChiTiet từ mapper
        Anh anh = anhMapper.toEntity(request);
        anh.setId(UUID.randomUUID());
        anh.setIdAnh(idAnh);
        anh.setImgURL(imageUrl);

        anhRepository.save(anh);
        return anhMapper.toResponse(anh);
    }

    // ================== UPDATE FILE ẢNH ==================
    @Override
    public AnhDisplayReponse updateImage(UUID id, AnhUpdateRequestDTO request) throws IOException {
        Anh anh = anhRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ảnh với ID: " + id));

        MultipartFile file = request.getFile();
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File ảnh không được trống");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("Tên file không hợp lệ");
        }

        // Dùng idAnh mới nếu có, không thì giữ idAnh cũ
        String newIdAnh = StringUtils.isNotBlank(request.getIdAnh())
                ? request.getIdAnh().trim()
                : anh.getIdAnh();

        String extension = getFileExtension(fileName);
        File fileUpload = convert(file, newIdAnh, extension);

        try {
            cloudinary.uploader().upload(
                    fileUpload,
                    ObjectUtils.asMap("public_id", newIdAnh, "resource_type", "image")
            );
        } finally {
            cleanDisk(fileUpload);
        }

        anh.setIdAnh(newIdAnh);
        anh.setImgURL(cloudinary.url().generate(newIdAnh + "." + extension));

        anhRepository.save(anh);
        return anhMapper.toResponse(anh);
    }

    // ================== LIST THEO SPCT ==================
    @Override
    public List<AnhDisplayReponse> getByLaptopChiTiet(UUID idLaptopChiTiet) {
        return anhRepository.findByIdLaptopChiTiet_Id(idLaptopChiTiet)
                .stream()
                .map(anhMapper::toResponse)
                .toList();
    }

    // ================== DELETE ==================
//    @Override
//    public void delete(UUID id) {
//        anhRepository.deleteById(id);
//    }

    // ================== HELPERS ==================

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
        int dot = originalFilename.lastIndexOf('.');
        if (dot < 0 || dot == originalFilename.length() - 1) {
            throw new IllegalArgumentException("File không có phần mở rộng hợp lệ: " + originalFilename);
        }
        return originalFilename.substring(dot + 1);
    }
}
