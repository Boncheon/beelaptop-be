package com.example.sever;

public enum KieuGiamGia {

    GIAM_CO_DINH("Giảm cố định"),       // Giảm số tiền trực tiếp
    GIAM_PHAN_TRAM("Giảm phần trăm");   // Giảm theo %

    private final String moTa;

    KieuGiamGia(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }


}
