package com.springboot.vaccineappointmentsystem.repository;

import com.springboot.vaccineappointmentsystem.entity.VaccinationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccinationRecordRepository extends JpaRepository<VaccinationRecord, Long> {
    List<VaccinationRecord> findByUserId(Long userId);

    @Query("SELECT r FROM VaccinationRecord r JOIN FETCH r.vaccine WHERE r.user.id = :userId")
    List<VaccinationRecord> findByUserIdWithVaccine(@Param("userId") Long userId);

    List<VaccinationRecord> findByVaccineId(Long vaccineId);
    List<VaccinationRecord> findByStatus(Integer status);
    List<VaccinationRecord> findByAppointmentId(Long appointmentId);
}