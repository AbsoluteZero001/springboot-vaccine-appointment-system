package com.springboot.vaccineappointmentsystem.service.impl;

import com.springboot.vaccineappointmentsystem.dto.AppointmentStatistics;
import com.springboot.vaccineappointmentsystem.enums.AppointmentStatus;
import com.springboot.vaccineappointmentsystem.repository.AppointmentRepository;
import com.springboot.vaccineappointmentsystem.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public AppointmentStatistics getOverallStatistics() {
        AppointmentStatistics stats = new AppointmentStatistics();

        long total = appointmentRepository.count();
        long completed = appointmentRepository.countByStatus(AppointmentStatus.COMPLETED);
        long appointed = appointmentRepository.countByStatus(AppointmentStatus.APPOINTED);
        long noShow = appointmentRepository.countByStatus(AppointmentStatus.NO_SHOW);
        long cancelled = appointmentRepository.countByStatus(AppointmentStatus.CANCELLED);

        stats.setTotalAppointments(total);
        stats.setCompletedCount(completed);
        stats.setAppointedCount(appointed);
        stats.setNoShowCount(noShow);
        stats.setCancelledCount(cancelled);

        // Vaccination rate = completed / (completed + noShow) * 100
        long resolvedTotal = completed + noShow;
        if (resolvedTotal > 0) {
            stats.setVaccinationRate(Math.round((double) completed / resolvedTotal * 10000.0) / 100.0);
        }

        // Appointment success rate = completed / total * 100
        if (total > 0) {
            stats.setAppointmentSuccessRate(Math.round((double) completed / total * 10000.0) / 100.0);
        }

        return stats;
    }
}
