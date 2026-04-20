package com.springboot.vaccineappointmentsystem.controller;

import com.springboot.vaccineappointmentsystem.entity.Appointment;
import com.springboot.vaccineappointmentsystem.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = Long.valueOf(payload.get("userId").toString());
            Long vaccineId = Long.valueOf(payload.get("vaccineId").toString());
            String appointmentTimeStr = payload.get("appointmentTime").toString();
            LocalDateTime appointmentTime = LocalDateTime.parse(appointmentTimeStr);
            Appointment appointment = appointmentService.createAppointment(userId, vaccineId, appointmentTime);
            return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid request format");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/user/{userId}")
    public List<Appointment> getAppointmentsByUser(@PathVariable Long userId) {
        return appointmentService.getAppointmentsByUser(userId);
    }

    @GetMapping("/vaccine/{vaccineId}")
    public List<Appointment> getAppointmentsByVaccine(@PathVariable Long vaccineId) {
        return appointmentService.getAppointmentsByVaccine(vaccineId);
    }

    @GetMapping("/pending")
    public List<Appointment> getPendingAppointments() {
        return appointmentService.getPendingAppointments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long id) {
        Optional<Appointment> appointmentOpt = appointmentService.getAppointmentById(id);
        if (appointmentOpt.isPresent()) {
            return ResponseEntity.ok(appointmentOpt.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Appointment not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id, @RequestBody Map<String, Long> payload) {
        Long userId = payload.get("userId");
        if (userId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "userId is required");
            return ResponseEntity.badRequest().body(error);
        }
        try {
            Appointment appointment = appointmentService.cancelAppointment(id, userId);
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirmAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.confirmAppointment(id);
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.completeAppointment(id);
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}