package com.springboot.vaccineappointmentsystem.repository;

import com.springboot.vaccineappointmentsystem.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByVaccineIdOrderByCreateTimeDesc(Long vaccineId);

    List<Review> findByUserIdOrderByCreateTimeDesc(Long userId);

    @Query("SELECT r FROM Review r JOIN FETCH r.vaccine JOIN FETCH r.user WHERE r.vaccine.id = :vaccineId ORDER BY r.createTime DESC")
    List<Review> findByVaccineIdWithUser(@Param("vaccineId") Long vaccineId);

    boolean existsByUserIdAndVaccineId(Long userId, Long vaccineId);
}
