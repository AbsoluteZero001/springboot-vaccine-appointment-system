package com.springboot.vaccineappointmentsystem.service;

import com.springboot.vaccineappointmentsystem.entity.VaccinationRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VaccinationRecordService {
    VaccinationRecord createRecord(Long appointmentId, LocalDateTime vaccinationTime, String notes);
    VaccinationRecord updateRecord(Long recordId, LocalDateTime vaccinationTime, String notes, Integer status);
    Optional<VaccinationRecord> getRecordById(Long id);
    List<VaccinationRecord> getRecordsByUser(Long userId);
    List<VaccinationRecord> getRecordsByVaccine(Long vaccineId);
    List<VaccinationRecord> getRecordsByStatus(Integer status);
    VaccinationRecord markAsAdministered(Long recordId, LocalDateTime actualTime, String notes);
}