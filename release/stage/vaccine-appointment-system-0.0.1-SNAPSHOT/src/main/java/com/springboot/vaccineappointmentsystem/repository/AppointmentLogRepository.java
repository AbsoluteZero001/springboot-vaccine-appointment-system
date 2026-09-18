package com.springboot.vaccineappointmentsystem.repository;

import com.springboot.vaccineappointmentsystem.entity.AppointmentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentLogRepository extends JpaRepository<AppointmentLog, Long> {
    List<AppointmentLog> findByAppointmentIdOrderByCreatedAtDesc(Long appointmentId);
}
