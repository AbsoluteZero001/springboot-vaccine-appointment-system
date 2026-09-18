package com.springboot.vaccineappointmentsystem.repository;

import com.springboot.vaccineappointmentsystem.entity.Appointment;
import com.springboot.vaccineappointmentsystem.entity.SysUser;
import com.springboot.vaccineappointmentsystem.entity.Vaccine;
import com.springboot.vaccineappointmentsystem.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByUser(SysUser user);
    List<Appointment> findByVaccine(Vaccine vaccine);
    List<Appointment> findByUserId(Long userId);

    @Query("SELECT a FROM Appointment a JOIN FETCH a.vaccine JOIN FETCH a.user LEFT JOIN FETCH a.familyMember WHERE a.user.id = :userId")
    List<Appointment> findByUserIdWithVaccine(@Param("userId") Long userId);

    @Query("SELECT a FROM Appointment a JOIN FETCH a.user JOIN FETCH a.vaccine LEFT JOIN FETCH a.familyMember WHERE a.vaccine.id = :vaccineId")
    List<Appointment> findByVaccineId(@Param("vaccineId") Long vaccineId);

    List<Appointment> findByStatus(AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.user.id = :userId AND a.vaccine.id = :vaccineId AND a.status IN :statuses")
    List<Appointment> findByUserAndVaccineAndStatusIn(@Param("userId") Long userId, @Param("vaccineId") Long vaccineId, @Param("statuses") List<AppointmentStatus> statuses);

    @Query("SELECT a FROM Appointment a WHERE a.user.id = :userId AND a.status IN :statuses")
    List<Appointment> findByUserAndStatusIn(@Param("userId") Long userId, @Param("statuses") List<AppointmentStatus> statuses);

    @Query("SELECT a FROM Appointment a JOIN FETCH a.user JOIN FETCH a.vaccine LEFT JOIN FETCH a.familyMember ORDER BY a.createTime DESC")
    List<Appointment> findAllWithDetails();

    @Query("SELECT a FROM Appointment a JOIN FETCH a.user JOIN FETCH a.vaccine LEFT JOIN FETCH a.familyMember WHERE a.status = :status ORDER BY a.createTime DESC")
    List<Appointment> findByStatusWithDetails(@Param("status") AppointmentStatus status);

    Optional<Appointment> findByIdAndUserId(Long id, Long userId);

    // Find overdue APPOINTED appointments without vaccination record (for MISSED detection)
    @Query("SELECT a FROM Appointment a WHERE a.status = :status AND a.appointmentTime < :now AND a.id NOT IN (SELECT r.appointment.id FROM VaccinationRecord r)")
    List<Appointment> findOverdueWithoutRecord(@Param("status") AppointmentStatus status, @Param("now") LocalDateTime now);

    long countByStatus(AppointmentStatus status);

    long countByCreateTimeBetween(LocalDateTime start, LocalDateTime end);

    long countByStatusAndCreateTimeBetween(AppointmentStatus status, LocalDateTime start, LocalDateTime end);
}
