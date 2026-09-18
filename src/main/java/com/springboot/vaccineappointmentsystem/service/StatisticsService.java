package com.springboot.vaccineappointmentsystem.service;

import com.springboot.vaccineappointmentsystem.dto.AppointmentStatistics;

public interface StatisticsService {
    AppointmentStatistics getOverallStatistics();
}
