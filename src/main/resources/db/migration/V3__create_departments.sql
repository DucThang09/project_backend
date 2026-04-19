-- Migration V3: Create departments table and add foreign key
-- This migration creates the departments table and establishes foreign key relationship with employees
-- Created on: April 13, 2026
-- Author: tdthang

CREATE TABLE IF NOT EXISTS `departments` (
    department_id BIGINT NOT NULL AUTO_INCREMENT,
    department_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (`department_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Insert default department
INSERT INTO `departments` (department_name)
VALUES ('Admin');

-- Add foreign key constraint to employees table
ALTER TABLE `employees`
ADD CONSTRAINT `fk_employees_departments`
FOREIGN KEY (`department_id`) REFERENCES `departments` (`department_id`);
