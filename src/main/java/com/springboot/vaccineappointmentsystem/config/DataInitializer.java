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
        dropTypeColumnIfExists();
        migrateRoleColumn();
        seedAdminIfEmpty();
        seedVaccinesIfEmpty();
        seedVaccinePrices();
    }

    /**
     * Remove the stale 'type' column leftover from the old schema.
     * Hibernate ddl-auto:update only adds columns, never drops them.
     */
    private void dropTypeColumnIfExists() {
        try {
            jdbcTemplate.execute("ALTER TABLE sys_user DROP COLUMN type");
            log.info("已删除 sys_user 表中废弃的 type 列");
        } catch (Exception ignored) {
            // Column doesn't exist — nothing to do
        }
    }

    /**
     * After Hibernate ddl-auto:update adds the 'role' column, existing rows have role=NULL.
     * Fix them so role-based auth works correctly.
     */
    private void migrateRoleColumn() {
        try {
            // Check if role column exists (it might not if this is the first run)
            jdbcTemplate.queryForObject("SELECT role FROM sys_user LIMIT 1", String.class);
        } catch (Exception e) {
            return; // role column doesn't exist yet — Hibernate hasn't added it
        }
        int updated = jdbcTemplate.update(
                "UPDATE sys_user SET role = 'ROLE_ADMIN' WHERE username = 'admin' AND (role IS NULL OR role = '')");
        if (updated > 0) {
            log.info("已将 admin 的 role 迁移为 ROLE_ADMIN");
        }
        int usersFixed = jdbcTemplate.update(
                "UPDATE sys_user SET role = 'ROLE_USER' WHERE role IS NULL OR role = ''");
        if (usersFixed > 0) {
            log.info("已将 {} 个用户的 role 迁移为 ROLE_USER", usersFixed);
        }
    }

    private void seedAdminIfEmpty() {
        try {
            Long count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM sys_user WHERE role = 'ROLE_ADMIN'", Long.class);
            if (count != null && count > 0) {
                log.info("管理员账户已存在 ({} 个)，跳过初始化", count);
                return;
            }
        } catch (Exception e) {
            log.info("sys_user 表不存在或为空，开始初始化管理员账户...");
        }

        try {
            jdbcTemplate.update(
                    "INSERT IGNORE INTO sys_user (username, password, phone, role, status, create_time, update_time) " +
                            "VALUES ('admin', '$2b$10$o1DD40tNdnPaRQ0hW8pbT.l5/Ao3/EtvOcHU9p0rrpp/fiD/ST3Uq', '13800000000', 'ROLE_ADMIN', 1, NOW(), NOW())");
            log.info("默认管理员账户已创建: admin / admin123");
        } catch (Exception e) {
            log.warn("管理员账户创建失败: {}", e.getMessage());
        }
    }

    private void seedVaccinesIfEmpty() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM vaccine", Integer.class);
            if (count != null && count >= 46) {
                log.info("疫苗数据完整 ({} 种)，跳过初始化", count);
                return;
            }
            log.info("疫苗数据不完整 (当前 {} 种)，执行同步...", count);
        } catch (Exception e) {
            log.info("vaccine 表不存在或为空，开始初始化疫苗数据...");
        }

        try {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("database/data-vaccines.sql"));
            populator.setContinueOnError(false);
            populator.execute(dataSource);
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vaccine", Integer.class);
            if (count != null && count > 0) {
                log.info("疫苗数据同步完成，当前共 {} 种疫苗", count);
            } else {
                log.error("疫苗数据同步失败：数据文件执行后疫苗表仍为空，请检查 data-vaccines.sql");
            }
        } catch (Exception e) {
            log.error("疫苗数据初始化失败: {}", e.getMessage(), e);
        }
    }

    private void seedVaccinePrices() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM vaccine WHERE price IS NOT NULL", Integer.class);
            if (count != null && count >= 46) {
                return; // all prices already set
            }
            log.info("初始化疫苗价格...");
            Object[][] prices = {
                    {1, 128.00}, {2, 168.00}, {3, 118.00}, {4, 158.00}, {5, 198.00},
                    {6, 248.00}, {7, 298.00}, {8, 3980.00}, {9, 2480.00}, {10, 980.00},
                    {11, 168.00}, {12, 128.00}, {13, 328.00}, {14, 728.00}, {15, 1628.00},
                    {16, 0.00}, {17, 288.00}, {18, 168.00}, {19, 158.00}, {20, 0.00},
                    {21, 0.00}, {22, 0.00}, {23, 0.00}, {24, 0.00}, {25, 0.00},
                    {26, 0.00}, {27, 0.00}, {28, 158.00}, {29, 198.00}, {30, 328.00},
                    {31, 268.00}, {32, 628.00}, {33, 258.00}, {34, 198.00}, {35, 1388.00},
                    {36, 0.00}, {37, 0.00}, {38, 188.00}, {39, 0.00}, {40, 1280.00},
                    {41, 148.00}, {42, 248.00}, {43, 288.00}, {44, 0.00}, {45, 0.00},
                    {46, 398.00}
            };
            for (Object[] p : prices) {
                jdbcTemplate.update("UPDATE vaccine SET price = ? WHERE id = ?", p[1], p[0]);
            }
            log.info("疫苗价格初始化完成");
        } catch (Exception e) {
            log.warn("疫苗价格初始化跳过: {}", e.getMessage());
        }
    }
}
