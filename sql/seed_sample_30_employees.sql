-- Run this after the application starts in dev mode with Hibernate create-drop.
-- It seeds base master data plus 21 visible employees for list/search/sort testing.

DELETE FROM employee_certifications;
DELETE FROM employees;
DELETE FROM certifications;
DELETE FROM departments;

INSERT INTO departments (department_id, department_name)
VALUES
    (1, 'Admin'),
    (2, 'Development'),
    (3, 'QA'),
    (4, 'HR');

INSERT INTO certifications (certification_id, certification_name, certification_level)
VALUES
    (1, 'Trinh do tieng nhat cap 1', 1),
    (2, 'Trinh do tieng nhat cap 2', 2),
    (3, 'Trinh do tieng nhat cap 3', 3),
    (4, 'Trinh do tieng nhat cap 4', 4),
    (5, 'Trinh do tieng nhat cap 5', 5);

INSERT INTO employees (
    employee_id,
    department_id,
    employee_name,
    employee_name_kana,
    employee_birth_date,
    employee_email,
    employee_telephone,
    employee_login_id,
    employee_login_password
)
VALUES
    (1, 1, 'Administrator', 'Administrator', '1990-01-01', 'la@luvina.net', '0900000000', 'admin', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    (2, 2, 'Nguyen Van An', 'Nguyen Van An', '1990-01-15', 'an01@example.com', '0901000001', 'user01', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    (3, 2, 'Tran Thi Binh', 'Tran Thi Binh', '1991-02-18', 'binh02@example.com', '0901000002', 'user02', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    (4, 2, 'Le Hoang Chau', 'Le Hoang Chau', '1992-03-20', 'chau03@example.com', '0901000003', 'user03', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    (5, 2, 'Pham Gia Duy', 'Pham Gia Duy', '1989-04-22', 'duy04@example.com', '0901000004', 'user04', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    (6, 2, 'Vo Minh Giang', 'Vo Minh Giang', '1993-05-11', 'giang05@example.com', '0901000005', 'user05', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    (7, 2, 'Bui Thu Ha', 'Bui Thu Ha', '1994-06-30', 'ha06@example.com', '0901000006', 'user06', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    (8, 2, 'Dang Quoc Hieu', 'Dang Quoc Hieu', '1990-07-14', 'hieu07@example.com', '0901000007', 'user07', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    (9, 2, 'Doan Thanh Khoa', 'Doan Thanh Khoa', '1988-08-09', 'khoa08@example.com', '0901000008', 'user08', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    (10, 3, 'Huynh Bao Lam', 'Huynh Bao Lam', '1995-09-01', 'lam09@example.com', '0901000009', 'user09', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    (11, 3, 'Ngo Ngoc Minh', 'Ngo Ngoc Minh', '1991-10-05', 'minh10@example.com', '0901000010', 'user10', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    (12, 3, 'Phan Thanh Nam', 'Phan Thanh Nam', '1992-11-19', 'nam11@example.com', '0901000011', 'user11', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    (13, 3, 'Ly Kim Oanh', 'Ly Kim Oanh', '1993-12-03', 'oanh12@example.com', '0901000012', 'user12', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    (14, 3, 'Mai Phuong Phuc', 'Mai Phuong Phuc', '1987-01-27', 'phuc13@example.com', '0901000013', 'user13', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    (15, 3, 'Ta Nhat Quan', 'Ta Nhat Quan', '1990-02-12', 'quan14@example.com', '0901000014', 'user14', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    (16, 4, 'Ton Nu Quynh', 'Ton Nu Quynh', '1994-03-17', 'quynh15@example.com', '0901000015', 'user15', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    (17, 4, 'Vu Anh Son', 'Vu Anh Son', '1989-04-25', 'son16@example.com', '0901000016', 'user16', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    (18, 4, 'Truong Gia Tam', 'Truong Gia Tam', '1991-05-08', 'tam17@example.com', '0901000017', 'user17', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    (19, 4, 'Cao Thanh Uyen', 'Cao Thanh Uyen', '1995-06-18', 'uyen18@example.com', '0901000018', 'user18', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    (20, 4, 'Kieu Bao Van', 'Kieu Bao Van', '1992-07-29', 'van19@example.com', '0901000019', 'user19', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    (21, 2, 'Luong Duc Xuan', 'Luong Duc Xuan', '1988-08-16', 'xuan20@example.com', '0901000020', 'user20', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy'),
    (22, 3, 'Nguyen Bao Thu', 'Nguyen Bao Thu', '1992-09-09', 'thu21@example.com', '0901000021', 'user21', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy');

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
    (15, 16, 1, '2024-03-01', '2027-03-01', 880.00);
