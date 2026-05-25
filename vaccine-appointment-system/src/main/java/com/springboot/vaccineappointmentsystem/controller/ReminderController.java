package com.springboot.vaccineappointmentsystem.controller;

import com.springboot.vaccineappointmentsystem.entity.VaccinationReminder;
import com.springboot.vaccineappointmentsystem.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    @Autowired
    private ReminderService reminderService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VaccinationReminder>> getAll(@PathVariable Long userId) {
        return ResponseEntity.ok(reminderService.getAllReminders(userId));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<VaccinationReminder>> getUnread(@PathVariable Long userId) {
        return ResponseEntity.ok(reminderService.getUnreadReminders(userId));
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Long>> countUnread(@PathVariable Long userId) {
        Map<String, Long> result = new HashMap<>();
        result.put("count", reminderService.countUnread(userId));
        return ResponseEntity.ok(result);
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = Long.valueOf(payload.get("userId").toString());
            Long vaccineId = Long.valueOf(payload.get("vaccineId").toString());
            reminderService.generateRemindersForUser(userId, vaccineId);
            Map<String, String> msg = new HashMap<>();
            msg.put("message", "提醒已生成");
            return ResponseEntity.ok(msg);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable Long id, @RequestBody Map<String, Long> payload) {
        try {
            Long userId = payload.get("userId");
            reminderService.markAsRead(id, userId);
            Map<String, String> msg = new HashMap<>();
            msg.put("message", "已标记已读");
            return ResponseEntity.ok(msg);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<?> markAllRead(@PathVariable Long userId) {
        reminderService.markAllAsRead(userId);
        Map<String, String> msg = new HashMap<>();
        msg.put("message", "全部已读");
        return ResponseEntity.ok(msg);
    }
}
