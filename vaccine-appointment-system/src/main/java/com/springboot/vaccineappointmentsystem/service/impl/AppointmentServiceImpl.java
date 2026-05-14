package com.springboot.vaccineappointmentsystem.service.impl;

import com.springboot.vaccineappointmentsystem.entity.*;
import com.springboot.vaccineappointmentsystem.enums.AppointmentStatus;
import com.springboot.vaccineappointmentsystem.enums.VaccinationRecordStatus;
import com.springboot.vaccineappointmentsystem.repository.*;
import com.springboot.vaccineappointmentsystem.service.AppointmentService;
import com.springboot.vaccineappointmentsystem.service.RedisLockService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private SysUserRepository sysUserRepository;
    @Autowired
    private VaccineRepository vaccineRepository;
    @Autowired
    private VaccinationRecordRepository vaccinationRecordRepository;
    @Autowired
    private AppointmentLogRepository appointmentLogRepository;
    @Autowired
    private RedisLockService redisLockService;
    @Autowired
    private EntityManager entityManager;

    // ── Stock helpers with optimistic-lock retry ──────────────────

    private static final int STOCK_RETRY_MAX = 3;

    private void decrementStock(Vaccine vaccine) {
        for (int i = 0; i < STOCK_RETRY_MAX; i++) {
            try {
                vaccine.setStockQuantity(vaccine.getStockQuantity() - 1);
                vaccineRepository.saveAndFlush(vaccine);
                return;
            } catch (OptimisticLockException e) {
                if (i == STOCK_RETRY_MAX - 1) throw new RuntimeException("库存更新冲突，请重试");
                entityManager.refresh(vaccine);
            }
        }
    }

    private void incrementStock(Vaccine vaccine) {
        for (int i = 0; i < STOCK_RETRY_MAX; i++) {
            try {
                vaccine.setStockQuantity(vaccine.getStockQuantity() + 1);
                vaccineRepository.saveAndFlush(vaccine);
                return;
            } catch (OptimisticLockException e) {
                if (i == STOCK_RETRY_MAX - 1) throw new RuntimeException("库存更新冲突，请重试");
                entityManager.refresh(vaccine);
            }
        }
    }

    // ── Audit helpers ────────────────────────────────────────────

    private void audit(Long appointmentId, String action, Integer oldCode, Integer newCode, String changedBy, String reason) {
        AppointmentLog entry = new AppointmentLog();
        entry.setAppointmentId(appointmentId);
        entry.setAction(action);
        entry.setOldStatus(oldCode);
        entry.setNewStatus(newCode);
        entry.setChangedBy(changedBy);
        entry.setChangeReason(reason);
        appointmentLogRepository.save(entry);
    }

    private void auditSystem(Long appointmentId, String action, Integer oldCode, Integer newCode, String reason) {
        audit(appointmentId, action, oldCode, newCode, "SYSTEM", reason);
    }

    // ── Business methods ─────────────────────────────────────────

    @Override
    public Appointment createAppointment(Long userId, Long vaccineId, LocalDateTime appointmentTime) {
        boolean lockAcquired = false;
        try {
            lockAcquired = redisLockService.lockForAppointment(userId, vaccineId);
            if (!lockAcquired) throw new RuntimeException("系统繁忙，请稍后再试");

            SysUser user = sysUserRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Vaccine vaccine = vaccineRepository.findById(vaccineId)
                    .orElseThrow(() -> new RuntimeException("Vaccine not found"));
            if (!vaccine.getAvailable()) throw new RuntimeException("疫苗不可用");
            if (vaccine.getStockQuantity() <= 0) throw new RuntimeException("疫苗库存不足");
            if (hasPendingAppointment(userId, vaccineId))
                throw new RuntimeException("您已有一个该疫苗的待处理预约");
            if (appointmentTime.isBefore(LocalDateTime.now()))
                throw new RuntimeException("无法预约过去的时间");

            Appointment appointment = new Appointment();
            appointment.setUser(user);
            appointment.setVaccine(vaccine);
            appointment.setAppointmentTime(appointmentTime);
            appointment.setStatus(AppointmentStatus.APPOINTED);
            appointment.setStatusUpdatedAt(LocalDateTime.now());
            Appointment saved = appointmentRepository.save(appointment);

            decrementStock(vaccine);

            audit(saved.getId(), "CREATE", null, AppointmentStatus.APPOINTED.getCode(),
                    user.getUsername(), "用户预约疫苗 " + vaccine.getName());
            return saved;
        } finally {
            if (lockAcquired) redisLockService.unlockForAppointment(userId, vaccineId);
        }
    }

    @Override
    public Appointment cancelAppointment(Long appointmentId, Long userId) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, userId)
                .orElseThrow(() -> new RuntimeException("Appointment not found or not owned by user"));
        return doCancel(appointment, appointment.getUser().getUsername());
    }

    @Override
    public Appointment cancelAppointmentByAdmin(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        return doCancel(appointment, "ADMIN");
    }

    private void validateCancellable(Appointment appointment) {
        AppointmentStatus cur = appointment.getStatus();
        if (cur == AppointmentStatus.CANCELLED) throw new RuntimeException("预约已被取消");
        if (!cur.canTransitionTo(AppointmentStatus.CANCELLED))
            throw new RuntimeException("无法取消：预约状态为 " + cur.getDisplayName());
    }

    private Appointment doCancel(Appointment appointment, String operator) {
        validateCancellable(appointment);
        int oldCode = appointment.getStatus().getCode();
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setStatusUpdatedAt(LocalDateTime.now());
        Vaccine vaccine = appointment.getVaccine();
        incrementStock(vaccine);
        Appointment saved = appointmentRepository.save(appointment);
        audit(appointment.getId(), "CANCEL", oldCode, AppointmentStatus.CANCELLED.getCode(),
                operator, "取消预约，库存已恢复");
        return saved;
    }

    @Override
    @Transactional
    public Appointment completeAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        AppointmentStatus cur = appointment.getStatus();
        if (!cur.canTransitionTo(AppointmentStatus.COMPLETED))
            throw new RuntimeException("无法完成：预约状态为 " + cur.getDisplayName());

        int oldCode = cur.getCode();

        // Ensure vaccination record exists (idempotent)
        List<VaccinationRecord> existing = vaccinationRecordRepository.findByAppointmentId(appointmentId);
        if (existing.isEmpty()) {
            VaccinationRecord record = new VaccinationRecord();
            record.setAppointment(appointment);
            record.setUser(appointment.getUser());
            record.setVaccine(appointment.getVaccine());
            record.setVaccinationTime(LocalDateTime.now());
            record.setStatus(VaccinationRecordStatus.ADMINISTERED);
            record.setNotes("管理员完成接种");
            try {
                vaccinationRecordRepository.saveAndFlush(record);
            } catch (Exception e) {
                log.warn("创建接种记录失败 (可能已存在): {}", e.getMessage());
                // If duplicate, load the existing record and update its status
                List<VaccinationRecord> retry = vaccinationRecordRepository.findByAppointmentId(appointmentId);
                if (retry.isEmpty()) throw new RuntimeException("无法创建接种记录: " + e.getMessage());
                VaccinationRecord existingRecord = retry.get(0);
                existingRecord.setStatus(VaccinationRecordStatus.ADMINISTERED);
                existingRecord.setNotes("管理员完成接种");
                vaccinationRecordRepository.save(existingRecord);
            }
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setStatusUpdatedAt(LocalDateTime.now());
        Appointment saved = appointmentRepository.saveAndFlush(appointment);
        audit(appointmentId, "COMPLETE", oldCode, AppointmentStatus.COMPLETED.getCode(),
                "ADMIN", "管理员确认接种完成");
        log.info("预约 {} 已完成接种，状态已更新为 COMPLETED", appointmentId);
        return saved;
    }

    @Override
    public VaccinationRecord createLateRecord(Long appointmentId, String notes) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (appointment.getStatus() != AppointmentStatus.NO_SHOW)
            throw new RuntimeException("只有爽约的预约才能补录接种记录，当前状态: "
                    + appointment.getStatus().getDisplayName());

        // Check duplicate
        List<VaccinationRecord> existing = vaccinationRecordRepository.findByAppointmentId(appointmentId);
        if (!existing.isEmpty())
            throw new RuntimeException("该预约已有接种记录");

        Vaccine vaccine = appointment.getVaccine();
        if (vaccine.getStockQuantity() <= 0)
            throw new RuntimeException("疫苗库存不足，无法创建补录接种记录");
        decrementStock(vaccine);

        VaccinationRecord record = new VaccinationRecord();
        record.setAppointment(appointment);
        record.setUser(appointment.getUser());
        record.setVaccine(vaccine);
        record.setVaccinationTime(LocalDateTime.now());
        record.setStatus(VaccinationRecordStatus.ADMINISTERED);
        record.setNotes(notes != null ? notes : "补录接种记录（逾期补录）");
        VaccinationRecord saved = vaccinationRecordRepository.save(record);

        audit(appointmentId, "LATE_RECORD", AppointmentStatus.NO_SHOW.getCode(), null,
                "ADMIN", "补录逾期接种记录，库存已扣减: " + (notes != null ? notes : "无备注"));
        log.info("Late vaccination record created for NO_SHOW appointment {}, stock deducted", appointmentId);
        return saved;
    }

    @Override
    public void detectAndMarkNoShow() {
        List<Appointment> overdue = appointmentRepository
                .findOverdueWithoutRecord(AppointmentStatus.APPOINTED, LocalDateTime.now());
        for (Appointment a : overdue) {
            int oldCode = a.getStatus().getCode();
            a.setStatus(AppointmentStatus.NO_SHOW);
            a.setStatusUpdatedAt(LocalDateTime.now());
            appointmentRepository.save(a);

            Vaccine vaccine = a.getVaccine();
            incrementStock(vaccine);

            auditSystem(a.getId(), "AUTO_NO_SHOW", oldCode, AppointmentStatus.NO_SHOW.getCode(),
                    "系统自动检测：预约时间 " + a.getAppointmentTime() + " 已过，未到场接种，库存已恢复");
            log.info("Marked appointment {} as NO_SHOW (was scheduled for {}), stock restored", a.getId(), a.getAppointmentTime());
        }
    }

    @Override
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    @Override
    public List<Appointment> getAppointmentsByUser(Long userId) {
        return appointmentRepository.findByUserIdWithVaccine(userId);
    }

    @Override
    public List<Appointment> getAppointmentsByVaccine(Long vaccineId) {
        return appointmentRepository.findByVaccineId(vaccineId);
    }

    @Override
    public List<Appointment> getPendingAppointments() {
        return appointmentRepository.findByStatus(AppointmentStatus.APPOINTED);
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAllWithDetails();
    }

    @Override
    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatusWithDetails(status);
    }

    @Override
    public boolean hasPendingAppointment(Long userId, Long vaccineId) {
        return !appointmentRepository.findByUserAndVaccineAndStatusIn(
                userId, vaccineId, Collections.singletonList(AppointmentStatus.APPOINTED)).isEmpty();
    }

    @Override
    public List<AppointmentLog> getAppointmentLogs(Long appointmentId) {
        return appointmentLogRepository.findByAppointmentIdOrderByCreatedAtDesc(appointmentId);
    }
}
