package com.springboot.vaccineappointmentsystem.service.impl;

import com.springboot.vaccineappointmentsystem.entity.Vaccine;
import com.springboot.vaccineappointmentsystem.repository.VaccineRepository;
import com.springboot.vaccineappointmentsystem.service.FileStorageService;
import com.springboot.vaccineappointmentsystem.service.VaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VaccineServiceImpl implements VaccineService {

    @Autowired
    private VaccineRepository vaccineRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public List<Vaccine> getAllVaccines() {
        return vaccineRepository.findAll();
    }

    @Override
    public List<Vaccine> getAvailableVaccines() {
        return vaccineRepository.findByAvailableTrue();
    }

    @Override
    public Optional<Vaccine> getVaccineById(Long id) {
        return vaccineRepository.findById(id);
    }

    @Override
    public Vaccine createVaccine(Vaccine vaccine) {
        if (vaccine.getStockQuantity() == null) {
            vaccine.setStockQuantity(0);
        } else if (vaccine.getStockQuantity() < 0) {
            throw new RuntimeException("Stock quantity cannot be negative");
        }
        return vaccineRepository.save(vaccine);
    }

    @Override
    public Vaccine updateVaccine(Long id, Vaccine vaccineDetails) {
        Vaccine vaccine = vaccineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaccine not found with id: " + id));
        if (vaccineDetails.getName() != null) {
            vaccine.setName(vaccineDetails.getName());
        }
        if (vaccineDetails.getManufacturer() != null) {
            vaccine.setManufacturer(vaccineDetails.getManufacturer());
        }
        if (vaccineDetails.getDescription() != null) {
            vaccine.setDescription(vaccineDetails.getDescription());
        }
        if (vaccineDetails.getStockQuantity() != null) {
            vaccine.setStockQuantity(vaccineDetails.getStockQuantity());
        }
        if (vaccineDetails.getAvailable() != null) {
            vaccine.setAvailable(vaccineDetails.getAvailable());
        }
        if (vaccineDetails.getImageUrl() != null) {
            // Check if image URL is being changed
            String oldImageUrl = vaccine.getImageUrl();
            String newImageUrl = vaccineDetails.getImageUrl();
            if (!newImageUrl.equals(oldImageUrl)) {
                // Delete old image file if exists
                if (oldImageUrl != null) {
                    try {
                        String oldFilename = oldImageUrl.substring(oldImageUrl.lastIndexOf('/') + 1);
                        fileStorageService.deleteFile(oldFilename);
                    } catch (Exception e) {
                        System.err.println("Failed to delete old image file for vaccine " + id + ": " + e.getMessage());
                    }
                }
                vaccine.setImageUrl(newImageUrl);
            }
        }
        return vaccineRepository.save(vaccine);
    }

    @Override
    public void deleteVaccine(Long id) {
        Vaccine vaccine = vaccineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaccine not found with id: " + id));
        // Delete associated image file if exists
        if (vaccine.getImageUrl() != null) {
            try {
                // Extract filename from URL (format: /uploads/filename)
                String imageUrl = vaccine.getImageUrl();
                String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
                fileStorageService.deleteFile(filename);
            } catch (Exception e) {
                // Log warning but continue with deletion
                System.err.println("Failed to delete image file for vaccine " + id + ": " + e.getMessage());
            }
        }
        vaccineRepository.deleteById(id);
    }

    @Override
    public Vaccine updateStock(Long id, Integer quantity) {
        if (quantity < 0) {
            throw new RuntimeException("Stock quantity cannot be negative");
        }
        Vaccine vaccine = vaccineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaccine not found with id: " + id));
        vaccine.setStockQuantity(quantity);
        return vaccineRepository.save(vaccine);
    }

    @Override
    public Vaccine toggleAvailability(Long id, Boolean available) {
        Vaccine vaccine = vaccineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaccine not found with id: " + id));
        vaccine.setAvailable(available);
        return vaccineRepository.save(vaccine);
    }

    @Override
    public List<Vaccine> searchVaccinesByName(String name) {
        return vaccineRepository.findByNameContainingIgnoreCase(name);
    }
}