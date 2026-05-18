package com.springboot.vaccineappointmentsystem.controller;

import com.springboot.vaccineappointmentsystem.entity.Appointment;
import com.springboot.vaccineappointmentsystem.entity.AppointmentLog;
import com.springboot.vaccineappointmentsystem.entity.VaccinationRecord;
import com.springboot.vaccineappointmentsystem.enums.AppointmentStatus;
import com.springboot.vaccineappointmentsystem.repository.AppointmentRepository;
import com.springboot.vaccineappointmentsystem.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    private AppointmentRepository appointmentRepository;

    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = Long.valueOf(payload.get("userId").toString());
            Long vaccineId = Long.valueOf(payload.get("vaccineId").toString());
            LocalDateTime appointmentTime = LocalDateTime.parse(payload.get("appointmentTime").toString());
            Appointment appointment = appointmentService.createAppointment(userId, vaccineId, appointmentTime);
            return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "请求格式无效");
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

    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/pending")
    public List<Appointment> getPendingAppointments() {
        return appointmentService.getPendingAppointments();
    }

    @GetMapping("/status/{status}")
    public List<Appointment> getAppointmentsByStatus(@PathVariable Integer status) {
        return appointmentService.getAppointmentsByStatus(AppointmentStatus.fromCode(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long id) {
        Optional<Appointment> appointmentOpt = appointmentService.getAppointmentById(id);
        if (appointmentOpt.isPresent()) {
            return ResponseEntity.ok(appointmentOpt.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "预约未找到");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // ── Audit log ──────────────────────────────────────────────

    @GetMapping("/{id}/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AppointmentLog> getAppointmentLogs(@PathVariable Long id) {
        return appointmentService.getAppointmentLogs(id);
    }

    // ── Actions ────────────────────────────────────────────────

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id, @RequestBody Map<String, Long> payload) {
        Long userId = payload.get("userId");
        if (userId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "用户ID不能为空");
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

    @PostMapping("/{id}/cancel/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cancelAppointmentByAdmin(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.cancelAppointmentByAdmin(id);
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('ADMIN')")
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

    @PostMapping("/{id}/late-record")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createLateRecord(@PathVariable Long id, @RequestBody(required = false) Map<String, String> payload) {
        try {
            String notes = payload != null ? payload.get("notes") : null;
            VaccinationRecord record = appointmentService.createLateRecord(id, notes);
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ── Payment ─────────────────────────────────────────────────

    @PostMapping("/{id}/pay")
    public ResponseEntity<?> payAppointment(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            Long userId = Long.valueOf(payload.get("userId").toString());
            String remark = payload.get("remark") != null ? (String) payload.get("remark") : null;
            Appointment appointment = appointmentService.getAppointmentById(id)
                    .orElseThrow(() -> new RuntimeException("预约未找到"));
            if (!appointment.getUser().getId().equals(userId)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "无权操作此预约");
                return ResponseEntity.badRequest().body(error);
            }
            if (appointment.getPaymentStatus() == 1) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "该预约已支付");
                return ResponseEntity.badRequest().body(error);
            }
            appointment.setPaymentStatus(1);
            appointment.setPaymentTime(java.time.LocalDateTime.now());
            if (remark != null) {
                appointment.setRemark(remark);
            }
            Appointment saved = appointmentRepository.save(appointment);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
