package com.springboot.vaccineappointmentsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "vaccine")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Vaccine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String manufacturer;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer stockQuantity = 0;

    @Column(nullable = false)
    private Boolean available = true;

    @Column(length = 255)
    private String imageUrl;

    @Column(length = 50)
    private String category;

    @Column(length = 100)
    private String brand;

    @Column(length = 50)
    private String dosage;

    @Column(length = 100)
    private String technique;

    @Column(columnDefinition = "TEXT")
    private String scheduleInfo;

    @Column
    private Integer dosesRequired;

    @Column(length = 100)
    private String ageRange;

    @Column(length = 200)
    private String targetDisease;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateTime;
}