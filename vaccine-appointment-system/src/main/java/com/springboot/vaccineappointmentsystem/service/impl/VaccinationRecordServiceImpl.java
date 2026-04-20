package com.springboot.vaccineappointmentsystem.service.impl;

import com.springboot.vaccineappointmentsystem.entity.Appointment;
import com.springboot.vaccineappointmentsystem.entity.VaccinationRecord;
import com.springboot.vaccineappointmentsystem.repository.AppointmentRepository;
import com.springboot.vaccineappointmentsystem.repository.VaccinationRecordRepository;
import com.springboot.vaccineappointmentsystem.service.VaccinationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VaccinationRecordServiceImpl implements VaccinationRecordService {

    @Autowired
    private VaccinationRecordRepository vaccinationRecordRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public VaccinationRecord createRecord(Long appointmentId, LocalDateTime vaccinationTime, String notes) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        // Check if appointment is confirmed or completed
        if (appointment.getStatus() != 1 && appointment.getStatus() != 2) {
            throw new RuntimeException("Appointment must be confirmed or completed to create record");
        }
        // Check if record already exists
        List<VaccinationRecord> existing = vaccinationRecordRepository.findByAppointmentId(appointmentId);
        if (!existing.isEmpty()) {
            throw new RuntimeException("Vaccination record already exists for this appointment");
        }
        VaccinationRecord record = new VaccinationRecord();
        record.setAppointment(appointment);
        record.setUser(appointment.getUser());
        record.setVaccine(appointment.getVaccine());
        record.setVaccinationTime(vaccinationTime);
        record.setNotes(notes);
        record.setStatus(0); // scheduled
        return vaccinationRecordRepository.save(record);
    }

    @Override
    public VaccinationRecord updateRecord(Long recordId, LocalDateTime vaccinationTime, String notes, Integer status) {
        VaccinationRecord record = vaccinationRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Vaccination record not found"));
        if (vaccinationTime != null) {
            record.setVaccinationTime(vaccinationTime);
        }
        if (notes != null) {
            record.setNotes(notes);
        }
        if (status != null) {
            record.setStatus(status);
        }
        return vaccinationRecordRepository.save(record);
    }

    @Override
    public Optional<VaccinationRecord> getRecordById(Long id) {
        return vaccinationRecordRepository.findById(id);
    }

    @Override
    public List<VaccinationRecord> getRecordsByUser(Long userId) {
        return vaccinationRecordRepository.findByUserId(userId);
    }

    @Override
    public List<VaccinationRecord> getRecordsByVaccine(Long vaccineId) {
        return vaccinationRecordRepository.findByVaccineId(vaccineId);
    }

    @Override
    public List<VaccinationRecord> getRecordsByStatus(Integer status) {
        return vaccinationRecordRepository.findByStatus(status);
    }

    @Override
    public VaccinationRecord markAsAdministered(Long recordId, LocalDateTime actualTime, String notes) {
        VaccinationRecord record = vaccinationRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Vaccination record not found"));
        record.setStatus(1); // administered
        if (actualTime != null) {
            record.setVaccinationTime(actualTime);
        }
        if (notes != null) {
            record.setNotes(notes);
        }
        // Update appointment status to completed if not already
        Appointment appointment = record.getAppointment();
        if (appointment.getStatus() != 2) {
            appointment.setStatus(2); // completed
            appointmentRepository.save(appointment);
        }
        return vaccinationRecordRepository.save(record);
    }
}