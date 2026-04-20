package com.springboot.vaccineappointmentsystem.repository;

import com.springboot.vaccineappointmentsystem.entity.Appointment;
import com.springboot.vaccineappointmentsystem.entity.User;
import com.springboot.vaccineappointmentsystem.entity.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByUser(User user);
    List<Appointment> findByVaccine(Vaccine vaccine);
    List<Appointment> findByUserId(Long userId);
    List<Appointment> findByVaccineId(Long vaccineId);
    List<Appointment> findByStatus(Integer status);

    @Query("SELECT a FROM Appointment a WHERE a.user.id = :userId AND a.vaccine.id = :vaccineId AND a.status IN :statuses")
    List<Appointment> findByUserAndVaccineAndStatusIn(@Param("userId") Long userId, @Param("vaccineId") Long vaccineId, @Param("statuses") List<Integer> statuses);

    @Query("SELECT a FROM Appointment a WHERE a.user.id = :userId AND a.status IN :statuses")
    List<Appointment> findByUserAndStatusIn(@Param("userId") Long userId, @Param("statuses") List<Integer> statuses);

    Optional<Appointment> findByIdAndUserId(Long id, Long userId);
}