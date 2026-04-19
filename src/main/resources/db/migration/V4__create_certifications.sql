-- Migration V4: Create certifications table
-- This migration creates the certifications table and inserts initial certification data
-- Created on: April 13, 2026
-- Author: tdthang

CREATE TABLE IF NOT EXISTS `certifications` (
    certification_id BIGINT NOT NULL AUTO_INCREMENT,
    certification_name VARCHAR(50) NOT NULL,
    certification_level INT NOT NULL,
    PRIMARY KEY (`certification_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Insert initial certification data
INSERT INTO `certifications` (certification_id, certification_name, certification_level)
VALUES
    (1, 'Trình độ tiếng nhật cấp 1', 1),
    (2, 'Trình độ tiếng nhật cấp 2', 2),
    (3, 'Trình độ tiếng nhật cấp 3', 3),
    (4, 'Trình độ tiếng nhật cấp 4', 4),
    (5, 'Trình độ tiếng nhật cấp 5', 5);
