package com.springboot.vaccineappointmentsystem.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    UNKNOWN(0, "未知"),
    MALE(1, "男"),
    FEMALE(2, "女");

    private final int code;
    private final String displayName;

    Gender(int code, String displayName) {
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
    public static Gender fromCode(int code) {
        for (Gender g : values()) {
            if (g.code == code) return g;
        }
        return UNKNOWN;
    }
}
