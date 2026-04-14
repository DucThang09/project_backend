ALTER TABLE employees
    ADD COLUMN employee_role VARCHAR(20) NOT NULL DEFAULT 'USER' AFTER employee_login_password;

UPDATE employees
SET employee_role = 'ADMIN'
WHERE employee_login_id = 'admin';

UPDATE employees
SET employee_role = 'USER'
WHERE employee_login_id <> 'admin';
