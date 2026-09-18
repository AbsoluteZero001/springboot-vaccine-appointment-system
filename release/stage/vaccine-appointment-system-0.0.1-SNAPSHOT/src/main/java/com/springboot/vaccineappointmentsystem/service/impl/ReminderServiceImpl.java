package com.springboot.vaccineappointmentsystem.service.impl;

import com.springboot.vaccineappointmentsystem.entity.*;
import com.springboot.vaccineappointmentsystem.repository.*;
import com.springboot.vaccineappointmentsystem.service.ReminderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReminderServiceImpl implements ReminderService {

    private static final Logger log = LoggerFactory.getLogger(ReminderServiceImpl.class);

    @Autowired
    private VaccinationReminderRepository reminderRepository;
    @Autowired
    private VaccinationRecordRepository vaccinationRecordRepository;
    @Autowired
    private VaccineRepository vaccineRepository;
    @Autowired
    private SysUserRepository sysUserRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public void generateRemindersForUser(Long userId, Long vaccineId) {
        Vaccine vaccine = vaccineRepository.findById(vaccineId)
                .orElseThrow(() -> new RuntimeException("疫苗不存在"));

        if (vaccine.getDosesRequired() == null || vaccine.getDosesRequired() <= 1) return;

        int totalDoses = vaccine.getDosesRequired();
        // Count how many doses this user has already taken for this vaccine
        List<VaccinationRecord> records = vaccinationRecordRepository.findByUserId(userId);
        long completedDoses = records.stream()
                .filter(r -> r.getVaccine().getId().equals(vaccineId) && r.getStatus().getCode() == 1)
                .count();

        int nextDose = (int) completedDoses + 1;
        if (nextDose > totalDoses) return; // All doses completed

        // Don't create duplicate reminders
        if (reminderRepository.existsByUserIdAndVaccineIdAndDoseNumber(userId, vaccineId, nextDose))
            return;

        // Calculate next dose date based on vaccine scheduleInfo
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderDate = now.plusDays(28); // default 4 weeks

        VaccinationReminder reminder = new VaccinationReminder();
        reminder.setUser(sysUserRepository.findById(userId).orElseThrow());
        reminder.setVaccine(vaccine);
        reminder.setDoseNumber(nextDose);
        reminder.setTotalDoses(totalDoses);
        reminder.setReminderDate(reminderDate);
        reminder.setMessage("您接种的" + vaccine.getName() + "第" + nextDose + "针（共" + totalDoses + "针）需要预约接种");
        reminderRepository.save(reminder);

        log.info("Generated reminder for user {} - {} dose {}/{}", userId, vaccine.getName(), nextDose, totalDoses);
    }

    @Override
    public void markAsRead(Long reminderId, Long userId) {
        VaccinationReminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("提醒不存在"));
        if (!reminder.getUser().getId().equals(userId))
            throw new RuntimeException("无权操作");
        reminder.setIsRead(true);
        reminderRepository.save(reminder);
    }

    @Override
    public void markAllAsRead(Long userId) {
        List<VaccinationReminder> unread = reminderRepository.findByUserIdAndIsReadFalseOrderByReminderDateAsc(userId);
        for (VaccinationReminder r : unread) {
            r.setIsRead(true);
        }
        reminderRepository.saveAll(unread);
    }

    @Override
    public List<VaccinationReminder> getUnreadReminders(Long userId) {
        return reminderRepository.findByUserIdAndIsReadFalseOrderByReminderDateAsc(userId);
    }

    @Override
    public List<VaccinationReminder> getAllReminders(Long userId) {
        return reminderRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    @Override
    public long countUnread(Long userId) {
        return reminderRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    @Scheduled(cron = "0 0 9 * * *") // Daily at 9am
    public void sendReminders() {
        log.info("Running scheduled reminder check...");
        try {
            // Check for multi-dose vaccines and create reminders after vaccination
            List<VaccinationRecord> recentRecords = vaccinationRecordRepository.findAll();
            LocalDateTime cutoff = LocalDateTime.now().minusDays(1);
            for (VaccinationRecord record : recentRecords) {
                if (record.getCreateTime() != null && record.getCreateTime().isAfter(cutoff)
                        && record.getStatus().getCode() == 1) {
                    try {
                        generateRemindersForUser(record.getUser().getId(), record.getVaccine().getId());
                    } catch (Exception e) {
                        log.debug("Skip reminder for record {}: {}", record.getId(), e.getMessage());
                    }
                }
            }

            // Log reminders due within 3 days
            List<VaccinationReminder> dueReminders = reminderRepository
                    .findByReminderDateBeforeAndIsReadFalse(LocalDateTime.now().plusDays(3));
            log.info("{} reminders are due within 3 days", dueReminders.size());
        } catch (Exception e) {
            log.error("Error in scheduled reminder check", e);
        }
    }
}
