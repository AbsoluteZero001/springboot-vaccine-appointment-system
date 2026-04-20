package com.springboot.vaccineappointmentsystem.service;

import com.springboot.vaccineappointmentsystem.entity.Appointment;
import com.springboot.vaccineappointmentsystem.entity.User;
import com.springboot.vaccineappointmentsystem.entity.Vaccine;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    Appointment createAppointment(Long userId, Long vaccineId, LocalDateTime appointmentTime);
    Appointment cancelAppointment(Long appointmentId, Long userId);
    Appointment confirmAppointment(Long appointmentId);
    Appointment completeAppointment(Long appointmentId);
    Optional<Appointment> getAppointmentById(Long id);
    List<Appointment> getAppointmentsByUser(Long userId);
    List<Appointment> getAppointmentsByVaccine(Long vaccineId);
    List<Appointment> getPendingAppointments();
    boolean hasPendingAppointment(Long userId, Long vaccineId);
}