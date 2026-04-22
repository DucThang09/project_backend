-- Migration V1: Initialize database schema
-- This migration creates the initial tables for the employee management system
-- Created on: April 13, 2026
-- Author: tdthang

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS employee_certifications;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS certifications;
DROP TABLE IF EXISTS departments;

SET FOREIGN_KEY_CHECKS = 1;

-- Create departments table
CREATE TABLE departments (
    department_id BIGINT NOT NULL AUTO_INCREMENT,
    department_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (department_id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create certifications table
CREATE TABLE certifications (
    certification_id BIGINT NOT NULL AUTO_INCREMENT,
    certification_name VARCHAR(255) NOT NULL,
    certification_level INT NOT NULL,
    PRIMARY KEY (certification_id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create employees table
CREATE TABLE employees (
    employee_id BIGINT NOT NULL AUTO_INCREMENT,
    department_id BIGINT NOT NULL,
    employee_name VARCHAR(255) NOT NULL,
    employee_name_kana VARCHAR(255) NULL,
    employee_birth_date DATE NULL,
    employee_email VARCHAR(255) NOT NULL,
    employee_telephone VARCHAR(50) NULL,
    employee_login_id VARCHAR(50) NOT NULL,
    employee_login_password VARCHAR(100) DEFAULT NULL,
    employee_role VARCHAR(20) NOT NULL DEFAULT 'USER',
    PRIMARY KEY (employee_id) USING BTREE,
    CONSTRAINT fk_employees_departments
        FOREIGN KEY (department_id) REFERENCES departments (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create employee_certifications table
CREATE TABLE employee_certifications (
    employee_certification_id BIGINT NOT NULL AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    certification_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    score DECIMAL(5,2) NOT NULL,
    PRIMARY KEY (employee_certification_id) USING BTREE,
    CONSTRAINT fk_employee_certifications_employee
        FOREIGN KEY (employee_id) REFERENCES employees (employee_id),
    CONSTRAINT fk_employee_certifications_certification
        FOREIGN KEY (certification_id) REFERENCES certifications (certification_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Insert initial data for departments
INSERT INTO departments (department_id, department_name)
VALUES
    (1, 'Admin'),
    (2, 'Development'),
    (3, 'QA'),
    (4, 'HR');

-- Insert initial data for certifications
INSERT INTO certifications (certification_id, certification_name, certification_level)
VALUES
    (1, 'Trình độ tiếng Nhật cấp 1', 1),
    (2, 'Trình độ tiếng Nhật cấp 2', 2),
    (3, 'Trình độ tiếng Nhật cấp 3', 3),
    (4, 'Trình độ tiếng Nhật cấp 4', 4),
    (5, 'Trình độ tiếng Nhật cấp 5', 5);

-- Insert initial data for employees
INSERT INTO employees (
    employee_id,
    department_id,
    employee_name,
    employee_name_kana,
    employee_birth_date,
    employee_email,
    employee_telephone,
    employee_login_id,
    employee_login_password,
    employee_role
)
VALUES
    (1, 1, 'Administrator', 'Administrator', '1985-01-01', 'la@luvina.net', '0901999999', 'admin', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'ADMIN'),
    (2, 2, 'Nguyen Van An', 'Nguyen Van An', '1990-01-15', 'an01@example.com', '0901000001', 'user01', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (3, 2, 'Tran Thi Binh', 'Tran Thi Binh', '1991-02-18', 'binh02@example.com', '0901000002', 'user02', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (4, 2, 'Le Hoang Chau', 'Le Hoang Chau', '1992-03-20', 'chau03@example.com', '0901000003', 'user03', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (5, 2, 'Pham Gia Duy', 'Pham Gia Duy', '1989-04-22', 'duy04@example.com', '0901000004', 'user04', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (6, 2, 'Vo Minh Giang', 'Vo Minh Giang', '1993-05-11', 'giang05@example.com', '0901000005', 'user05', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (7, 2, 'Bui Thu Ha', 'Bui Thu Ha', '1994-06-30', 'ha06@example.com', '0901000006', 'user06', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (8, 2, 'Dang Quoc Hieu', 'Dang Quoc Hieu', '1990-07-14', 'hieu07@example.com', '0901000007', 'user07', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (9, 2, 'Doan Thanh Khoa', 'Doan Thanh Khoa', '1988-08-09', 'khoa08@example.com', '0901000008', 'user08', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (10, 3, 'Huynh Bao Lam', 'Huynh Bao Lam', '1995-09-01', 'lam09@example.com', '0901000009', 'user09', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (11, 3, 'Ngo Ngoc Minh', 'Ngo Ngoc Minh', '1991-10-05', 'minh10@example.com', '0901000010', 'user10', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (12, 3, 'Phan Thanh Nam', 'Phan Thanh Nam', '1992-11-19', 'nam11@example.com', '0901000011', 'user11', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (13, 3, 'Ly Kim Oanh', 'Ly Kim Oanh', '1993-12-03', 'oanh12@example.com', '0901000012', 'user12', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (14, 3, 'Mai Phuong Phuc', 'Mai Phuong Phuc', '1987-01-27', 'phuc13@example.com', '0901000013', 'user13', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (15, 3, 'Ta Nhat Quan', 'Ta Nhat Quan', '1990-02-12', 'quan14@example.com', '0901000014', 'user14', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (16, 4, 'Ton Nu Quynh', 'Ton Nu Quynh', '1994-03-17', 'quynh15@example.com', '0901000015', 'user15', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (17, 4, 'Vu Anh Son', 'Vu Anh Son', '1989-04-25', 'son16@example.com', '0901000016', 'user16', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (18, 4, 'Truong Gia Tam', 'Truong Gia Tam', '1991-05-08', 'tam17@example.com', '0901000017', 'user17', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (19, 4, 'Cao Thanh Uyen', 'Cao Thanh Uyen', '1995-06-18', 'uyen18@example.com', '0901000018', 'user18', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (20, 4, 'Kieu Bao Van', 'Kieu Bao Van', '1992-07-29', 'van19@example.com', '0901000019', 'user19', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (21, 4, 'Luong Duc Xuan', 'Luong Duc Xuan', '1988-08-16', 'xuan20@example.com', '0901000020', 'user20', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (22, 2, 'Nguyen Trung Kien', 'Nguyen Trung Kien', '1992-02-05', 'kien32@example.com', '0901000032', 'user32', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (23, 2, 'Nguyen Trung Kien', 'Nguyen Trung Kien', '1993-03-10', 'kien33@example.com', '0901000033', 'user33', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (24, 3, 'Nguyen Trung Kien', 'Nguyen Trung Kien', '1991-04-15', 'kien34@example.com', '0901000034', 'user34', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (25, 3, 'Nguyen Trung Kien', 'Nguyen Trung Kien', '1990-05-20', 'kien35@example.com', '0901000035', 'user35', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER'),
    (26, 4, 'Nguyen Trung Kien', 'Nguyen Trung Kien', '1994-06-25', 'kien36@example.com', '0901000036', 'user36', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy', 'USER');

INSERT INTO employee_certifications (
    employee_certification_id,
    employee_id,
    certification_id,
    start_date,
    end_date,
    score
)
VALUES
    (1, 2, 5, '2023-01-01', '2026-01-01', 450.00),
    (2, 3, 4, '2023-02-01', '2026-02-01', 520.00),
    (3, 4, 3, '2023-03-01', '2026-03-01', 610.00),
    (4, 5, 2, '2023-04-01', '2026-04-01', 710.00),
    (5, 6, 1, '2023-05-01', '2026-05-01', 820.00),
    (6, 7, 5, '2023-06-01', '2026-06-01', 430.00),
    (7, 8, 4, '2023-07-01', '2026-07-01', 540.00),
    (8, 9, 3, '2023-08-01', '2026-08-01', 640.00),
    (9, 10, 2, '2023-09-01', '2026-09-01', 730.00),
    (10, 11, 1, '2023-10-01', '2026-10-01', 850.00),
    (11, 12, 5, '2023-11-01', '2026-11-01', 460.00),
    (12, 13, 4, '2023-12-01', '2026-12-01', 560.00),
    (13, 14, 3, '2024-01-01', '2027-01-01', 620.00),
    (14, 15, 2, '2024-02-01', '2027-02-01', 740.00),
    (15, 16, 1, '2024-03-01', '2027-03-01', 880.00),
    (16, 17, 5, '2024-04-01', '2027-04-01', 470.00),
    (17, 18, 4, '2024-05-01', '2027-05-01', 550.00),
    (18, 19, 3, '2024-06-01', '2027-06-01', 630.00),
    (19, 20, 2, '2024-07-01', '2027-07-01', 720.00),
    (20, 21, 1, '2024-08-01', '2027-08-01', 810.00),
    (21, 22, 3, '2024-09-01', '2026-09-30', 605.00),
    (22, 23, 3, '2024-10-01', '2026-10-31', 615.00),
    (23, 24, 3, '2024-11-01', '2026-11-30', 625.00),
    (24, 25, 2, '2024-12-01', '2027-01-31', 735.00),
    (25, 26, 1, '2025-01-01', '2027-02-28', 845.00);

ALTER TABLE departments AUTO_INCREMENT = 5;
ALTER TABLE certifications AUTO_INCREMENT = 6;
ALTER TABLE employees AUTO_INCREMENT = 27;
ALTER TABLE employee_certifications AUTO_INCREMENT = 26;
