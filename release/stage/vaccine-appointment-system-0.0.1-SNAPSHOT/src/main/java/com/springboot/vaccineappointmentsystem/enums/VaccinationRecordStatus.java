package com.springboot.vaccineappointmentsystem.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum VaccinationRecordStatus {
    SCHEDULED(0, "已安排"),
    ADMINISTERED(1, "已接种");

    private final int code;
    private final String displayName;

    VaccinationRecordStatus(int code, String displayName) {
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
    public static VaccinationRecordStatus fromCode(int code) {
        for (VaccinationRecordStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("Unknown vaccination record status code: " + code);
    }
}
