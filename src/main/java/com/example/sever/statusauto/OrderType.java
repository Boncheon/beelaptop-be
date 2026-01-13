package com.example.sever.statusauto;

public enum OrderType {
    POS("TAI_QUAY"),
    ONLINE("ONLINE"),
    DELIVERY("GIAO_HANG");

    private final String dbValue;

    OrderType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public static OrderType fromDb(String s) {
        if (s == null) return null;
        for (OrderType t : values()) {
            if (t.dbValue.equalsIgnoreCase(s) || t.name().equalsIgnoreCase(s)) return t;
        }
        throw new IllegalArgumentException("Unknown OrderType: " + s);
    }
}
