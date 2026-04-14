CREATE TABLE IF NOT EXISTS `employee_certifications` (
    employee_certification_id BIGINT NOT NULL AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    certification_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    score DECIMAL(5,2) NOT NULL,
    PRIMARY KEY (`employee_certification_id`) USING BTREE,
    CONSTRAINT `fk_employee_certifications_employee`
        FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`),
    CONSTRAINT `fk_employee_certifications_certification`
        FOREIGN KEY (`certification_id`) REFERENCES `certifications` (`certification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
