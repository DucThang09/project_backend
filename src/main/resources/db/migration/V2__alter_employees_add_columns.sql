ALTER TABLE employees
    MODIFY COLUMN employee_name VARCHAR(255) NOT NULL,
    MODIFY COLUMN employee_email VARCHAR(255) NOT NULL,
    ADD COLUMN employee_name_kana VARCHAR(255) NULL AFTER employee_name,
    ADD COLUMN employee_birth_date DATE NULL AFTER employee_name_kana,
    ADD COLUMN employee_telephone VARCHAR(50) NULL AFTER employee_email;
