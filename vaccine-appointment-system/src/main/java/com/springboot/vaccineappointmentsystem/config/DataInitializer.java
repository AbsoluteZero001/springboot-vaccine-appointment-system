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
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM admin", Integer.class);
            if (count != null && count > 0) {
                log.info("数据库已初始化 ({} 条管理员记录)，跳过种子数据导入。", count);
                return;
            }
        } catch (Exception e) {
            log.info("管理员表不存在或为空，开始数据库初始化...");
        }

        log.info("执行一次性数据库初始化...");
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("schema.sql"));
        populator.addScript(new ClassPathResource("data.sql"));
        populator.setContinueOnError(true);
        populator.execute(dataSource);
        log.info("数据库初始化完成。");
    }
}
