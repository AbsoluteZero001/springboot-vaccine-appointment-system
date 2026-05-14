package com.springboot.vaccineappointmentsystem.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserType {
    NORMAL(0, "普通用户"),
    ADMIN(1, "管理员");

    private final int code;
    private final String displayName;

    UserType(int code, String displayName) {
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
    public static UserType fromCode(int code) {
        for (UserType t : values()) {
            if (t.code == code) return t;
        }
        throw new IllegalArgumentException("Unknown user type code: " + code);
    }
}
