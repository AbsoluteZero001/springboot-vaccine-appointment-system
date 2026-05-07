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
}
