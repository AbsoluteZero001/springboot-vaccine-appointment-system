package com.springboot.vaccineappointmentsystem.dto;

import lombok.Data;

@Data
public class AppointmentStatistics {
    private long totalAppointments;
    private long appointedCount;
    private long completedCount;
    private long noShowCount;
    private long cancelledCount;
    private double vaccinationRate;
    private double appointmentSuccessRate;
    private String period = "ALL";
}
