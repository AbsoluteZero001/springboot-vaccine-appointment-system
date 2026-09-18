package com.springboot.vaccineappointmentsystem.service.impl;

import com.springboot.vaccineappointmentsystem.entity.Appointment;
import com.springboot.vaccineappointmentsystem.entity.VaccinationRecord;
import com.springboot.vaccineappointmentsystem.enums.AppointmentStatus;
import com.springboot.vaccineappointmentsystem.enums.VaccinationRecordStatus;
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

        AppointmentStatus apptStatus = appointment.getStatus();
        if (apptStatus == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("无法为已取消的预约创建记录");
        }

        List<VaccinationRecord> existing = vaccinationRecordRepository.findByAppointmentId(appointmentId);
        if (!existing.isEmpty()) {
            throw new RuntimeException("该预约已有接种记录");
        }

        VaccinationRecord record = new VaccinationRecord();
        record.setAppointment(appointment);
        record.setUser(appointment.getUser());
        record.setVaccine(appointment.getVaccine());
        record.setVaccinationTime(vaccinationTime);
        record.setNotes(notes);
        record.setStatus(VaccinationRecordStatus.SCHEDULED);
        return vaccinationRecordRepository.save(record);
    }

    @Override
    public VaccinationRecord updateRecord(Long recordId, LocalDateTime vaccinationTime, String notes, VaccinationRecordStatus status) {
        VaccinationRecord record = vaccinationRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("接种记录未找到"));
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
        return vaccinationRecordRepository.findByUserIdWithVaccine(userId);
    }

    @Override
    public List<VaccinationRecord> getRecordsByVaccine(Long vaccineId) {
        return vaccinationRecordRepository.findByVaccineId(vaccineId);
    }

    @Override
    public List<VaccinationRecord> getRecordsByStatus(VaccinationRecordStatus status) {
        return vaccinationRecordRepository.findByStatusWithDetails(status);
    }

    @Override
    public VaccinationRecord markAsAdministered(Long recordId, LocalDateTime actualTime, String notes) {
        VaccinationRecord record = vaccinationRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("接种记录未找到"));
        record.setStatus(VaccinationRecordStatus.ADMINISTERED);
        if (actualTime != null) {
            record.setVaccinationTime(actualTime);
        }
        if (notes != null) {
            record.setNotes(notes);
        }

        // Sync appointment status to COMPLETED if in a non-terminal state
        Appointment appointment = record.getAppointment();
        if (!appointment.getStatus().isTerminal()) {
            appointment.setStatus(AppointmentStatus.COMPLETED);
            appointment.setStatusUpdatedAt(LocalDateTime.now());
            appointmentRepository.save(appointment);
        }
        return vaccinationRecordRepository.save(record);
    }
}
