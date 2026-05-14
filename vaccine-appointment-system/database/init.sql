-- ============================================
-- Vaccine Appointment System - 数据库初始化脚本
-- Database: vaccine_appointment_db
--
-- 表结构: sys_user, vaccine, appointment, vaccination_record, appointment_log
-- 初始数据: 46种疫苗 + 1个管理员账户
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
-- 清理旧表结构（user/admin 已合并为 sys_user）
-- ============================================
DROP TABLE IF EXISTS `appointment_log`;
DROP TABLE IF EXISTS `vaccination_record`;
DROP TABLE IF EXISTS `appointment`;
DROP TABLE IF EXISTS `admin`;
DROP TABLE IF EXISTS `user`;

-- ============================================
-- 1. 统一用户表 sys_user
--    type: 0=普通用户(NORMAL)  1=管理员(ADMIN)
--    status: 0=禁用  1=正常
--    gender: 0=未知 1=男 2=女
-- ============================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `username`    VARCHAR(50)  NOT NULL,
    `password`    VARCHAR(100) NOT NULL,
    `phone`  VARCHAR(20) NOT NULL,
    `type`        INT          NOT NULL DEFAULT 0 COMMENT '0=普通用户 1=管理员',
    `status`      INT          NOT NULL DEFAULT 1 COMMENT '0=禁用 1=正常',
    `gender` INT         NULL COMMENT '0=未知 1=男 2=女',
    `birthday`    DATE         NULL COMMENT '出生日期',
    `remark`      VARCHAR(500) NULL COMMENT '备注（过敏史、慢性病等）',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_user_username` (`username`),
    UNIQUE KEY `uk_sys_user_phone` (`phone`),
    INDEX `idx_sys_user_type` (`type`),
    INDEX `idx_sys_user_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 管理员初始数据 (密码: admin123)
INSERT INTO `sys_user` (`username`, `password`, `phone`, `type`, `status`, `create_time`, `update_time`)
VALUES ('admin', '$2b$10$o1DD40tNdnPaRQ0hW8pbT.l5/Ao3/EtvOcHU9p0rrpp/fiD/ST3Uq', '13800000000', 1, 1, NOW(), NOW());

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
-- 疫苗初始数据 (46种)
-- ============================================
INSERT INTO `vaccine` (`id`, `name`, `manufacturer`, `description`, `stock_quantity`, `available`, `image_url`,
                       `category`, `brand`, `dosage`, `technique`, `schedule_info`, `doses_required`, `age_range`,
                       `target_disease`, `version`, `create_time`, `update_time`)
VALUES
-- 乙肝疫苗系列 (7种)
(1, '重组乙型肝炎疫苗（CHO细胞）10μg', '华北制药金坦生物',
 '采用CHO细胞表达乙肝表面抗原，安全有效，适用于新生儿和儿童基础免疫。', 1200, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '乙肝疫苗', '华北金坦',
 '10μg/0.5ml', 'CHO细胞', '周一至周五 8:00-11:30, 14:00-16:30；周六 8:00-11:30', 3, '全年龄段（尤其新生儿/儿童）',
 '预防乙型肝炎病毒感染', 0, NOW(), NOW()),
(2, '重组乙型肝炎疫苗（CHO细胞）20μg', '华北制药金坦生物',
 'CHO细胞表达，20μg高剂量，适用于成人及青少年加强免疫。', 800, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '乙肝疫苗', '华北金坦',
 '20μg/1.0ml', 'CHO细胞', '周一至周五 8:00-11:30, 14:00-16:30；周六 8:00-11:30', 3, '15岁以上青少年及成人',
 '预防乙型肝炎病毒感染', 0, NOW(), NOW()),
(3, '重组乙型肝炎疫苗（汉逊酵母）10μg', '大连汉信生物制药',
 '汉逊酵母表达系统，免疫原性优良，适用于儿童基础免疫。', 900, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '乙肝疫苗', '大连汉信',
 '10μg/0.5ml', '汉逊酵母', '周一至周五 8:00-11:30, 14:00-16:30', 3, '16岁以下儿童/青少年', '预防乙型肝炎病毒感染', 0,
 NOW(), NOW()),
(4, '重组乙型肝炎疫苗（汉逊酵母）20μg', '大连汉信生物制药',
 '汉逊酵母表达，20μg规格，适用于成人和高危人群。', 850, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '乙肝疫苗', '大连汉信',
 '20μg/1.0ml', '汉逊酵母', '周一至周五 8:00-11:30, 14:00-16:30', 3, '16岁以上及成人', '预防乙型肝炎病毒感染', 0, NOW(),
 NOW()),
(5, '重组乙型肝炎疫苗（酿酒酵母）10μg', '葛兰素史克（GSK）',
 '全球经典乙肝疫苗，酿酒酵母表达系统，临床使用超过30年。', 600, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '乙肝疫苗', 'Engerix-B',
 '10μg/0.5ml', '酿酒酵母', '周一至周五 8:00-11:30, 14:00-16:30', 3, '全年龄段（婴幼儿/儿童）', '预防乙型肝炎病毒感染', 0,
 NOW(), NOW()),
(6, '重组乙型肝炎疫苗（酿酒酵母）20μg', '葛兰素史克（GSK）',
 '酿酒酵母表达，20μg规格，成人标准免疫剂量。', 700, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '乙肝疫苗', 'Engerix-B',
 '20μg/1.0ml', '酿酒酵母', '周一至周五 8:00-11:30, 14:00-16:30', 3, '成人（20岁以上）', '预防乙型肝炎病毒感染', 0, NOW(),
 NOW()),
(7, '重组乙型肝炎疫苗（酿酒酵母）60μg', '葛兰素史克（GSK）',
 '60μg高剂量免疫，适用于乙肝疫苗常规免疫无应答者。', 300, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '乙肝疫苗', 'Engerix-B',
 '60μg/1.0ml', '酿酒酵母', '周一至周五 8:00-11:30（仅上午）', 1, '常规乙肝疫苗接种无应答的成人', '预防乙型肝炎病毒感染',
 0, NOW(), NOW()),
-- HPV疫苗系列 (3种)
(8, '九价人乳头瘤病毒疫苗（酿酒酵母）', '默沙东（MSD）',
 '覆盖HPV 6/11/16/18/31/33/45/52/58九种亚型，可预防约90%的宫颈癌及相关疾病。', 200, TRUE,
 'https://images.unsplash.com/photo-1584467735871-8db9ac8d091c?w=400&h=300&fit=crop', 'HPV疫苗', 'Gardasil 9',
 '0.5ml', '重组蛋白（酿酒酵母）', '周一至周五 8:00-11:30（仅上午接种，需提前一周预约）', 3, '9-45岁女性',
 '预防宫颈癌及HPV相关疾病', 0, NOW(), NOW()),
(9, '四价人乳头瘤病毒疫苗（酿酒酵母）', '默沙东（MSD）',
 '覆盖HPV 6/11/16/18四种亚型，同时预防生殖器疣和宫颈癌。', 350, TRUE,
 'https://images.unsplash.com/photo-1584467735871-8db9ac8d091c?w=400&h=300&fit=crop', 'HPV疫苗', 'Gardasil',
 '0.5ml', '重组蛋白（酿酒酵母）', '周一至周五 8:00-11:30（仅上午接种）', 3, '20-45岁女性', '预防宫颈癌及生殖器疣', 0, NOW(),
 NOW()),
(10, '双价人乳头瘤病毒疫苗（大肠杆菌）', '厦门万泰沧海生物',
 '国产HPV疫苗，覆盖HPV 16/18高危亚型，性价比优越。', 500, TRUE,
 'https://images.unsplash.com/photo-1584467735871-8db9ac8d091c?w=400&h=300&fit=crop', 'HPV疫苗', '馨可宁（Cecolin）',
 '0.5ml', '重组蛋白（大肠杆菌）', '周一至周五 8:00-11:30, 14:00-16:30', 3, '9-45岁女性',
 '预防HPV 16/18型所致的宫颈癌', 0, NOW(), NOW()),
-- 流感疫苗系列 (3种)
(11, '四价流感病毒裂解疫苗', '华兰生物',
 '覆盖两种甲型（H1N1/H3N2）和两种乙型（Victoria/Yamagata）流感病毒株。', 1500, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '流感疫苗', '华兰生物',
 '0.5ml', '裂解灭活', '周一至周日 8:00-16:30（节假日不休）', 1, '6月龄以上全年龄段', '预防季节性流行性感冒', 0, NOW(),
 NOW()),
(12, '三价流感病毒裂解疫苗', '长春生物制品研究所',
 '覆盖两种甲型和一种乙型流感病毒株，基础流感防护。', 1000, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '流感疫苗', '长春生物',
 '0.5ml', '裂解灭活', '周一至周日 8:00-16:30（节假日不休）', 1, '6月龄以上全年龄段', '预防季节性流行性感冒', 0, NOW(),
 NOW()),
(34, '四价流感病毒亚单位疫苗', '江苏中慧元通生物科技',
 '亚单位工艺流感疫苗，纯度更高、不良反应更少，覆盖甲型H1N1/H3N2及乙型Victoria/Yamagata四种毒株。', 1000, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '流感疫苗', '中慧元通',
 '0.5ml/支', '亚单位灭活', '周一至周日 8:00-16:30（节假日不休）', 1, '6月龄以上全年龄段',
 '预防四种流感病毒株引起的季节性流感', 0, NOW(), NOW()),
-- 肺炎疫苗系列 (3种)
(13, '23价肺炎球菌多糖疫苗', '默沙东（MSD）',
 '覆盖23种血清型肺炎球菌，推荐老年人和慢性病患者接种。', 800, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '肺炎疫苗', 'Pneumovax 23',
 '0.5ml', '多糖', '周一至周五 8:00-11:30, 14:00-16:30', 1, '2岁以上儿童及成人（尤其老年人）', '预防肺炎球菌性疾病', 0,
 NOW(), NOW()),
(14, '13价肺炎球菌多糖结合疫苗', '辉瑞（Pfizer）',
 '覆盖13种血清型，结合工艺免疫记忆效果好，婴幼儿首选。', 400, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '肺炎疫苗', 'Prevenar 13',
 '0.5ml', '多糖结合', '周一至周五 8:00-11:30, 14:00-16:30', 4, '6周龄-5岁婴幼儿', '预防婴幼儿肺炎球菌性疾病', 0, NOW(),
 NOW()),
(32, '13价肺炎球菌多糖结合疫苗（国产）', '云南沃森生物技术',
 '国产13价肺炎球菌结合疫苗（沃安欣），覆盖13种血清型，结合工艺产生免疫记忆，婴幼儿肺炎防护首选。', 600, TRUE,
 'https://images.unsplash.com/photo-1584467735871-8db9ac8d091c?w=400&h=300&fit=crop', '肺炎疫苗', '沃安欣',
 '0.5ml/支', '多糖结合（CRM197载体）', '周一至周五 8:00-11:30, 14:00-16:30', 4, '6周龄至5周岁婴幼儿',
 '预防13种血清型肺炎链球菌侵袭性疾病', 0, NOW(), NOW()),
(33, '23价肺炎球菌多糖疫苗（国产）', '成都生物制品研究所',
 '国产23价肺炎球菌多糖疫苗，覆盖23种常见血清型，推荐老年人及慢性基础疾病患者接种。', 800, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '肺炎疫苗', '成都生物',
 '0.5ml/支', '多糖', '周一至周五 8:00-11:30, 14:00-16:30', 1, '2岁以上高危人群（65岁以上老人、慢性病患者）',
 '预防23种血清型肺炎球菌性疾病', 0, NOW(), NOW()),
-- 带状疱疹疫苗系列 (2种)
(15, '重组带状疱疹疫苗（CHO细胞）', '葛兰素史克（GSK）',
 '重组蛋白+AS01B佐剂系统，保护效力超过90%，预防带状疱疹及后遗神经痛。', 250, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '带状疱疹疫苗', 'Shingrix（欣安立适）',
 '0.5ml', '重组蛋白（CHO细胞）', '周一至周五 8:00-11:30, 14:00-16:30', 2, '50岁及以上成人',
 '预防带状疱疹及带状疱疹后神经痛', 0, NOW(), NOW()),
(35, '带状疱疹减毒活疫苗', '长春百克生物科技',
 '国产带状疱疹减毒活疫苗（感维），单次皮下注射，覆盖年龄早于重组亚单位疫苗（40岁以上即可接种）。', 500, TRUE,
 'https://images.unsplash.com/photo-1584467735871-8db9ac8d091c?w=400&h=300&fit=crop', '带状疱疹疫苗', '感维',
 '0.5ml/支', '减毒活病毒', '周一至周五 8:00-11:30, 14:00-16:30', 1, '40岁及以上成人', '预防带状疱疹及后遗神经痛', 0,
 NOW(), NOW()),
-- 新冠/狂犬/水痘/甲肝/百白破/乙脑/流脑等
(16, '新冠病毒mRNA疫苗（加强针）', '多个厂家',
 '针对奥密克戎变异株的mRNA加强针，已完成基础免疫后6个月接种。', 2000, TRUE,
 'https://images.unsplash.com/photo-1584467735871-8db9ac8d091c?w=400&h=300&fit=crop', '新冠疫苗', '多个品牌',
 '0.3ml', 'mRNA / 灭活', '周一至周六 8:00-17:00（中午不休）', 1, '18岁以上已完基础免疫人群',
 '预防新型冠状病毒感染及重症', 0, NOW(), NOW()),
(17, '冻干人用狂犬病疫苗（Vero细胞）', '辽宁成大生物',
 '暴露后预防狂犬病的标准疫苗，Vero细胞培养工艺，免疫原性优良。', 500, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '狂犬疫苗', '成大速达',
 '1.0ml', 'Vero细胞培养灭活', '周一至周日 8:00-20:00（犬伤门诊24小时接诊）', 5, '全年龄段（暴露后无禁忌）', '预防狂犬病', 0,
 NOW(), NOW()),
(18, '水痘减毒活疫苗', '长春百克生物',
 '预防水痘-带状疱疹病毒引起的儿童水痘，安全有效。', 600, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '水痘疫苗', '百克生物',
 '0.5ml', '减毒活病毒', '周一至周五 8:00-11:30, 14:00-16:30', 2, '12月龄-12岁儿童', '预防水痘', 0, NOW(), NOW()),
(19, '甲型肝炎灭活疫苗', '长春生物制品研究所',
 '预防甲型肝炎病毒感染，安全稳定，保护期长达20年以上。', 700, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '甲肝疫苗', '长春生物',
 '0.5ml', '灭活病毒', '周一至周五 8:00-11:30, 14:00-16:30', 2, '18月龄以上儿童及成人', '预防甲型肝炎', 0, NOW(), NOW()),
(20, '吸附无细胞百白破联合疫苗', '赛诺菲（Sanofi）',
 '同时预防百日咳、白喉、破伤风三种疾病，儿童基础免疫必种疫苗。', 1000, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '百白破疫苗', '赛诺菲',
 '0.5ml', '灭活/类毒素', '周一至周五 8:00-11:30, 14:00-16:30', 4, '3月龄-6岁儿童', '预防百日咳、白喉、破伤风', 0, NOW(),
 NOW()),
(21, '流行性乙型脑炎减毒活疫苗', '成都生物制品研究所',
 '预防流行性乙型脑炎，国家免疫规划必种疫苗之一。', 800, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '乙脑疫苗', '成都生物',
 '0.5ml', '减毒活病毒', '周一至周五 8:00-11:30, 14:00-16:30', 2, '8月龄-6岁儿童', '预防流行性乙型脑炎', 0, NOW(),
 NOW()),
(22, 'A+C群脑膜炎球菌多糖疫苗', '武汉生物制品研究所',
 '预防A群和C群脑膜炎球菌引起的流行性脑脊髓膜炎。', 750, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '流脑疫苗', '武汉生物',
 '0.5ml', '多糖', '周一至周五 8:00-11:30, 14:00-16:30', 2, '6月龄-15岁儿童', '预防A/C群流行性脑脊髓膜炎', 0, NOW(),
 NOW()),
-- 卡介苗 / 脊灰系列
(23, '皮内注射用卡介苗（BCG）', '成都生物制品研究所',
 '预防结核病的经典减毒活疫苗，国家免疫规划新生儿必种疫苗，可有效预防婴幼儿粟粒性结核和结核性脑膜炎。', 1500, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '卡介苗', '成都生物',
 '0.1ml/5人份', '减毒活细菌', '周一至周五 8:00-11:30, 14:00-16:30', 1, '新生儿（出生24小时内），未接种的PPD阴性儿童',
 '预防结核病（尤其重症结核）', 0, NOW(), NOW()),
(24, '脊髓灰质炎灭活疫苗（IPV，Sabin株）', '中国医学科学院医学生物学研究所',
 'Sabin株灭活脊灰疫苗，安全性优于OPV，无VAPP风险，国家免疫规划2月龄/3月龄与bOPV序贯使用。', 1200, TRUE,
 'https://images.unsplash.com/photo-1584467735871-8db9ac8d091c?w=400&h=300&fit=crop', '脊灰疫苗', '昆明所',
 '0.5ml/支', '灭活病毒', '周一至周五 8:00-11:30, 14:00-16:30', 4, '2月龄及以上婴幼儿', '预防脊髓灰质炎（小儿麻痹症）', 0,
 NOW(), NOW()),
(25, '二价口服脊髓灰质炎减毒活疫苗（bOPV）', '北京天坛生物制品研究所',
 '口服减毒活疫苗，I型和III型双价，用于IPV序贯免疫程序中的后续剂次及4岁加强，服用方便。', 1000, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '脊灰疫苗', '北京天坛',
 '2滴/0.1ml', '减毒活病毒（口服）', '周一至周五 8:00-11:30, 14:00-16:30', 2, '4月龄及4周岁儿童', '预防脊髓灰质炎', 0,
 NOW(), NOW()),
-- 麻疹 / 麻腮风
(26, '麻疹减毒活疫苗', '北京天坛生物制品研究所',
 '麻疹单价减毒活疫苗，用于未纳入麻腮风联合疫苗时的麻疹基础免疫，现多被MMR替代使用。', 500, TRUE,
 'https://images.unsplash.com/photo-1584467735871-8db9ac8d091c?w=400&h=300&fit=crop', '麻疹疫苗', '北京天坛',
 '0.5ml/支', '减毒活病毒', '周一至周五 8:00-11:30, 14:00-16:30', 1, '8月龄以上无麻疹免疫史儿童', '预防麻疹', 0, NOW(),
 NOW()),
(27, '麻腮风联合减毒活疫苗（MMR）', '上海生物制品研究所',
 '同时预防麻疹、流行性腮腺炎、风疹三种呼吸道传染病，国家免疫规划18月龄和4-6岁共接种2剂。', 1200, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '麻腮风疫苗', '上海生物',
 '0.5ml/支', '减毒活病毒（联合）', '周一至周五 8:00-11:30, 14:00-16:30', 2, '8月龄以上儿童（18月龄首剂+4-6岁加强）',
 '预防麻疹、腮腺炎、风疹', 0, NOW(), NOW()),
-- Hib / 轮状病毒 / 手足口
(28, 'b型流感嗜血杆菌结合疫苗（Hib）', '兰州生物制品研究所',
 '预防b型流感嗜血杆菌所致婴幼儿重症感染（脑膜炎/肺炎/会厌炎），常与百白破等组成四联/五联苗使用。', 800, TRUE,
 'https://images.unsplash.com/photo-1584467735871-8db9ac8d091c?w=400&h=300&fit=crop', 'Hib疫苗', '兰州生物',
 '0.5ml/支', '多糖结合', '周一至周五 8:00-11:30, 14:00-16:30', 4, '2月龄至5周岁婴幼儿',
 '预防b型流感嗜血杆菌感染（脑膜炎/肺炎）', 0, NOW(), NOW()),
(29, '口服轮状病毒减毒活疫苗（单价）', '兰州生物制品研究所',
 '国产单价轮状病毒疫苗（罗特威），预防A群轮状病毒引起的婴幼儿秋季腹泻，每年服用1次至3岁。', 900, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '轮状病毒疫苗', '罗特威',
 '3ml/瓶', '减毒活病毒（口服）', '周一至周五 8:00-11:30, 14:00-16:30', 3, '2月龄至3周岁婴幼儿',
 '预防A群轮状病毒引起的婴幼儿严重腹泻', 0, NOW(), NOW()),
(30, '口服五价重配轮状病毒减毒活疫苗', '默沙东（MSD）',
 '进口五价轮状病毒疫苗（RotaTeq），覆盖G1/G2/G3/G4/P1A五种常见血清型，保护范围广。', 400, TRUE,
 'https://images.unsplash.com/photo-1584467735871-8db9ac8d091c?w=400&h=300&fit=crop', '轮状病毒疫苗', 'RotaTeq（乐幼康）',
 '2ml/支', '减毒活病毒（牛-人重配株/口服）', '周一至周五 8:00-11:30, 14:00-16:30', 3, '6周龄至32周龄婴儿',
 '预防五种血清型轮状病毒引起的重症婴幼儿胃肠炎', 0, NOW(), NOW()),
(31, '肠道病毒71型灭活疫苗（人二倍体细胞）', '北京科兴生物制品',
 '预防EV71型肠道病毒引起的重症手足口病（脑干脑炎/神经源性肺水肿），人二倍体细胞工艺安全性优良。', 700, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '手足口疫苗', '益尔来福',
 '0.5ml/支', '灭活病毒', '周一至周五 8:00-11:30, 14:00-16:30', 2, '6月龄至5周岁儿童', '预防EV71型重症手足口病', 0,
 NOW(), NOW()),
-- 出血热 / 钩端螺旋体 / 霍乱
(36, '双价肾综合征出血热灭活疫苗（沙鼠肾细胞）', '长春生物制品研究所',
 '预防汉坦病毒I型（汉滩型）和II型（首尔型）引起的流行性出血热，适用于疫区高风险人群。', 400, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '出血热疫苗', '长春生物',
 '1.0ml/支', '灭活病毒', '周一至周五 8:00-11:30, 14:00-16:30', 3, '16-60岁高发地区居民及野外作业等高风险人群',
 '预防汉坦病毒引起的肾综合征出血热', 0, NOW(), NOW()),
(37, '钩端螺旋体灭活疫苗（多价）', '上海生物制品研究所',
 '多价钩端螺旋体灭活疫苗，覆盖黄疸出血群等多种血清群，适用于疫区农民、下水道工人等职业高风险人群。', 300, TRUE,
 'https://images.unsplash.com/photo-1584467735871-8db9ac8d091c?w=400&h=300&fit=crop', '钩端螺旋体疫苗', '上海生物',
 '1.0ml/支', '灭活全菌体', '周一至周五 8:00-11:30, 14:00-16:30', 2, '疫区农民、下水道工人、野外作业者等职业高风险人群',
 '预防多种血清群钩端螺旋体病', 0, NOW(), NOW()),
(38, '口服重组B亚单位/菌体霍乱疫苗（肠溶胶囊）', '上海联合赛尔生物工程',
 '口服肠溶胶囊剂型，预防霍乱O1群的同时提供产毒性大肠杆菌（ETEC）交叉保护，适用于旅行者和疫区人群。', 400, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '霍乱疫苗', '上海联合赛尔',
 '每粒含rBS 1mg+5×10¹⁰灭活菌体', '重组蛋白+灭活全菌体（口服）', '周一至周五 8:00-11:30, 14:00-16:30', 3,
 '2岁以上前往霍乱疫区旅行者及疫区高危人群', '预防霍乱O1群及ETEC旅行者腹泻', 0, NOW(), NOW()),
-- 黄热病 / 登革热
(39, '黄热病减毒活疫苗（鸡胚适应17D株）', '北京天坛生物制品研究所',
 'WHO认证终身有效的黄热病疫苗，国际旅行健康必备，前往非洲/南美疫区国家入境强制要求出示黄皮书。', 200, TRUE,
 'https://images.unsplash.com/photo-1584467735871-8db9ac8d091c?w=400&h=300&fit=crop', '黄热病疫苗', '北京天坛',
 '0.5ml/支', '减毒活病毒（鸡胚培养）', '周一至周五 8:00-11:30（仅上午，需提前预约）', 1, '9月龄以上前往非洲/南美疫区旅行者',
 '预防黄热病毒感染', 0, NOW(), NOW()),
(40, '四价登革热嵌合减毒活疫苗', '赛诺菲巴斯德（Sanofi Pasteur）',
 '基于黄热病毒骨架嵌合四种登革病毒基因的重组减毒活疫苗（Dengvaxia），接种前须血清学检测确认既往感染。', 150, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '登革热疫苗', 'Dengvaxia',
 '0.5ml/支', '减毒活病毒（嵌合病毒）', '周一至周五 8:00-11:30（仅上午，需提前预约）', 3,
 '9-45岁有登革热既往感染血清学证据者',
 '预防四种血清型登革病毒感染及重症', 0, NOW(), NOW()),
-- 甲肝系列 (补充)
(41, '甲型肝炎灭活疫苗（儿童剂型）', '北京科兴生物制品',
 '国产儿童剂型甲肝灭活疫苗（孩尔来福），含250U抗原/0.5ml，12月龄以上即可接种，安全稳定。', 800, TRUE,
 'https://images.unsplash.com/photo-1584467735871-8db9ac8d091c?w=400&h=300&fit=crop', '甲肝疫苗', '孩尔来福',
 '0.5ml/支（250U）', '灭活病毒', '周一至周五 8:00-11:30, 14:00-16:30', 2, '12月龄至15周岁儿童', '预防甲型肝炎', 0, NOW(),
 NOW()),
(42, '甲型肝炎灭活疫苗（成人剂型）', '葛兰素史克（GSK）',
 '进口成人剂型甲肝灭活疫苗（Havrix），含1440 ELISA单位/1.0ml，适用于16岁以上成人及旅行者。', 400, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '甲肝疫苗', 'Havrix',
 '1.0ml/支（1440 EU）', '灭活病毒', '周一至周五 8:00-11:30, 14:00-16:30', 2,
 '16岁及以上成人（旅行者/餐饮从业者/慢性肝病患者）',
 '预防甲型肝炎', 0, NOW(), NOW()),
-- 乙肝加强 / 腮腺炎 / 风疹 / 流脑四价
(43, '重组乙型肝炎疫苗（酿酒酵母）60μg加强针', '深圳康泰生物制品',
 '国产高剂量60μg乙肝加强疫苗，适用于常规3剂后HBsAb<10mIU/mL无应答者，通过高抗原剂量激发有效保护。', 500, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '乙肝疫苗', '深圳康泰',
 '60μg/1.0ml', '重组蛋白（酿酒酵母）', '周一至周五 8:00-11:30, 14:00-16:30', 1, '常规乙肝疫苗接种后无应答的成人',
 '预防乙型肝炎病毒感染（加强免疫）', 0, NOW(), NOW()),
(44, '流行性腮腺炎减毒活疫苗（单苗）', '上海生物制品研究所',
 '腮腺炎单价减毒活疫苗，用于不宜接种MMR中对麻疹/风疹成分有禁忌者，现多被麻腮风联合疫苗替代。', 300, TRUE,
 'https://images.unsplash.com/photo-1584467735871-8db9ac8d091c?w=400&h=300&fit=crop', '腮腺炎疫苗', '上海生物',
 '0.5ml/支', '减毒活病毒', '周一至周五 8:00-11:30, 14:00-16:30', 1, '8月龄以上未患腮腺炎且无MMR接种史者',
 '预防流行性腮腺炎', 0, NOW(), NOW()),
(45, '风疹减毒活疫苗（单苗）', '北京天坛生物制品研究所',
 '风疹单价减毒活疫苗，重点保护育龄妇女预防孕早期风疹感染导致的先天性风疹综合征（CRS），现多被MMR替代。', 300, TRUE,
 'https://images.unsplash.com/photo-1584467735871-8db9ac8d091c?w=400&h=300&fit=crop', '风疹疫苗', '北京天坛',
 '0.5ml/支', '减毒活病毒', '周一至周五 8:00-11:30, 14:00-16:30', 1, '8月龄以上未免疫儿童及育龄期未免疫妇女',
 '预防风疹及先天性风疹综合征', 0, NOW(), NOW()),
(46, 'ACYW135群脑膜炎球菌多糖结合疫苗', '康希诺生物股份',
 '国产四价流脑结合疫苗（曼海欣），CRM197载体，覆盖A/C/Y/W135四种血清群，婴幼儿中免疫记忆优于多糖疫苗。', 500, TRUE,
 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop', '流脑疫苗', '曼海欣',
 '0.5ml/支', '多糖结合（CRM197载体）', '周一至周五 8:00-11:30, 14:00-16:30', 2, '3月龄至3周岁婴幼儿',
 '预防A/C/Y/W135群流行性脑脊髓膜炎', 0, NOW(), NOW());
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
