package com.springboot.vaccineappointmentsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.springboot.vaccineappointmentsystem.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Appointment {
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
    private LocalDateTime appointmentTime;

    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.APPOINTED;

    @Column(nullable = false)
    private Integer paymentStatus = 0; // 0=未支付 1=已支付 2=已退款

    @Column
    private LocalDateTime paymentTime;

    @Column(length = 500)
    private String remark;

    @Column
    private LocalDateTime statusUpdatedAt = LocalDateTime.now();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateTime;
}
