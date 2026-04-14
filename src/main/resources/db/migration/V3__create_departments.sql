CREATE TABLE IF NOT EXISTS `departments` (
    department_id BIGINT NOT NULL AUTO_INCREMENT,
    department_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (`department_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `departments` (department_name)
VALUES ('Admin');

ALTER TABLE `employees`
ADD CONSTRAINT `fk_employees_departments`
FOREIGN KEY (`department_id`) REFERENCES `departments` (`department_id`);
