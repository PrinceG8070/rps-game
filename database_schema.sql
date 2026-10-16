-- ========================================================
-- Rock Paper Scissors (RPS) Database Schema for MySQL 8.0
-- Database: rps_db
-- ========================================================

CREATE DATABASE IF NOT EXISTS `rps_db` 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE `rps_db`;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `email` VARCHAR(100),
    `role` VARCHAR(20) DEFAULT 'ROLE_USER',
    `created_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB;

-- 2. Game Scores Table (One-to-One with Users)
CREATE TABLE IF NOT EXISTS `game_scores` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL UNIQUE,
    `wins` INT NOT NULL DEFAULT 0,
    `losses` INT NOT NULL DEFAULT 0,
    `ties` INT NOT NULL DEFAULT 0,
    `updated_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT `fk_game_scores_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3. Game Rounds History Table (One-to-Many with Users)
CREATE TABLE IF NOT EXISTS `game_rounds` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `player_move` VARCHAR(20) NOT NULL,
    `computer_move` VARCHAR(20) NOT NULL,
    `result` VARCHAR(10) NOT NULL,
    `played_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT `fk_game_rounds_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_user_played_at` (`user_id`, `played_at` DESC)
) ENGINE=InnoDB;

-- 4. Login Audit Logs Table
CREATE TABLE IF NOT EXISTS `login_logs` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NULL,
    `username` VARCHAR(50) NOT NULL,
    `ip_address` VARCHAR(45),
    `success` BOOLEAN NOT NULL DEFAULT TRUE,
    `timestamp` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT `fk_login_logs_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL,
    INDEX `idx_log_timestamp` (`timestamp` DESC)
) ENGINE=InnoDB;

-- Sample Seed Data (Optional)
INSERT IGNORE INTO `users` (`id`, `username`, `password`, `email`) 
VALUES (1, 'demo', '$2a$10$w1qZ/b8Qo0R8gY4K1t3UgeP6E9R9e6r3w4f5h6j7k8l9z0x1c2v3b', 'demo@example.com');

INSERT IGNORE INTO `game_scores` (`user_id`, `wins`, `losses`, `ties`) 
VALUES (1, 5, 2, 1);
