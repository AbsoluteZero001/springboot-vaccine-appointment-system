-- Vaccine Appointment System Database Schema
-- MySQL

-- User table
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `phone` VARCHAR(20),
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT 'USER, ADMIN, etc.',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '0: inactive, 1: active',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_username` (`username`),
    INDEX `idx_email` (`email`),
    INDEX `idx_role` (`role`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Admin table
CREATE TABLE IF NOT EXISTS `admin` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(100) NOT NULL,
    `role` VARCHAR(20) NOT NULL DEFAULT 'ADMIN' COMMENT 'ADMIN, SUPER_ADMIN',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Vaccine table
CREATE TABLE IF NOT EXISTS `vaccine` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    `manufacturer` VARCHAR(100),
    `description` TEXT,
    `stock_quantity` INT NOT NULL DEFAULT 0,
    `available` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否上架',
    `image_url` VARCHAR(255) COMMENT '疫苗图片URL',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_name` (`name`),
    INDEX `idx_available` (`available`),
    INDEX `idx_stock` (`stock_quantity`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Appointment table
CREATE TABLE IF NOT EXISTS `appointment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `vaccine_id` BIGINT NOT NULL,
    `appointment_time` DATETIME NOT NULL COMMENT '预约接种时间',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0: pending, 1: confirmed, 2: completed, 3: cancelled',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`vaccine_id`) REFERENCES `vaccine`(`id`) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_vaccine_id` (`vaccine_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_appointment_time` (`appointment_time`),
    -- Prevent duplicate appointment with same user, vaccine and status
    UNIQUE INDEX `uniq_user_vaccine_status` (`user_id`, `vaccine_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Vaccination record table
CREATE TABLE IF NOT EXISTS `vaccination_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `appointment_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `vaccine_id` BIGINT NOT NULL,
    `vaccination_time` DATETIME NOT NULL COMMENT '实际接种时间',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0: scheduled, 1: administered',
    `notes` TEXT,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`appointment_id`) REFERENCES `appointment`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`vaccine_id`) REFERENCES `vaccine`(`id`) ON DELETE CASCADE,
    UNIQUE INDEX `uniq_appointment` (`appointment_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_vaccine_id` (`vaccine_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_vaccination_time` (`vaccination_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;