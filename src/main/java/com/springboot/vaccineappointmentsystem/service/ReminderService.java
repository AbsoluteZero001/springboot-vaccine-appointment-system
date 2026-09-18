package com.springboot.vaccineappointmentsystem.service;

import com.springboot.vaccineappointmentsystem.entity.VaccinationReminder;

import java.util.List;

public interface ReminderService {
    void generateRemindersForUser(Long userId, Long vaccineId);

    void markAsRead(Long reminderId, Long userId);

    void markAllAsRead(Long userId);

    List<VaccinationReminder> getUnreadReminders(Long userId);

    List<VaccinationReminder> getAllReminders(Long userId);

    long countUnread(Long userId);

    void sendReminders();
}
