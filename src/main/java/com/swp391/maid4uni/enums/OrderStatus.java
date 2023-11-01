package com.swp391.maid4uni.enums;

public enum OrderStatus {
    DECLINED("Từ chối"),
    WAITING_FOR_APPROVAL("Chờ duyệt"),
    APPROVED("Đồng ý");

    private final String value;

    private OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
