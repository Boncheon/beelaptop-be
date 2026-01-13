package com.example.sever.utils;

import com.example.sever.dto.Pos.GHN.GhnFeeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class GhnFeeRequestBuilder {

    private static final Logger log = LoggerFactory.getLogger(GhnFeeRequestBuilder.class);

    private GhnFeeRequestBuilder() {}

    // ====== 1 nguồn chuẩn DEFAULT để không lệch ======
    public static final int DEFAULT_ITEM_WEIGHT_GRAM = 2000; // 2kg / laptop
    public static final int DEFAULT_L_CM = 35;
    public static final int DEFAULT_W_CM = 25;
    public static final int DEFAULT_H_CM = 2;

    // DB khoiLuong là KG? (vd 1.8) => true (x1000 ra gram)
    // DB khoiLuong là GRAM? (vd 1800) => false
    public static final boolean KHOI_LUONG_IS_KG = true;

    // bật/tắt insurance
    public static final boolean GHN_USE_INSURANCE_VALUE = false;

    public static int toPositiveIntCeil(Double v) {
        if (v == null || v <= 0) return 0;
        return (int) Math.ceil(v);
    }

    public static int toWeightGram(Double khoiLuong) {
        if (khoiLuong == null || khoiLuong <= 0) return 0;
        double w = khoiLuong;
        return KHOI_LUONG_IS_KG ? (int) Math.ceil(w * 1000.0) : (int) Math.ceil(w);
    }

    public static int moneyToIntSafe(BigDecimal money) {
        if (money == null) return 0;
        if (money.compareTo(BigDecimal.ZERO) <= 0) return 0;

        BigDecimal capped = money.min(BigDecimal.valueOf(Integer.MAX_VALUE));
        return capped.setScale(0, RoundingMode.DOWN).intValue();
    }

    /** Build request CHUẨN (trim wardCode, fallback defaults, insurance=subtotal) */
    public static GhnFeeRequest buildFeeRequest(
            Integer toDistrictId,
            String toWardCode,
            int totalWeightGram,
            int lengthCm,
            int widthCm,
            int heightCm,
            BigDecimal subtotal
    ) {
        // ✅ default theo config/constant (giữ tương thích code cũ)
        return buildFeeRequest(toDistrictId, toWardCode, totalWeightGram, lengthCm, widthCm, heightCm, subtotal, GHN_USE_INSURANCE_VALUE);
    }

    /** ✅ NEW: cho phép bật/tắt insurance theo request */
    public static GhnFeeRequest buildFeeRequest(
            Integer toDistrictId,
            String toWardCode,
            int totalWeightGram,
            int lengthCm,
            int widthCm,
            int heightCm,
            BigDecimal subtotal,
            boolean useInsurance
    ) {
        log.info("[GHN][FeeBuilder] input: toDistrictId={}, toWardCode='{}', totalWeightGram={}, LxWxH={}x{}x{}, subtotal={}, KHOI_LUONG_IS_KG={}, useInsurance={}",
                toDistrictId, toWardCode, totalWeightGram, lengthCm, widthCm, heightCm, subtotal, KHOI_LUONG_IS_KG, useInsurance);

        String ward = (toWardCode != null ? toWardCode.trim() : null);
        if (toDistrictId == null || ward == null || ward.isBlank()) {
            log.warn("[GHN][FeeBuilder] missing toDistrictId/toWardCode -> toDistrictId={}, ward='{}'", toDistrictId, ward);
            throw new IllegalArgumentException("Thiếu toDistrictId/toWardCode để tính phí GHN");
        }

        int w = (totalWeightGram > 0 ? totalWeightGram : DEFAULT_ITEM_WEIGHT_GRAM);
        int l = (lengthCm > 0 ? lengthCm : DEFAULT_L_CM);
        int wi = (widthCm > 0 ? widthCm : DEFAULT_W_CM);
        int h = (heightCm > 0 ? heightCm : DEFAULT_H_CM);

        int insurance = useInsurance ? moneyToIntSafe(subtotal) : 0;

        log.info("[GHN][FeeBuilder] normalized: toDistrictId={}, toWardCode='{}', weight={}g, size={}x{}x{}cm, insuranceValue={}",
                toDistrictId, ward, w, l, wi, h, insurance);

        GhnFeeRequest req = new GhnFeeRequest();
        req.setToDistrictId(toDistrictId);
        req.setToWardCode(ward);
        req.setWeight(w);
        req.setLength(l);
        req.setWidth(wi);
        req.setHeight(h);
        req.setInsuranceValue(insurance);

        log.info("[GHN][FeeBuilder] built request: {}", safeToString(req));
        return req;
    }

    // Tránh log bị null/khó đọc nếu GhnFeeRequest chưa override toString()
    private static String safeToString(GhnFeeRequest req) {
        if (req == null) return "null";
        return "GhnFeeRequest{toDistrictId=" + req.getToDistrictId()
                + ", toWardCode='" + req.getToWardCode() + '\''
                + ", weight=" + req.getWeight()
                + ", length=" + req.getLength()
                + ", width=" + req.getWidth()
                + ", height=" + req.getHeight()
                + ", insuranceValue=" + req.getInsuranceValue()
                + '}';
    }
}
