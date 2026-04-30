-- ============================================
-- Vaccine Appointment System - Seed Data
-- ============================================
-- All passwords are bcrypt encoded: "user123" / "admin123"
-- Generated with bcryptjs, $2a$ prefix for Spring Security compatibility
-- ============================================

-- Fix column defaults for Hibernate-generated tables
ALTER TABLE `admin`
    MODIFY `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE `admin`
    MODIFY `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE `vaccine`
    MODIFY `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE `vaccine`
    MODIFY `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE `appointment`
    MODIFY `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE `appointment`
    MODIFY `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE `vaccination_record`
    MODIFY `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE `vaccination_record`
    MODIFY `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE `user`
    MODIFY `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE `user`
    MODIFY `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Insert default admin account
INSERT INTO `admin` (`username`, `password`, `role`) VALUES ('admin',
                                                             '$2a$10$i.UeKhM0VBXin5RTHLS6N.4N4ogdUoTGmiwf9xFuFPQMEqTzbQnyq',
                                                             'ADMIN'),
                                                            ('superadmin',
                                                             '$2a$10$i.UeKhM0VBXin5RTHLS6N.4N4ogdUoTGmiwf9xFuFPQMEqTzbQnyq',
                                                             'SUPER_ADMIN')
ON DUPLICATE KEY UPDATE `password` = VALUES(`password`), `role` = VALUES(`role`);

-- ============================================
-- Vaccines (20+ realistic Chinese-standard vaccines)
-- ============================================
INSERT INTO `vaccine` (`id`, `name`, `manufacturer`, `description`, `stock_quantity`, `available`, `image_url`,
                       `category`, `brand`, `dosage`, `technique`, `schedule_info`, `doses_required`, `age_range`,
                       `target_disease`)
VALUES
-- 1: 乙肝疫苗 (CHO细胞) 10μg
(1, '重组乙型肝炎疫苗（CHO细胞）10μg', '华北制药金坦生物',
 '采用CHO细胞表达乙肝表面抗原，安全有效，适用于新生儿和儿童基础免疫。',
 1200, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop',
 '乙肝疫苗', '华北金坦', '10μg/0.5ml', 'CHO细胞',
 '周一至周五 8:00-11:30, 14:00-16:30；周六 8:00-11:30', 3, '全年龄段（尤其新生儿/儿童）', '预防乙型肝炎病毒感染'),

-- 2: 乙肝疫苗 (CHO细胞) 20μg
(2, '重组乙型肝炎疫苗（CHO细胞）20μg', '华北制药金坦生物',
 'CHO细胞表达，20μg高剂量，适用于成人及青少年加强免疫。',
 800, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop',
 '乙肝疫苗', '华北金坦', '20μg/1.0ml', 'CHO细胞',
 '周一至周五 8:00-11:30, 14:00-16:30；周六 8:00-11:30', 3, '15岁以上青少年及成人', '预防乙型肝炎病毒感染'),

-- 3: 乙肝疫苗 (汉逊酵母) 10μg
(3, '重组乙型肝炎疫苗（汉逊酵母）10μg', '大连汉信生物制药',
 '汉逊酵母表达系统，免疫原性优良，适用于儿童基础免疫。',
 900, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop',
 '乙肝疫苗', '大连汉信', '10μg/0.5ml', '汉逊酵母',
 '周一至周五 8:00-11:30, 14:00-16:30', 3, '16岁以下儿童/青少年', '预防乙型肝炎病毒感染'),

-- 4: 乙肝疫苗 (汉逊酵母) 20μg
(4, '重组乙型肝炎疫苗（汉逊酵母）20μg', '大连汉信生物制药',
 '汉逊酵母表达，20μg规格，适用于成人和高危人群。',
 850, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop',
 '乙肝疫苗', '大连汉信', '20μg/1.0ml', '汉逊酵母',
 '周一至周五 8:00-11:30, 14:00-16:30', 3, '16岁以上及成人', '预防乙型肝炎病毒感染'),

-- 5: 乙肝疫苗 (酿酒酵母) 10μg
(5, '重组乙型肝炎疫苗（酿酒酵母）10μg', '葛兰素史克（GSK）',
 '全球经典乙肝疫苗，酿酒酵母表达系统，临床使用超过30年。',
 600, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop',
 '乙肝疫苗', 'Engerix-B', '10μg/0.5ml', '酿酒酵母',
 '周一至周五 8:00-11:30, 14:00-16:30', 3, '全年龄段（婴幼儿/儿童）', '预防乙型肝炎病毒感染'),

-- 6: 乙肝疫苗 (酿酒酵母) 20μg
(6, '重组乙型肝炎疫苗（酿酒酵母）20μg', '葛兰素史克（GSK）',
 '酿酒酵母表达，20μg规格，成人标准免疫剂量。',
 700, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop',
 '乙肝疫苗', 'Engerix-B', '20μg/1.0ml', '酿酒酵母',
 '周一至周五 8:00-11:30, 14:00-16:30', 3, '成人（20岁以上）', '预防乙型肝炎病毒感染'),

-- 7: 乙肝疫苗 (酿酒酵母) 60μg
(7, '重组乙型肝炎疫苗（酿酒酵母）60μg', '葛兰素史克（GSK）',
 '60μg高剂量免疫，适用于乙肝疫苗常规免疫无应答者。',
 300, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop',
 '乙肝疫苗', 'Engerix-B', '60μg/1.0ml', '酿酒酵母',
 '周一至周五 8:00-11:30（仅上午）', 1, '常规乙肝疫苗接种无应答的成人', '预防乙型肝炎病毒感染'),

-- 8: HPV 九价疫苗
(8, '九价人乳头瘤病毒疫苗（酿酒酵母）', '默沙东（MSD）',
 '覆盖HPV 6/11/16/18/31/33/45/52/58九种亚型，可预防约90%的宫颈癌及相关疾病。',
 200, TRUE, 'https://images.unsplash.com/photo-1584467735871-8db9ac8d091c?w=400&h=300&fit=crop',
 'HPV疫苗', 'Gardasil 9', '0.5ml', '重组蛋白（酿酒酵母）',
 '周一至周五 8:00-11:30（仅上午接种，需提前一周预约）', 3, '9-45岁女性', '预防宫颈癌及HPV相关疾病'),

-- 9: HPV 四价疫苗
(9, '四价人乳头瘤病毒疫苗（酿酒酵母）', '默沙东（MSD）',
 '覆盖HPV 6/11/16/18四种亚型，同时预防生殖器疣和宫颈癌。',
 350, TRUE, 'https://images.unsplash.com/photo-1584467735871-8db9ac8d091c?w=400&h=300&fit=crop',
 'HPV疫苗', 'Gardasil', '0.5ml', '重组蛋白（酿酒酵母）',
 '周一至周五 8:00-11:30（仅上午接种）', 3, '20-45岁女性', '预防宫颈癌及生殖器疣'),

-- 10: HPV 二价疫苗
(10, '双价人乳头瘤病毒疫苗（大肠杆菌）', '厦门万泰沧海生物',
 '国产HPV疫苗，覆盖HPV 16/18高危亚型，性价比优越。',
 500, TRUE, 'https://images.unsplash.com/photo-1584467735871-8db9ac8d091c?w=400&h=300&fit=crop',
 'HPV疫苗', '馨可宁（Cecolin）', '0.5ml', '重组蛋白（大肠杆菌）',
 '周一至周五 8:00-11:30, 14:00-16:30', 3, '9-45岁女性', '预防HPV 16/18型所致的宫颈癌'),

-- 11: 四价流感疫苗
(11, '四价流感病毒裂解疫苗', '华兰生物',
 '覆盖两种甲型（H1N1/H3N2）和两种乙型（Victoria/Yamagata）流感病毒株。',
 1500, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop',
 '流感疫苗', '华兰生物', '0.5ml', '裂解灭活',
 '周一至周日 8:00-16:30（节假日不休）', 1, '6月龄以上全年龄段', '预防季节性流行性感冒'),

-- 12: 三价流感疫苗
(12, '三价流感病毒裂解疫苗', '长春生物制品研究所',
 '覆盖两种甲型和一种乙型流感病毒株，基础流感防护。',
 1000, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop',
 '流感疫苗', '长春生物', '0.5ml', '裂解灭活',
 '周一至周日 8:00-16:30（节假日不休）', 1, '6月龄以上全年龄段', '预防季节性流行性感冒'),

-- 13: 23价肺炎球菌多糖疫苗
(13, '23价肺炎球菌多糖疫苗', '默沙东（MSD）',
 '覆盖23种血清型肺炎球菌，推荐老年人和慢性病患者接种。',
 800, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop',
 '肺炎疫苗', 'Pneumovax 23', '0.5ml', '多糖',
 '周一至周五 8:00-11:30, 14:00-16:30', 1, '2岁以上儿童及成人（尤其老年人）', '预防肺炎球菌性疾病'),

-- 14: 13价肺炎球菌结合疫苗
(14, '13价肺炎球菌多糖结合疫苗', '辉瑞（Pfizer）/ 沃森生物',
 '覆盖13种血清型，结合工艺免疫记忆效果好，婴幼儿首选。',
 400, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop',
 '肺炎疫苗', 'Prevenar 13 / 沃安欣', '0.5ml', '多糖结合',
 '周一至周五 8:00-11:30, 14:00-16:30', 4, '6周龄-5岁婴幼儿', '预防婴幼儿肺炎球菌性疾病'),

-- 15: 带状疱疹疫苗
(15, '重组带状疱疹疫苗（CHO细胞）', '葛兰素史克（GSK）',
 '重组蛋白+AS01B佐剂系统，保护效力超过90%，预防带状疱疹及后遗神经痛。',
 250, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop',
 '带状疱疹疫苗', 'Shingrix（欣安立适）', '0.5ml', '重组蛋白（CHO细胞）',
 '周一至周五 8:00-11:30, 14:00-16:30', 2, '50岁及以上成人', '预防带状疱疹及带状疱疹后神经痛'),

-- 16: 新冠疫苗加强针
(16, '新冠病毒mRNA疫苗（加强针）', '多个厂家',
 '针对奥密克戎变异株的mRNA加强针，已完成基础免疫后6个月接种。',
 2000, TRUE, 'https://images.unsplash.com/photo-1584467735871-8db9ac8d091c?w=400&h=300&fit=crop',
 '新冠疫苗', '多个品牌', '0.3ml', 'mRNA / 灭活',
 '周一至周六 8:00-17:00（中午不休）', 1, '18岁以上已完基础免疫人群', '预防新型冠状病毒感染及重症'),

-- 17: 狂犬病疫苗
(17, '冻干人用狂犬病疫苗（Vero细胞）', '辽宁成大生物',
 '暴露后预防狂犬病的标准疫苗，Vero细胞培养工艺，免疫原性优良。',
 500, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop',
 '狂犬疫苗', '成大速达', '1.0ml', 'Vero细胞培养灭活',
 '周一至周日 8:00-20:00（犬伤门诊24小时接诊）', 5, '全年龄段（暴露后无禁忌）', '预防狂犬病'),

-- 18: 水痘减毒活疫苗
(18, '水痘减毒活疫苗', '长春百克生物',
 '预防水痘-带状疱疹病毒引起的儿童水痘，安全有效。',
 600, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop',
 '水痘疫苗', '百克生物', '0.5ml', '减毒活病毒',
 '周一至周五 8:00-11:30, 14:00-16:30', 2, '12月龄-12岁儿童', '预防水痘'),

-- 19: 甲肝灭活疫苗
(19, '甲型肝炎灭活疫苗', '长春生物制品研究所',
 '预防甲型肝炎病毒感染，安全稳定，保护期长达20年以上。',
 700, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop',
 '甲肝疫苗', '长春生物', '0.5ml', '灭活病毒',
 '周一至周五 8:00-11:30, 14:00-16:30', 2, '18月龄以上儿童及成人', '预防甲型肝炎'),

-- 20: 百白破疫苗
(20, '吸附无细胞百白破联合疫苗', '赛诺菲（Sanofi）',
 '同时预防百日咳、白喉、破伤风三种疾病，儿童基础免疫必种疫苗。',
 1000, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop',
 '百白破疫苗', '赛诺菲', '0.5ml', '灭活/类毒素',
 '周一至周五 8:00-11:30, 14:00-16:30', 4, '3月龄-6岁儿童', '预防百日咳、白喉、破伤风'),

-- 21: 乙脑减毒活疫苗
(21, '流行性乙型脑炎减毒活疫苗', '成都生物制品研究所',
 '预防流行性乙型脑炎，国家免疫规划必种疫苗之一。',
 800, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop',
 '乙脑疫苗', '成都生物', '0.5ml', '减毒活病毒',
 '周一至周五 8:00-11:30, 14:00-16:30', 2, '8月龄-6岁儿童', '预防流行性乙型脑炎'),

-- 22: 流脑A+C群疫苗
(22, 'A+C群脑膜炎球菌多糖疫苗', '武汉生物制品研究所',
 '预防A群和C群脑膜炎球菌引起的流行性脑脊髓膜炎。',
 750, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop',
 '流脑疫苗', '武汉生物', '0.5ml', '多糖',
 '周一至周五 8:00-11:30, 14:00-16:30', 2, '6月龄-15岁儿童', '预防A/C群流行性脑脊髓膜炎')

ON DUPLICATE KEY UPDATE `name`           = VALUES(`name`),
                        `manufacturer`   = VALUES(`manufacturer`),
                        `description`    = VALUES(`description`),
                        `stock_quantity` = VALUES(`stock_quantity`),
                        `available`      = VALUES(`available`),
                        `image_url`      = VALUES(`image_url`),
                        `category`       = VALUES(`category`),
                        `brand`          = VALUES(`brand`),
                        `dosage`         = VALUES(`dosage`),
                        `technique`      = VALUES(`technique`),
                        `schedule_info`  = VALUES(`schedule_info`),
                        `doses_required` = VALUES(`doses_required`),
                        `age_range`      = VALUES(`age_range`),
                        `target_disease` = VALUES(`target_disease`);

-- ============================================
-- Users (existing test accounts)
-- ============================================
INSERT INTO `user` (`username`, `password`, `email`, `phone`, `role`, `status`) VALUES ('testuser',
                                                                                        '$2a$10$i.UeKhM0VBXin5RTHLS6N.4N4ogdUoTGmiwf9xFuFPQMEqTzbQnyq',
                                                                                        'test@example.com',
                                                                                        '13800138000', 'USER', 1),
                                                                                       ('john_doe',
                                                                                        '$2a$10$i.UeKhM0VBXin5RTHLS6N.4N4ogdUoTGmiwf9xFuFPQMEqTzbQnyq',
                                                                                        'john.doe@example.com',
                                                                                        '13900139000', 'USER', 1),
                                                                                       ('jane_smith',
                                                                                        '$2a$10$i.UeKhM0VBXin5RTHLS6N.4N4ogdUoTGmiwf9xFuFPQMEqTzbQnyq',
                                                                                        'jane.smith@example.com',
                                                                                        '13700137000', 'USER', 1),
                                                                                       ('robert_johnson',
                                                                                        '$2a$10$i.UeKhM0VBXin5RTHLS6N.4N4ogdUoTGmiwf9xFuFPQMEqTzbQnyq',
                                                                                        'robert.j@example.com',
                                                                                        '13600136000', 'USER', 1),
                                                                                       ('lisa_wang',
                                                                                        '$2a$10$i.UeKhM0VBXin5RTHLS6N.4N4ogdUoTGmiwf9xFuFPQMEqTzbQnyq',
                                                                                        'lisa.wang@example.com',
                                                                                        '13500135000', 'USER', 1),
                                                                                       ('admin_user',
                                                                                        '$2a$10$i.UeKhM0VBXin5RTHLS6N.4N4ogdUoTGmiwf9xFuFPQMEqTzbQnyq',
                                                                                        'admin.user@example.com',
                                                                                        '13400134000', 'ADMIN', 1),
                                                                                       ('michael_brown',
                                                                                        '$2a$10$i.UeKhM0VBXin5RTHLS6N.4N4ogdUoTGmiwf9xFuFPQMEqTzbQnyq',
                                                                                        'michael.brown@example.com',
                                                                                        '13300133000', 'USER', 1),
                                                                                       ('sarah_lee',
                                                                                        '$2a$10$i.UeKhM0VBXin5RTHLS6N.4N4ogdUoTGmiwf9xFuFPQMEqTzbQnyq',
                                                                                        'sarah.lee@example.com',
                                                                                        '13200132000', 'USER', 1),
                                                                                       ('david_zhang',
                                                                                        '$2a$10$i.UeKhM0VBXin5RTHLS6N.4N4ogdUoTGmiwf9xFuFPQMEqTzbQnyq',
                                                                                        'david.zhang@example.com',
                                                                                        '13100131000', 'USER', 1),
                                                                                       ('emily_chen',
                                                                                        '$2a$10$i.UeKhM0VBXin5RTHLS6N.4N4ogdUoTGmiwf9xFuFPQMEqTzbQnyq',
                                                                                        'emily.chen@example.com',
                                                                                        '13000130000', 'USER', 1),
                                                                                       ('william_wu',
                                                                                        '$2a$10$i.UeKhM0VBXin5RTHLS6N.4N4ogdUoTGmiwf9xFuFPQMEqTzbQnyq',
                                                                                        'william.wu@example.com',
                                                                                        '12900129000', 'USER', 1)
ON DUPLICATE KEY UPDATE `password` = VALUES(`password`), `role` = VALUES(`role`), `status` = VALUES(`status`);

-- ============================================
-- Sample Appointments
-- Note: only inserted on first initialization (DataInitializer runs once)
-- ============================================
INSERT INTO `appointment` (`user_id`, `vaccine_id`, `appointment_time`, `status`) VALUES (1, 1,
                                                                                          DATE_ADD(NOW(), INTERVAL 7 DAY),
                                                                                          0), -- testuser, 乙肝CHO 10μg, pending
                                                                                         (2, 8,
                                                                                          DATE_ADD(NOW(), INTERVAL 3 DAY),
                                                                                          1), -- john_doe, HPV九价, confirmed
                                                                                         (3, 11,
                                                                                          DATE_ADD(NOW(), INTERVAL 5 DAY),
                                                                                          0), -- jane_smith, 四价流感, pending
                                                                                         (4, 13,
                                                                                          DATE_ADD(NOW(), INTERVAL 10 DAY),
                                                                                          2), -- robert_johnson, 23价肺炎, completed
                                                                                         (5, 12,
                                                                                          DATE_ADD(NOW(), INTERVAL 2 DAY),
                                                                                          3), -- lisa_wang, 三价流感, cancelled
                                                                                         (6, 10,
                                                                                          DATE_ADD(NOW(), INTERVAL 14 DAY),
                                                                                          1), -- admin_user, HPV二价, confirmed
                                                                                         (1, 18,
                                                                                          DATE_ADD(NOW(), INTERVAL 21 DAY),
                                                                                          0), -- testuser, 水痘疫苗, pending
                                                                                         (2, 15,
                                                                                          DATE_ADD(NOW(), INTERVAL 30 DAY),
                                                                                          1), -- john_doe, 带状疱疹, confirmed
                                                                                         (7, 18,
                                                                                          DATE_ADD(NOW(), INTERVAL 5 DAY),
                                                                                          0), -- michael_brown, 水痘疫苗, pending
                                                                                         (8, 13,
                                                                                          DATE_ADD(NOW(), INTERVAL 8 DAY),
                                                                                          1), -- sarah_lee, 23价肺炎, confirmed
                                                                                         (9, 20,
                                                                                          DATE_ADD(NOW(), INTERVAL 12 DAY),
                                                                                          0), -- david_zhang, 百白破, pending
                                                                                         (10, 17,
                                                                                          DATE_ADD(NOW(), INTERVAL 15 DAY),
                                                                                          2), -- emily_chen, 狂犬疫苗, completed
                                                                                         (11, 14,
                                                                                          DATE_ADD(NOW(), INTERVAL 20 DAY),
                                                                                          1)  -- william_wu, 13价肺炎, confirmed
ON DUPLICATE KEY UPDATE `status` = VALUES(`status`);

-- ============================================
-- Sample Vaccination Records
-- ============================================
INSERT INTO `vaccination_record` (`appointment_id`, `user_id`, `vaccine_id`, `vaccination_time`, `status`, `notes`)
VALUES (4, 4, 13, DATE_SUB(NOW(), INTERVAL 5 DAY), 1, '23价肺炎疫苗接种完成，无不良反应。'),
       (6, 6, 10, DATE_SUB(NOW(), INTERVAL 2 DAY), 1, 'HPV二价疫苗第一剂，接种后观察30分钟无异常。'),
       (2, 2, 8, DATE_SUB(NOW(), INTERVAL 1 DAY), 1, 'HPV九价疫苗第二剂，注射部位轻微酸痛。'),
       (10, 10, 17, DATE_SUB(NOW(), INTERVAL 3 DAY), 1, '狂犬疫苗第三剂，按程序完成接种。'),
       (13, 11, 14, DATE_ADD(NOW(), INTERVAL 20 DAY), 0, '预约13价肺炎疫苗第一剂。'),
       (8, 2, 15, DATE_ADD(NOW(), INTERVAL 30 DAY), 0, '预约带状疱疹疫苗第一剂。')
ON DUPLICATE KEY UPDATE `status` = VALUES(`status`),
                        `notes`  = VALUES(`notes`);
