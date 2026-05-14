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
        seedAdminIfEmpty();
        seedVaccinesIfEmpty();
    }

    private void seedAdminIfEmpty() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM sys_user WHERE type = 1", Integer.class);
            if (count != null && count > 0) {
                log.info("管理员账户已存在 ({} 个)，跳过初始化", count);
                return;
            }
        } catch (Exception e) {
            log.info("sys_user 表不存在或为空，开始初始化管理员账户...");
        }

        try {
            jdbcTemplate.update(
                    "INSERT IGNORE INTO sys_user (username, password, phone, type, status, create_time, update_time) " +
                            "VALUES ('admin', '$2b$10$o1DD40tNdnPaRQ0hW8pbT.l5/Ao3/EtvOcHU9p0rrpp/fiD/ST3Uq', '13800000000', 1, 1, NOW(), NOW())");
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
            populator.addScript(new ClassPathResource("data-vaccines.sql"));
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
}
