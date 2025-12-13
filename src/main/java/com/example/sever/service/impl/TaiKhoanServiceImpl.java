package com.example.sever.service.impl;

import com.cloudinary.Cloudinary;
import com.example.sever.dto.AccountDTO.DiaChiCreateRequest;
import com.example.sever.dto.AccountDTO.DiaChiUpdateRequest;
import com.example.sever.dto.AccountDTO.UpdateProfileCustomerRequest;
import com.example.sever.dto.AccountDTO.UpdateProfileCustomerResponse;
import com.example.sever.dto.request.TaiKhoanAddRequestDTO;
import com.example.sever.dto.request.TaiKhoanUpdateRequestDTO;
import com.example.sever.dto.response.DiaChi.DiaChiProjection;
import com.example.sever.dto.response.DiaChi.DiaChiResponse;
import com.example.sever.dto.response.TaiKhoanDisplayReponse;
import com.example.sever.entity.DiaChi;
import com.example.sever.entity.Role;
import com.example.sever.entity.TaiKhoan;
import com.example.sever.exception.AppException;
import com.example.sever.exception.ErrorCode;
import com.example.sever.mapper.TaiKhoanMapper;
import com.example.sever.repository.DiaChiRepository;
import com.example.sever.repository.RoleRepository;
import com.example.sever.repository.TaiKhoanRepository;
import com.example.sever.service.TaiKhoanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaiKhoanServiceImpl implements TaiKhoanService {
    private final RoleRepository roleRepository;
    private final TaiKhoanRepository taikhoanRepository;


    private final TaiKhoanMapper taikhoanMapper;
    @Autowired
    private DiaChiRepository diaChiRepository;
    @Autowired
    private Cloudinary cloudinary;

    public TaiKhoanServiceImpl(RoleRepository roleRepository, TaiKhoanRepository taikhoanRepository,
                               TaiKhoanMapper taikhoanMapper) {
        this.roleRepository = roleRepository;
        this.taikhoanRepository = taikhoanRepository;
        this.taikhoanMapper = taikhoanMapper;
    }

    @Override
    public Page<TaiKhoanDisplayReponse> getAllTaiKhoanforDisplay(Pageable pageable) {
        Page<TaiKhoan> tkPage = taikhoanRepository.findAll(pageable);
        List<TaiKhoanDisplayReponse> romDisplayReponses = tkPage.getContent().stream()
                .map(taikhoanMapper::getAlldisplayTaiKhoan).collect(Collectors.toList());

        return new PageImpl<>(romDisplayReponses , pageable, tkPage.getTotalElements());
    }

    @Override
    public TaiKhoanDisplayReponse addTaiKhoan(TaiKhoanAddRequestDTO updatedto) {

        TaiKhoan tk = new TaiKhoan();
        tk.setId(UUID.randomUUID());
        tk.setIdTaiKhoan(updatedto.getIdTaiKhoan());
        tk.setTen(updatedto.getTen());
        tk.setTrangThai(updatedto.getTrangThai());
        tk.setAnh(updatedto.getAnh());
        tk.setGioiTinh(updatedto.getGioiTinh());
        tk.setMatKhau(updatedto.getMatKhau());
        tk.setNgaySinh(updatedto.getNgaySinh());
        tk.setSoDienThoai(updatedto.getSoDienThoai());
        // Lấy entity liên kết từ DB
        Role role = roleRepository.findById(updatedto.getRole().getId())
                .orElseThrow(() -> new RuntimeException("RAM không tồn tại"));
        // Gán vào phiên bản
        tk.setIdRole(role);

        // Lưu lại
        TaiKhoan saved = taikhoanRepository.save(tk);
        return taikhoanMapper.getAlldisplayTaiKhoan(saved);
    }

    @Override
    public TaiKhoanDisplayReponse updateTaiKhoan(TaiKhoanUpdateRequestDTO updatedto) {
        TaiKhoan tk = taikhoanRepository.findById(updatedto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên bản"));
        tk.setIdTaiKhoan(updatedto.getIdTaiKhoan());
        tk.setTen(updatedto.getTen());
        tk.setTrangThai(updatedto.getTrangThai());
        tk.setAnh(updatedto.getAnh());
        tk.setGioiTinh(updatedto.getGioiTinh());
        tk.setMatKhau(updatedto.getMatKhau());
        tk.setNgaySinh(updatedto.getNgaySinh());
        tk.setSoDienThoai(updatedto.getSoDienThoai());
        // Lấy entity liên kết từ DB
        Role role = roleRepository.findById(updatedto.getRole().getId())
                .orElseThrow(() -> new RuntimeException("RAM không tồn tại"));
        // Gán vào phiên bản
        tk.setIdRole(role);

        // Lưu lại
        TaiKhoan saved = taikhoanRepository.save(tk);
        return taikhoanMapper.getAlldisplayTaiKhoan(saved);
    }
    @Override
    public List<DiaChiProjection> findAllAdress(UUID id) {
        return diaChiRepository.findAllByTaiKhoanProjection(id);
    }

    @Override
    public DiaChiResponse createAddressCustomer(DiaChiCreateRequest request) {
        // Lấy tài khoản từ database
        int number = (int)(Math.random() * 900) + 100;
        TaiKhoan taiKhoan = taikhoanRepository.findById(request.getIdTaiKhoan())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

        DiaChi diaChi = new DiaChi();

        diaChi.setId(UUID.randomUUID());
        diaChi.setIdDiaChi("DC" + number);
        diaChi.setIdTaiKhoan(taiKhoan);
        diaChi.setQuocGia("VietNam");
        diaChi.setTinhThanh(request.getTinhThanh());
        diaChi.setQuanHuyen(request.getQuanHuyen());
        diaChi.setPhuongXa(request.getPhuongXa());
        diaChi.setDiaChiChiTiet(request.getDiaChiChiTiet());
        diaChi.setHoTen(request.getHoTen());
        diaChi.setSoDienThoai(request.getSoDienThoai());

        // Khi thêm mới chưa phải mặc định
        diaChi.setMacDinh(false);

        DiaChi saved = diaChiRepository.save(diaChi);

        return new DiaChiResponse(
                saved.getId(),
                saved.getIdDiaChi(),
                saved.getIdTaiKhoan().getId(),  // Trả về UUID của tài khoản
                saved.getQuocGia(),
                saved.getTinhThanh(),
                saved.getQuanHuyen(),
                saved.getPhuongXa(),
                saved.getDiaChiChiTiet(),
                saved.getMacDinh() ,
                saved.getHoTen() ,
                saved.getSoDienThoai()
        );
    }



    @Override
    @Transactional
    public void deleteAddressCustomer(UUID id) {
        DiaChi diaChi = diaChiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại"));

        diaChiRepository.delete(diaChi);
    }

    @Override
    @Transactional
    public DiaChiResponse updateAddressCustomer(UUID id, DiaChiUpdateRequest request) {
        DiaChi diaChi = diaChiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ!"));

        diaChi.setQuocGia("VietNam");
        diaChi.setTinhThanh(request.getTinhThanh());
        diaChi.setQuanHuyen(request.getQuanHuyen());
        diaChi.setPhuongXa(request.getPhuongXa());
        diaChi.setDiaChiChiTiet(request.getDiaChiChiTiet());
        diaChi.setHoTen(request.getHoTen());
        diaChi.setSoDienThoai(request.getSoDienThoai());
        DiaChi saved = diaChiRepository.save(diaChi);

        return new DiaChiResponse(
                saved.getId(),
                saved.getIdDiaChi(),
                saved.getIdTaiKhoan().getId(),
                saved.getQuocGia(),
                saved.getTinhThanh(),
                saved.getQuanHuyen(),
                saved.getPhuongXa(),saved.getDiaChiChiTiet(),
                saved.getMacDinh() ,
                saved.getHoTen() ,
                saved.getSoDienThoai()
        );
    }

    @Override
    public DiaChiResponse setAddressDefaultCustomer(UUID id) {
        DiaChi diaChi = diaChiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ!"));

        UUID idTaiKhoan = diaChi.getIdTaiKhoan().getId();

        // Bỏ mặc định tất cả địa chỉ của user này
        diaChiRepository.clearDefault(idTaiKhoan);

        diaChi.setMacDinh(true);
        DiaChi saved = diaChiRepository.save(diaChi);

        return new DiaChiResponse(
                saved.getId(),
                saved.getIdDiaChi(),
                idTaiKhoan,
                saved.getQuocGia(),
                saved.getTinhThanh(),
                saved.getQuanHuyen(),
                saved.getPhuongXa(),
                saved.getDiaChiChiTiet(),
                true ,
                saved.getHoTen() ,
                saved.getSoDienThoai()
        );
    }

    @Override
    @Transactional
    public UpdateProfileCustomerResponse updateProfileCustomer(UUID id, UpdateProfileCustomerRequest request) {
        // Tìm tài khoản
        TaiKhoan taiKhoan = taikhoanRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản"));

        // Kiểm tra email trùng (nếu thay đổi email)
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (!request.getEmail().equals(taiKhoan.getEmail())) {
                if (taikhoanRepository.existsByEmail(request.getEmail())) {
                    throw new AppException(ErrorCode.EMAIL_EXISTED, "Email đã được sử dụng");
                }
            }
        }

        // Kiểm tra số điện thoại trùng (nếu thay đổi số điện thoại)
        if (request.getSoDienThoai() != null && !request.getSoDienThoai().trim().isEmpty()) {
            if (!request.getSoDienThoai().equals(taiKhoan.getSoDienThoai())) {
                if (taikhoanRepository.existsBySoDienThoai(request.getSoDienThoai())) {
                    throw new AppException(ErrorCode.SO_DIEN_THOAI_EXISTED, "Số điện thoại đã được sử dụng");
                }
            }
        }

        // Cập nhật thông tin - luôn cập nhật nếu có giá trị (không cần kiểm tra empty)
        if (request.getTen() != null) {
            taiKhoan.setTen(request.getTen().trim());
        }
        if (request.getEmail() != null) {
            taiKhoan.setEmail(request.getEmail().trim());
        }
        if (request.getSoDienThoai() != null) {
            taiKhoan.setSoDienThoai(request.getSoDienThoai().trim());
        }
        if (request.getNgaySinh() != null) {
            taiKhoan.setNgaySinh(request.getNgaySinh());
        }
        if (request.getGioiTinh() != null) {
            taiKhoan.setGioiTinh(request.getGioiTinh().trim());
        }

        // Xử lý ảnh: ưu tiên upload file mới, nếu không có thì dùng URL, nếu không có thì giữ nguyên
        if (request.getAnh() != null && !request.getAnh().isEmpty()) {
            // Upload file mới
            String imageUrl = uploadImage(request.getAnh());
            taiKhoan.setAnh(imageUrl);
        } else if (request.getAnhUrl() != null && !request.getAnhUrl().trim().isEmpty()) {
            // Chỉ update URL (từ JSON request)
            taiKhoan.setAnh(request.getAnhUrl().trim());
        }
        // Nếu cả hai đều null, giữ nguyên ảnh cũ

        // Lưu lại (updated_at sẽ tự động cập nhật bởi @LastModifiedDate)
        TaiKhoan saved = taikhoanRepository.save(taiKhoan);

        // Tạo response
        UpdateProfileCustomerResponse response = new UpdateProfileCustomerResponse();
        response.setId(saved.getId());
        response.setIdTaiKhoan(saved.getIdTaiKhoan());
        response.setTen(saved.getTen());
        response.setEmail(saved.getEmail());
        response.setSoDienThoai(saved.getSoDienThoai());
        response.setNgaySinh(saved.getNgaySinh());
        response.setGioiTinh(saved.getGioiTinh());
        response.setAnh(saved.getAnh());
        response.setUpdatedAt(saved.getUpdatedAt());
        response.setMessage("Cập nhật thông tin tài khoản thành công");

        return response;
    }

    private String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/jpg"))) {
                throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED, "Chỉ chấp nhận file ảnh JPEG hoặc PNG");
            }

            // Validate file size (tối đa 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED, "Kích thước ảnh không được vượt quá 5MB");
            }

            // Upload lên Cloudinary
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of());
            String imageUrl = uploadResult.get("url").toString();
            log.info("Upload ảnh thành công: {}", imageUrl);
            return imageUrl;
        } catch (IOException e) {
            log.error("Lỗi khi upload ảnh lên Cloudinary: {}", e.getMessage());
            throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED, "Lỗi khi upload ảnh: " + e.getMessage());
        }
    }

}
