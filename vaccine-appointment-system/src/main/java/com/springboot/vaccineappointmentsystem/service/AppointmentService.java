package com.springboot.vaccineappointmentsystem.service;

import com.springboot.vaccineappointmentsystem.entity.Appointment;
import com.springboot.vaccineappointmentsystem.entity.AppointmentLog;
import com.springboot.vaccineappointmentsystem.entity.VaccinationRecord;
import com.springboot.vaccineappointmentsystem.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    Appointment createAppointment(Long userId, Long vaccineId, LocalDateTime appointmentTime, Long familyMemberId);
    Appointment cancelAppointment(Long appointmentId, Long userId);
    Appointment cancelAppointmentByAdmin(Long appointmentId);
    Appointment completeAppointment(Long appointmentId);

    VaccinationRecord createLateRecord(Long appointmentId, String notes);

    Appointment rescheduleAppointment(Long appointmentId, Long userId, LocalDateTime newTime);

    void detectAndMarkNoShow();
    Optional<Appointment> getAppointmentById(Long id);
    List<Appointment> getAppointmentsByUser(Long userId);
    List<Appointment> getAppointmentsByVaccine(Long vaccineId);
    List<Appointment> getPendingAppointments();
    List<Appointment> getAllAppointments();

    List<Appointment> getAppointmentsByStatus(AppointmentStatus status);
    boolean hasPendingAppointment(Long userId, Long vaccineId);

    List<AppointmentLog> getAppointmentLogs(Long appointmentId);
}
