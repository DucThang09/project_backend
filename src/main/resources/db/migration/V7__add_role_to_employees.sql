-- Migration V7: Add role column to employees table
-- This migration adds an employee_role column and sets default roles for existing employees
-- Created on: April 13, 2026
-- Author: tdthang

ALTER TABLE employees
    ADD COLUMN employee_role VARCHAR(20) NOT NULL DEFAULT 'USER' AFTER employee_login_password;

-- Set admin role for the admin user
UPDATE employees
SET employee_role = 'ADMIN'
WHERE employee_login_id = 'admin';

-- Set user role for all other employees
UPDATE employees
SET employee_role = 'USER'
WHERE employee_login_id <> 'admin';
