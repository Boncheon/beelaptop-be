package com.example.sever.utils;


public class OrderConstants {

    // trạng thái đơn (online)
    public static final int ST_WAIT_CONFIRM = 1;   // Chờ xác nhận
    public static final int ST_CONFIRMED    = 2;   // Đã xác nhận
    public static final int ST_PREPARING    = 3;   // Đang chuẩn bị hàng
    public static final int ST_READY_SHIP   = 4;   // Sẵn sàng giao / Chờ giao hàng
    public static final int ST_SHIPPING     = 5;   // Đang giao hàng
    public static final int ST_DONE         = 6;   // Hoàn thành
    public static final int ST_CANCEL       = 7;   // Hủy

    // trạng thái thanh toán
    public static final int PAY_UNPAID = 0;
    public static final int PAY_PAID   = 1;

    private OrderConstants() {}
}