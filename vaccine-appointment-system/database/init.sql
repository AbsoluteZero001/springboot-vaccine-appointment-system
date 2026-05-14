-- ============================================
-- Vaccine Appointment System - 数据库初始化脚本
-- Database: vaccine_appointment_db
--
-- 表结构: sys_user, vaccine, appointment, vaccination_record, appointment_log
-- 初始数据: 1个管理员账户
-- 管理员: admin / admin123 (BCrypt加密)
-- 普通用户通过注册页面自行注册，不预置测试用户
-- ============================================

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- ============================================
-- 清理旧表
-- ============================================
DROP TABLE IF EXISTS `appointment_log`;
DROP TABLE IF EXISTS `vaccination_record`;
DROP TABLE IF EXISTS `appointment`;

-- ============================================
-- 1. 统一用户表 sys_user
--    role: ROLE_USER=普通用户  ROLE_ADMIN=管理员
--    status: 0=禁用  1=正常
-- ============================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `username`    VARCHAR(50)  NOT NULL,
    `password`    VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20) NOT NULL,
    `role`  VARCHAR(20) NOT NULL,
    `status`      INT          NOT NULL DEFAULT 1 COMMENT '0=禁用 1=正常',
    `gender`   INT          DEFAULT NULL COMMENT '0=未知 1=男 2=女',
    `birthday` DATE         DEFAULT NULL,
    `remark`   VARCHAR(500) DEFAULT NULL,
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_user_username` (`username`),
    UNIQUE KEY `uk_sys_user_phone` (`phone`),
    INDEX `idx_sys_user_role` (`role`),
    INDEX `idx_sys_user_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 管理员初始数据 (密码: admin123)
INSERT INTO `sys_user` (`username`, `password`, `phone`, `role`, `status`, `create_time`, `update_time`)
VALUES ('admin', '$2b$10$o1DD40tNdnPaRQ0hW8pbT.l5/Ao3/EtvOcHU9p0rrpp/fiD/ST3Uq', '13800000000', 'ROLE_ADMIN', 1, NOW(),
        NOW());

-- ============================================
-- 2. 疫苗表 vaccine
--    version 字段用于乐观锁并发控制
-- ============================================
DROP TABLE IF EXISTS `vaccine`;
CREATE TABLE `vaccine`
(
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `name`           VARCHAR(100) NOT NULL,
    `manufacturer`   VARCHAR(100) NULL,
    `description`    TEXT         NULL,
    `stock_quantity` INT          NOT NULL DEFAULT 0,
    `available`      BIT(1)       NOT NULL DEFAULT 1 COMMENT '1=可预约 0=已下架',
    `image_url`      VARCHAR(255) NULL,
    `category`       VARCHAR(50)  NULL,
    `brand`          VARCHAR(100) NULL,
    `dosage`         VARCHAR(50)  NULL,
    `technique`      VARCHAR(100) NULL,
    `schedule_info`  TEXT         NULL,
    `doses_required` INT          NULL,
    `age_range`      VARCHAR(100) NULL,
    `target_disease` VARCHAR(200) NULL,
    `version`        BIGINT       NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_vaccine_available` (`available`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ============================================
-- 3. 预约表 appointment
--    status: 0=已预约 1=已完成 2=未到场 3=已取消
-- ============================================
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment`
(
    `id`                BIGINT   NOT NULL AUTO_INCREMENT,
    `user_id`           BIGINT   NOT NULL,
    `vaccine_id`        BIGINT   NOT NULL,
    `appointment_time`  DATETIME NOT NULL,
    `status`            INT      NOT NULL DEFAULT 0 COMMENT '0=已预约 1=已完成 2=未到场 3=已取消',
    `status_updated_at` DATETIME NULL,
    `create_time`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_appointment_user_status` (`user_id`, `status`),
    INDEX `idx_appointment_status` (`status`),
    INDEX `idx_appointment_time` (`appointment_time`),
    CONSTRAINT `fk_appointment_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`),
    CONSTRAINT `fk_appointment_vaccine` FOREIGN KEY (`vaccine_id`) REFERENCES `vaccine` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- ============================================
-- 4. 接种记录表 vaccination_record
--    status: 0=已安排 1=已接种
-- ============================================
DROP TABLE IF EXISTS `vaccination_record`;
CREATE TABLE `vaccination_record`
(
    `id`               BIGINT   NOT NULL AUTO_INCREMENT,
    `appointment_id`   BIGINT   NOT NULL,
    `user_id`          BIGINT   NOT NULL,
    `vaccine_id`       BIGINT   NOT NULL,
    `doctor_id`        BIGINT   NULL COMMENT '执行接种的医护人员ID',
    `vaccination_time` DATETIME NOT NULL,
    `status`           INT      NOT NULL DEFAULT 0 COMMENT '0=已安排 1=已接种',
    `notes`            TEXT     NULL,
    `create_time`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_vr_appointment` (`appointment_id`),
    INDEX `idx_vr_user` (`user_id`),
    INDEX `idx_vr_vaccine` (`vaccine_id`),
    INDEX `idx_vr_doctor` (`doctor_id`),
    INDEX `idx_vr_status` (`status`),
    INDEX `idx_vr_time` (`vaccination_time`),
    CONSTRAINT `fk_vr_appointment` FOREIGN KEY (`appointment_id`) REFERENCES `appointment` (`id`),
    CONSTRAINT `fk_vr_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`),
    CONSTRAINT `fk_vr_vaccine` FOREIGN KEY (`vaccine_id`) REFERENCES `vaccine` (`id`),
    CONSTRAINT `fk_vr_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `sys_user` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- ============================================
-- 5. 预约操作日志表 appointment_log
-- ============================================
DROP TABLE IF EXISTS `appointment_log`;
CREATE TABLE `appointment_log`
(
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `appointment_id` BIGINT       NOT NULL,
    `action`         VARCHAR(30)  NOT NULL,
    `old_status`     INT          NULL,
    `new_status`     INT          NULL,
    `changed_by`     VARCHAR(100) NULL,
    `change_reason`  TEXT         NULL,
    `created_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_appointment_log_aid` (`appointment_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ============================================
-- 恢复 SQL 模式设置
-- ============================================
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
