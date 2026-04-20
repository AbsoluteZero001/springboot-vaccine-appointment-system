package com.springboot.vaccineappointmentsystem.service.impl;

import com.springboot.vaccineappointmentsystem.entity.Vaccine;
import com.springboot.vaccineappointmentsystem.repository.VaccineRepository;
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
        return vaccineRepository.save(vaccine);
    }

    @Override
    public void deleteVaccine(Long id) {
        vaccineRepository.deleteById(id);
    }

    @Override
    public Vaccine updateStock(Long id, Integer quantity) {
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