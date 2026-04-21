package com.springboot.vaccineappointmentsystem.service.impl;

import com.springboot.vaccineappointmentsystem.entity.Appointment;
import com.springboot.vaccineappointmentsystem.entity.User;
import com.springboot.vaccineappointmentsystem.entity.Vaccine;
import com.springboot.vaccineappointmentsystem.repository.AppointmentRepository;
import com.springboot.vaccineappointmentsystem.repository.UserRepository;
import com.springboot.vaccineappointmentsystem.repository.VaccineRepository;
import com.springboot.vaccineappointmentsystem.service.AppointmentService;
import com.springboot.vaccineappointmentsystem.service.RedisLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VaccineRepository vaccineRepository;

    @Autowired
    private RedisLockService redisLockService;

    @Override
    public Appointment createAppointment(Long userId, Long vaccineId, LocalDateTime appointmentTime) {
        // Acquire Redis lock to prevent concurrent duplicate appointments
        boolean lockAcquired = false;
        try {
            lockAcquired = redisLockService.lockForAppointment(userId, vaccineId);
            if (!lockAcquired) {
                throw new RuntimeException("System busy, please try again later");
            }

            // Check if user exists
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            // Check if vaccine exists and available
            Vaccine vaccine = vaccineRepository.findById(vaccineId)
                    .orElseThrow(() -> new RuntimeException("Vaccine not found with id: " + vaccineId));
            if (!vaccine.getAvailable()) {
                throw new RuntimeException("Vaccine is not available");
            }
            if (vaccine.getStockQuantity() <= 0) {
                throw new RuntimeException("Vaccine out of stock");
            }
            // Check for duplicate pending appointment
            if (hasPendingAppointment(userId, vaccineId)) {
                throw new RuntimeException("You already have a pending appointment for this vaccine");
            }
            // Create appointment
            Appointment appointment = new Appointment();
            appointment.setUser(user);
            appointment.setVaccine(vaccine);
            appointment.setAppointmentTime(appointmentTime);
            appointment.setStatus(0); // pending
            // Save appointment
            Appointment saved = appointmentRepository.save(appointment);
            // Decrease stock by 1
            vaccine.setStockQuantity(vaccine.getStockQuantity() - 1);
            vaccineRepository.save(vaccine);
            return saved;
        } finally {
            // Always release the lock
            if (lockAcquired) {
                redisLockService.unlockForAppointment(userId, vaccineId);
            }
        }
    }

    @Override
    public Appointment cancelAppointment(Long appointmentId, Long userId) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, userId)
                .orElseThrow(() -> new RuntimeException("Appointment not found or not owned by user"));
        if (appointment.getStatus() == 3) {
            throw new RuntimeException("Appointment already cancelled");
        }
        appointment.setStatus(3); // cancelled
        // Increase stock back
        Vaccine vaccine = appointment.getVaccine();
        vaccine.setStockQuantity(vaccine.getStockQuantity() + 1);
        vaccineRepository.save(vaccine);
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment cancelAppointmentByAdmin(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        if (appointment.getStatus() == 3) {
            throw new RuntimeException("Appointment already cancelled");
        }
        appointment.setStatus(3); // cancelled
        // Increase stock back
        Vaccine vaccine = appointment.getVaccine();
        vaccine.setStockQuantity(vaccine.getStockQuantity() + 1);
        vaccineRepository.save(vaccine);
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment confirmAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        if (appointment.getStatus() != 0) {
            throw new RuntimeException("Only pending appointments can be confirmed");
        }
        appointment.setStatus(1); // confirmed
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment completeAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        if (appointment.getStatus() != 1) {
            throw new RuntimeException("Only confirmed appointments can be completed");
        }
        appointment.setStatus(2); // completed
        return appointmentRepository.save(appointment);
    }

    @Override
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    @Override
    public List<Appointment> getAppointmentsByUser(Long userId) {
        return appointmentRepository.findByUserId(userId);
    }

    @Override
    public List<Appointment> getAppointmentsByVaccine(Long vaccineId) {
        return appointmentRepository.findByVaccineId(vaccineId);
    }

    @Override
    public List<Appointment> getPendingAppointments() {
        return appointmentRepository.findByStatus(0);
    }

    @Override
    public boolean hasPendingAppointment(Long userId, Long vaccineId) {
        List<Integer> pendingStatuses = Arrays.asList(0, 1); // pending and confirmed
        List<Appointment> appointments = appointmentRepository.findByUserAndVaccineAndStatusIn(userId, vaccineId, pendingStatuses);
        return !appointments.isEmpty();
    }
}