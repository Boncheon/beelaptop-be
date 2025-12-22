package com.example.sever.statusauto;

public class SeriStatus {
    private SeriStatus() {}

    public static final int ACTIVE  = 1; // ĐANG HOẠT ĐỘNG
    public static final int PENDING = 2; // ĐANG CHỜ (đã gắn hoá đơn, chưa thanh toán)
    public static final int SOLD    = 3; // ĐÃ BÁN
}
