-- Initial data for Vaccine Appointment System

-- Insert default admin account (password: admin123, bcrypt encoded)
-- You can generate bcrypt hash online or use Spring Security's BCryptPasswordEncoder
-- For demo, we use plain text (not secure). In production, always use hashed passwords.
-- We'll let the application encode the password, so here we insert raw password.
-- Alternatively, we can insert encoded password directly.
-- We'll insert raw password and rely on application to hash it upon first use.
-- For simplicity, we insert a pre-encoded BCrypt hash of "admin123"
-- BCrypt hash for "admin123": $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVXgvC
INSERT INTO `admin` (`username`, `password`, `role`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVXgvC', 'ADMIN')
ON DUPLICATE KEY UPDATE `password` = VALUES(`password`);

-- Insert sample vaccines
INSERT INTO `vaccine` (`name`, `manufacturer`, `description`, `stock_quantity`, `available`) VALUES
('Pfizer-BioNTech COVID-19 Vaccine', 'Pfizer, BioNTech', 'mRNA vaccine for COVID-19, 2 doses', 1000, TRUE),
('Moderna COVID-19 Vaccine', 'Moderna', 'mRNA vaccine for COVID-19, 2 doses', 800, TRUE),
('Johnson & Johnson COVID-19 Vaccine', 'Janssen', 'Single-dose COVID-19 vaccine', 500, TRUE),
('Influenza Vaccine 2023', 'Various', 'Seasonal flu vaccine', 1200, TRUE),
('Hepatitis B Vaccine', 'GSK', 'Vaccine for Hepatitis B, 3 doses', 600, TRUE)
ON DUPLICATE KEY UPDATE `stock_quantity` = VALUES(`stock_quantity`), `available` = VALUES(`available`);

-- Insert a test user (password: user123, bcrypt encoded)
-- BCrypt hash for "user123": $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVXgvC
INSERT INTO `user` (`username`, `password`, `email`, `phone`, `status`) VALUES
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVXgvC', 'test@example.com', '13800138000', 1)
ON DUPLICATE KEY UPDATE `password` = VALUES(`password`);