package com.springboot.vaccineappointmentsystem.repository;

import com.springboot.vaccineappointmentsystem.entity.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineRepository extends JpaRepository<Vaccine, Long> {
    List<Vaccine> findByAvailableTrue();
    List<Vaccine> findByAvailableTrueAndStockQuantityGreaterThan(Integer stockQuantity);
    List<Vaccine> findByNameContainingIgnoreCase(String name);
}