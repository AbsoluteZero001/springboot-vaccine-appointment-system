package com.springboot.vaccineappointmentsystem.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AppointmentStatus {
    APPOINTED(0, "已预约"),
    COMPLETED(1, "已完成"),
    NO_SHOW(2, "未到场"),
    CANCELLED(3, "已取消");

    private final int code;
    private final String displayName;

    AppointmentStatus(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static AppointmentStatus fromCode(int code) {
        for (AppointmentStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("Unknown appointment status code: " + code);
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == NO_SHOW || this == CANCELLED;
    }

    public boolean canTransitionTo(AppointmentStatus target) {
        return switch (this) {
            case APPOINTED -> target == COMPLETED || target == NO_SHOW || target == CANCELLED;
            case COMPLETED, NO_SHOW, CANCELLED -> false;
        };
    }
}
