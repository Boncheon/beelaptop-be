package com.example.sever;


public enum TrangThaiVoucher {

    CHUA_KICH_HOAT(0),
    DANG_HOAT_DONG(1),
    HET_HAN(2),
    NGUNG_HOAT_DONG(3);

    private final int value;

    TrangThaiVoucher(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


}
