package com.springboot.vaccineappointmentsystem.service.impl;

import com.springboot.vaccineappointmentsystem.service.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OverdueDetectionService {

    private static final Logger log = LoggerFactory.getLogger(OverdueDetectionService.class);

    @Autowired
    private AppointmentService appointmentService;

    @Scheduled(cron = "0 */5 * * * *")
    public void detectOverdueAppointments() {
        log.debug("Running overdue appointment detection...");
        try {
            appointmentService.detectAndMarkNoShow();
        } catch (Exception e) {
            log.error("Error during overdue appointment detection", e);
        }
    }
}
