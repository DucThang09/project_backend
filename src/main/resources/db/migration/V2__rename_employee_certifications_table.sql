SET @old_table_exists = (
    SELECT COUNT(*)
    FROM information_schema.tables
    WHERE table_schema = DATABASE()
      AND table_name = 'employee_certifications'
);

SET @new_table_exists = (
    SELECT COUNT(*)
    FROM information_schema.tables
    WHERE table_schema = DATABASE()
      AND table_name = 'employees_certifications'
);

SET @rename_sql = IF(
    @old_table_exists = 1 AND @new_table_exists = 0,
    'RENAME TABLE employee_certifications TO employees_certifications',
    'SELECT 1'
);

PREPARE rename_statement FROM @rename_sql;
EXECUTE rename_statement;
DEALLOCATE PREPARE rename_statement;
