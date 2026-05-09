-- =============================================================
-- 疫苗预约系统 - Docker MySQL 初始化脚本
-- 此文件挂载到 /docker-entrypoint-initdb.d/
-- Hibernate ddl-auto 负责建表，此文件仅确保字符集
-- =============================================================

ALTER
DATABASE vaccine_appointment_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
