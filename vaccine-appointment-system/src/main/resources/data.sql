-- Initial data for Vaccine Appointment System
-- All passwords are bcrypt encoded: "admin123" and "user123"

-- Insert default admin account
-- BCrypt hash for "admin123": $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVXgvC
INSERT INTO `admin` (`username`, `password`, `role`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVXgvC', 'ADMIN'),
('superadmin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVXgvC', 'SUPER_ADMIN')
ON DUPLICATE KEY UPDATE `password` = VALUES(`password`), `role` = VALUES(`role`);

-- Insert sample vaccines
INSERT INTO `vaccine` (`name`, `manufacturer`, `description`, `stock_quantity`, `available`, `image_url`) VALUES
('Pfizer-BioNTech COVID-19 Vaccine', 'Pfizer, BioNTech', 'mRNA vaccine for COVID-19, requires 2 doses 21 days apart', 1000, TRUE, 'https://images.unsplash.com/photo-1584467735871-8db9ac8d091c?w=400&h=300&fit=crop'),
('Moderna COVID-19 Vaccine', 'Moderna', 'mRNA vaccine for COVID-19, requires 2 doses 28 days apart', 800, TRUE, 'https://images.unsplash.com/photo-1584467735871-8db9ac8d091c?w=400&h=300&fit=crop'),
('Johnson & Johnson COVID-19 Vaccine', 'Janssen', 'Single-dose COVID-19 vaccine', 500, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w-400&h=300&fit=crop'),
('Influenza Vaccine 2023', 'Various manufacturers', 'Seasonal flu vaccine for 2023-2024 season', 1200, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop'),
('Hepatitis B Vaccine', 'GSK', 'Vaccine for Hepatitis B, requires 3 doses over 6 months', 600, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop'),
('HPV Vaccine (Gardasil 9)', 'Merck', 'Human Papillomavirus vaccine, 2-3 doses depending on age', 400, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop'),
('Tetanus-Diphtheria-Pertussis (Tdap)', 'Sanofi', 'Combination vaccine for tetanus, diphtheria, and pertussis', 900, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop'),
('Measles-Mumps-Rubella (MMR)', 'Merck', 'Combination vaccine for measles, mumps, and rubella', 700, TRUE, 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop')
ON DUPLICATE KEY UPDATE `stock_quantity` = VALUES(`stock_quantity`), `available` = VALUES(`available`), `image_url` = VALUES(`image_url`);

-- Insert sample users
-- BCrypt hash for "user123": $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVXgvC
INSERT INTO `user` (`username`, `password`, `email`, `phone`, `role`, `status`) VALUES
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVXgvC', 'test@example.com', '13800138000', 'USER', 1),
('john_doe', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVXgvC', 'john.doe@example.com', '13900139000', 'USER', 1),
('jane_smith', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVXgvC', 'jane.smith@example.com', '13700137000', 'USER', 1),
('robert_johnson', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVXgvC', 'robert.j@example.com', '13600136000', 'USER', 1),
('lisa_wang', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVXgvC', 'lisa.wang@example.com', '13500135000', 'USER', 1),
('admin_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVXgvC', 'admin.user@example.com', '13400134000', 'ADMIN', 1)
ON DUPLICATE KEY UPDATE `password` = VALUES(`password`), `role` = VALUES(`role`), `status` = VALUES(`status`);

-- Insert sample appointments
-- Note: Using user IDs 1-6 and vaccine IDs 1-8
INSERT INTO `appointment` (`user_id`, `vaccine_id`, `appointment_time`, `status`) VALUES
(1, 1, DATE_ADD(NOW(), INTERVAL 7 DAY), 0), -- testuser, Pfizer, pending
(2, 3, DATE_ADD(NOW(), INTERVAL 3 DAY), 1), -- john_doe, J&J, confirmed
(3, 2, DATE_ADD(NOW(), INTERVAL 5 DAY), 0), -- jane_smith, Moderna, pending
(4, 5, DATE_ADD(NOW(), INTERVAL 10 DAY), 2), -- robert_johnson, Hepatitis B, completed
(5, 4, DATE_ADD(NOW(), INTERVAL 2 DAY), 3), -- lisa_wang, Influenza, cancelled
(6, 6, DATE_ADD(NOW(), INTERVAL 14 DAY), 1), -- admin_user, HPV, confirmed
(1, 7, DATE_ADD(NOW(), INTERVAL 21 DAY), 0), -- testuser, Tdap, pending
(2, 8, DATE_ADD(NOW(), INTERVAL 30 DAY), 1)  -- john_doe, MMR, confirmed
ON DUPLICATE KEY UPDATE `status` = VALUES(`status`);

-- Insert sample vaccination records
-- Note: Appointment IDs 1-8 correspond to appointments above
INSERT INTO `vaccination_record` (`appointment_id`, `user_id`, `vaccine_id`, `vaccination_time`, `status`, `notes`) VALUES
(4, 4, 5, DATE_SUB(NOW(), INTERVAL 5 DAY), 1, 'First dose administered. Patient tolerated well.'),
(6, 6, 6, DATE_SUB(NOW(), INTERVAL 2 DAY), 1, 'Routine vaccination. No adverse reactions.'),
(2, 2, 3, DATE_SUB(NOW(), INTERVAL 1 DAY), 1, 'Single-dose COVID vaccine. Mild sore arm reported.'),
(8, 2, 8, DATE_ADD(NOW(), INTERVAL 30 DAY), 0, 'Scheduled for MMR vaccination.')
ON DUPLICATE KEY UPDATE `status` = VALUES(`status`), `notes` = VALUES(`notes`);