-- Run this after the application starts in dev mode with Hibernate create-drop.
-- It keeps only master data and a single admin account.

DELETE FROM employee_certifications;
DELETE FROM employees;
DELETE FROM certifications;
DELETE FROM departments;

INSERT INTO departments (department_id, department_name)
VALUES (1, 'Admin');

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
    (1, 1, 'Administrator', 'Administrator', '1990-01-01', 'la@luvina.net', '0900000000', 'admin', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy');
