package com.example.sever.converter;

import com.example.sever.KieuGiamGia;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class KieuGiamGiaConverter implements AttributeConverter<KieuGiamGia, String> {

    @Override
    public String convertToDatabaseColumn(KieuGiamGia kieuGiamGia) {
        if (kieuGiamGia == null) {
            return null;
        }
        return kieuGiamGia.getMoTa();
    }

    @Override
    public KieuGiamGia convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        
        String trimmedData = dbData.trim();
        
        // Tìm enum theo mô tả
        for (KieuGiamGia kieu : KieuGiamGia.values()) {
            if (kieu.getMoTa().equalsIgnoreCase(trimmedData)) {
                return kieu;
            }
        }
        
        // Nếu không tìm thấy theo mô tả, thử tìm theo name
        try {
            return KieuGiamGia.valueOf(trimmedData);
        } catch (IllegalArgumentException e) {
            // Xử lý các trường hợp đặc biệt
            if (trimmedData.equalsIgnoreCase("Cố định") || trimmedData.equalsIgnoreCase("Co dinh") 
                    || trimmedData.equalsIgnoreCase("GIAM_CO_DINH")) {
                return KieuGiamGia.GIAM_CO_DINH;
            }
            if (trimmedData.equalsIgnoreCase("Phần trăm") || trimmedData.equalsIgnoreCase("Phan tram") 
                    || trimmedData.equalsIgnoreCase("%") || trimmedData.equalsIgnoreCase("GIAM_PHAN_TRAM")) {
                return KieuGiamGia.GIAM_PHAN_TRAM;
            }
            
            // Log warning và trả về null thay vì throw exception
            System.err.println("Warning: Không thể chuyển đổi giá trị '" + dbData + "' thành KieuGiamGia. Trả về null.");
            return null;
        }
    }
}


















