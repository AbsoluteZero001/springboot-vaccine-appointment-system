package com.springboot.vaccineappointmentsystem.repository;

import com.springboot.vaccineappointmentsystem.entity.VaccinationReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VaccinationReminderRepository extends JpaRepository<VaccinationReminder, Long> {
    List<VaccinationReminder> findByUserIdOrderByCreateTimeDesc(Long userId);

    List<VaccinationReminder> findByUserIdAndIsReadFalseOrderByReminderDateAsc(Long userId);

    long countByUserIdAndIsReadFalse(Long userId);

    boolean existsByUserIdAndVaccineIdAndDoseNumber(Long userId, Long vaccineId, Integer doseNumber);

    List<VaccinationReminder> findByReminderDateBeforeAndIsReadFalse(LocalDateTime date);
}
