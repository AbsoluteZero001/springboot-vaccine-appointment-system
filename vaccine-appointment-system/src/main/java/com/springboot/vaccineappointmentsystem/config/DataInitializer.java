package com.springboot.vaccineappointmentsystem.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        migrateAppointmentStatusCodes();

        boolean needsInit = false;
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM admin", Integer.class);
            if (count != null && count > 0) {
                log.info("数据库已初始化 ({} 条管理员记录)，检查疫苗数据完整性...", count);
                // Check if new vaccines need to be synced
                Integer vaccineCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vaccine", Integer.class);
                if (vaccineCount != null && vaccineCount < 46) {
                    log.info("检测到疫苗数据不完整 (当前 {} 条，预期 46 条)，执行增量同步...", vaccineCount);
                    needsInit = true;
                } else {
                    log.info("疫苗数据完整 ({} 条)，跳过同步。", vaccineCount);
                    return;
                }
            }
        } catch (Exception e) {
            log.info("管理员表不存在或为空，开始数据库初始化...");
            needsInit = true;
        }

        if (needsInit) {
            log.info("执行数据库种子数据同步...");
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("schema.sql"));
            populator.addScript(new ClassPathResource("data.sql"));
            populator.setContinueOnError(true);
            populator.execute(dataSource);
            log.info("数据库同步完成。");
        }
    }

    /**
     * Migrate appointment status codes to the final 4-state system.
     * Final: 0=APPOINTED, 1=COMPLETED, 2=NO_SHOW, 3=CANCELLED
     * <p>
     * Phase 1 (original system): 1(confirmed)→0, 2(completed)→1
     * Phase 2 (first-opt intermediate): 3(NO_RESPONSE)→2, 4(CANCELLED)→3
     */
    private void migrateAppointmentStatusCodes() {
        try {
            // Detect which phase(s) we need by checking for first-opt marker code 4
            Integer phase2Count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM appointment WHERE status = 4", Integer.class);
            boolean firstOptRan = phase2Count != null && phase2Count > 0;

            // Detect original system
            Integer phase1Count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM appointment WHERE status IN (1, 2, 3)", Integer.class);
            boolean originalSystem = phase1Count != null && phase1Count > 0;

            if (!originalSystem && !firstOptRan) {
                return; // Already migrated or no data
            }

            log.info("状态码迁移: phase1(原始系统)={}, phase2(中间版本)={}", originalSystem, firstOptRan);

            if (originalSystem && !firstOptRan) {
                // Phase 1: Original system → final 4-state
                // Remove duplicates: keep status=0 over status=1 for same user+vaccine
                jdbcTemplate.update(
                        "DELETE a1 FROM appointment a1 INNER JOIN appointment a2 " +
                                "ON a1.user_id = a2.user_id AND a1.vaccine_id = a2.vaccine_id " +
                                "WHERE a1.status = 1 AND a2.status = 0");
                int u = jdbcTemplate.update(
                        "UPDATE appointment SET status = 0, status_updated_at = NOW() WHERE status = 1");
                if (u > 0) log.info("  迁移 {} 条 confirmed → APPOINTED", u);
                u = jdbcTemplate.update(
                        "UPDATE appointment SET status = 1, status_updated_at = NOW() WHERE status = 2");
                if (u > 0) log.info("  迁移 {} 条 completed → COMPLETED", u);
                // Old cancelled(3) → new CANCELLED(3): same code, no-op
            }

            if (firstOptRan) {
                // Phase 2: First-opt intermediate → final 4-state
                // NO_RESPONSE(3) and MISSED(2) → NO_SHOW(2)
                // Remove duplicates where both status=3 and status=2 exist for same user+vaccine
                jdbcTemplate.update(
                        "DELETE a1 FROM appointment a1 INNER JOIN appointment a2 " +
                                "ON a1.user_id = a2.user_id AND a1.vaccine_id = a2.vaccine_id " +
                                "WHERE a1.status = 3 AND a2.status = 2");
                int u = jdbcTemplate.update(
                        "UPDATE appointment SET status = 2, status_updated_at = NOW() WHERE status = 3");
                if (u > 0) log.info("  迁移 {} 条 NO_RESPONSE → NO_SHOW", u);
                u = jdbcTemplate.update(
                        "UPDATE appointment SET status = 3, status_updated_at = NOW() WHERE status = 4");
                if (u > 0) log.info("  迁移 {} 条 CANCELLED(4) → CANCELLED(3)", u);
            }

            log.info("状态码迁移完成");
        } catch (Exception e) {
            log.warn("状态码迁移跳过: {}", e.getMessage());
        }
    }
}
