package com.example.sever.utils;


public abstract class VnPayConstant {

    public static String vnp_Version = "2.1.0";

    public static String vnp_Command = "pay";

    public static String vnp_TmnCode = "JK1W3UIA";

    public static String vnp_HashSecret = "KIDDWUTDBOCVGT409FR43PSMPPTZEIT7";

    public static String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    public static String vnp_Refund = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

    public static String vnp_BankCode = "";

    public static String vnp_CurrCode = "VND";

    public static String vnp_Locale = "vn";


    public static String vnp_ReturnUrl = "http://localhost:8080/api/v1/laptops/vnpay/payment-callback";

    public static String vnp_ReturnUrlBuyOnline = "http://localhost:3000/client/payment/payment-success";

    public static String vnp_DevRegUrl = "http://sandbox.vnpayment.vn/devreg/";

    public static String vnp_MerchantPortalUrl = "https://sandbox.vnpayment.vn/merchantv2/";

}

