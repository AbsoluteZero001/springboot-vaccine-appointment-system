package com.springboot.vaccineappointmentsystem.controller;

import com.springboot.vaccineappointmentsystem.entity.Vaccine;
import com.springboot.vaccineappointmentsystem.service.VaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vaccines")
public class VaccineController {

    @Autowired
    private VaccineService vaccineService;

    @GetMapping
    public List<Vaccine> getAllVaccines() {
        return vaccineService.getAllVaccines();
    }

    @GetMapping("/available")
    public List<Vaccine> getAvailableVaccines() {
        return vaccineService.getAvailableVaccines();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVaccineById(@PathVariable Long id) {
        Optional<Vaccine> vaccineOpt = vaccineService.getVaccineById(id);
        if (vaccineOpt.isPresent()) {
            return ResponseEntity.ok(vaccineOpt.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Vaccine not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PostMapping
    public ResponseEntity<?> createVaccine(@RequestBody Vaccine vaccine) {
        try {
            Vaccine created = vaccineService.createVaccine(vaccine);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVaccine(@PathVariable Long id, @RequestBody Vaccine vaccineDetails) {
        try {
            Vaccine updated = vaccineService.updateVaccine(id, vaccineDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVaccine(@PathVariable Long id) {
        try {
            vaccineService.deleteVaccine(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Vaccine deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> payload) {
        Integer quantity = payload.get("quantity");
        if (quantity == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Quantity is required");
            return ResponseEntity.badRequest().body(error);
        }
        try {
            Vaccine updated = vaccineService.updateStock(id, quantity);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<?> toggleAvailability(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        Boolean available = payload.get("available");
        if (available == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Available flag is required");
            return ResponseEntity.badRequest().body(error);
        }
        try {
            Vaccine updated = vaccineService.toggleAvailability(id, available);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/search")
    public List<Vaccine> searchVaccines(@RequestParam String name) {
        return vaccineService.searchVaccinesByName(name);
    }
}