package com.springboot.vaccineappointmentsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "vaccination_reminder")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VaccinationReminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private SysUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_id", nullable = false)
    private Vaccine vaccine;

    @Column(nullable = false)
    private Integer doseNumber; // 第几针

    @Column(nullable = false)
    private Integer totalDoses; // 总针数

    @Column(nullable = false)
    private LocalDateTime reminderDate;

    @Column(nullable = false)
    private Boolean isRead = false;

    @Column(length = 200)
    private String message;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;
}
