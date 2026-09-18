package com.springboot.vaccineappointmentsystem.controller;

import com.springboot.vaccineappointmentsystem.entity.Vaccine;
import com.springboot.vaccineappointmentsystem.service.FileStorageService;
import com.springboot.vaccineappointmentsystem.service.VaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vaccines")
public class VaccineController {

    @Autowired
    private VaccineService vaccineService;

    @Autowired
    private FileStorageService fileStorageService;

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
            error.put("error", "疫苗未找到");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteVaccine(@PathVariable Long id) {
        try {
            vaccineService.deleteVaccine(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "疫苗删除成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> payload) {
        Integer quantity = payload.get("quantity");
        if (quantity == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "数量不能为空");
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleAvailability(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        Boolean available = payload.get("available");
        if (available == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "可用标志不能为空");
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

    @PostMapping(value = "/{id}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadVaccineImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "文件为空");
                return ResponseEntity.badRequest().body(error);
            }
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "只允许上传图片文件");
                return ResponseEntity.badRequest().body(error);
            }
            // Store file and get URL
            String imageUrl = fileStorageService.storeFile(file);
            // Update vaccine with image URL
            Vaccine vaccine = vaccineService.getVaccineById(id)
                    .orElseThrow(() -> new RuntimeException("疫苗未找到"));
            vaccine.setImageUrl(imageUrl);
            Vaccine updated = vaccineService.updateVaccine(id, vaccine);
            Map<String, Object> response = new HashMap<>();
            response.put("vaccine", updated);
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "图片上传失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}