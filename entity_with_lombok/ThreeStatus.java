package com.aiming.low.forum_post_service.entity;

public enum ThreeStatus {

    ACTIVE(1),
    NOT_ACTIVE(0),
    DELETE(-1);

    private int value;

    ThreeStatus(int value) {
        this.value = value;
    }

    public static ThreeStatus fromValue(int value) {
        for (ThreeStatus ts: ThreeStatus.values()) {
            if (ts.value == value) {
                return ts;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
