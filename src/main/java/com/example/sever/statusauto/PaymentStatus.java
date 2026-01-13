package com.example.sever.statusauto;

import java.util.Arrays;

public enum PaymentStatus {
    UNPAID(0),
    PAID(1);

    private final int code;

    PaymentStatus(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public static PaymentStatus fromCode(Integer code) {
        if (code == null) return null;
        return Arrays.stream(values())
                .filter(s -> s.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown PaymentStatus code: " + code));
    }
}
