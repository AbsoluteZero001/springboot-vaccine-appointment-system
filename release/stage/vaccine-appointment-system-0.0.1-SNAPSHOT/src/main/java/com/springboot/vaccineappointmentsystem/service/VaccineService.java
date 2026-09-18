package com.springboot.vaccineappointmentsystem.service;

import com.springboot.vaccineappointmentsystem.entity.Vaccine;

import java.util.List;
import java.util.Optional;

public interface VaccineService {
    List<Vaccine> getAllVaccines();
    List<Vaccine> getAvailableVaccines();
    Optional<Vaccine> getVaccineById(Long id);
    Vaccine createVaccine(Vaccine vaccine);
    Vaccine updateVaccine(Long id, Vaccine vaccineDetails);
    void deleteVaccine(Long id);
    Vaccine updateStock(Long id, Integer quantity);
    Vaccine toggleAvailability(Long id, Boolean available);
    List<Vaccine> searchVaccinesByName(String name);
}