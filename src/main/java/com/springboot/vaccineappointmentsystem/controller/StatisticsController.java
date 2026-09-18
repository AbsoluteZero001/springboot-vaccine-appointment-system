package com.springboot.vaccineappointmentsystem.controller;

import com.springboot.vaccineappointmentsystem.dto.AppointmentStatistics;
import com.springboot.vaccineappointmentsystem.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppointmentStatistics> getStatistics() {
        return ResponseEntity.ok(statisticsService.getOverallStatistics());
    }
}
