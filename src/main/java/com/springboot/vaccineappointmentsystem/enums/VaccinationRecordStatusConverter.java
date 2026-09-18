package com.springboot.vaccineappointmentsystem.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class VaccinationRecordStatusConverter implements AttributeConverter<VaccinationRecordStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(VaccinationRecordStatus status) {
        return status == null ? null : status.getCode();
    }

    @Override
    public VaccinationRecordStatus convertToEntityAttribute(Integer code) {
        return code == null ? null : VaccinationRecordStatus.fromCode(code);
    }
}
