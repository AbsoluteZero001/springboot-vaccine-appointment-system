package com.springboot.vaccineappointmentsystem.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AppointmentStatusConverter implements AttributeConverter<AppointmentStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AppointmentStatus status) {
        return status == null ? null : status.getCode();
    }

    @Override
    public AppointmentStatus convertToEntityAttribute(Integer code) {
        return code == null ? null : AppointmentStatus.fromCode(code);
    }
}
