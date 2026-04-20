package com.springboot.vaccineappointmentsystem.controller;

import com.springboot.vaccineappointmentsystem.entity.VaccinationRecord;
import com.springboot.vaccineappointmentsystem.service.VaccinationRecordService;
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
@RequestMapping("/api/vaccination-records")
public class VaccinationRecordController {

    @Autowired
    private VaccinationRecordService vaccinationRecordService;

    @PostMapping
    public ResponseEntity<?> createRecord(@RequestBody Map<String, Object> payload) {
        try {
            Long appointmentId = Long.valueOf(payload.get("appointmentId").toString());
            String vaccinationTimeStr = payload.get("vaccinationTime").toString();
            LocalDateTime vaccinationTime = LocalDateTime.parse(vaccinationTimeStr);
            String notes = (String) payload.get("notes");
            VaccinationRecord record = vaccinationRecordService.createRecord(appointmentId, vaccinationTime, notes);
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
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
    public List<VaccinationRecord> getRecordsByUser(@PathVariable Long userId) {
        return vaccinationRecordService.getRecordsByUser(userId);
    }

    @GetMapping("/vaccine/{vaccineId}")
    public List<VaccinationRecord> getRecordsByVaccine(@PathVariable Long vaccineId) {
        return vaccinationRecordService.getRecordsByVaccine(vaccineId);
    }

    @GetMapping("/status/{status}")
    public List<VaccinationRecord> getRecordsByStatus(@PathVariable Integer status) {
        return vaccinationRecordService.getRecordsByStatus(status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecordById(@PathVariable Long id) {
        Optional<VaccinationRecord> recordOpt = vaccinationRecordService.getRecordById(id);
        if (recordOpt.isPresent()) {
            return ResponseEntity.ok(recordOpt.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Vaccination record not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecord(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            LocalDateTime vaccinationTime = payload.get("vaccinationTime") != null ?
                    LocalDateTime.parse(payload.get("vaccinationTime").toString()) : null;
            String notes = (String) payload.get("notes");
            Integer status = payload.get("status") != null ?
                    Integer.valueOf(payload.get("status").toString()) : null;
            VaccinationRecord updated = vaccinationRecordService.updateRecord(id, vaccinationTime, notes, status);
            return ResponseEntity.ok(updated);
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

    @PostMapping("/{id}/administer")
    public ResponseEntity<?> markAsAdministered(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            LocalDateTime actualTime = payload.get("actualTime") != null ?
                    LocalDateTime.parse(payload.get("actualTime").toString()) : null;
            String notes = (String) payload.get("notes");
            VaccinationRecord updated = vaccinationRecordService.markAsAdministered(id, actualTime, notes);
            return ResponseEntity.ok(updated);
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
}