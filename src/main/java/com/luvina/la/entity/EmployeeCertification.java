/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * EmployeeCertification.java, 10/05/2026 tdthang
 */
package com.luvina.la.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * Entity đại diện cho bảng employees_certifications.
 * Liên kết nhân viên với chứng chỉ của họ, bao gồm ngày bắt đầu,
 * ngày kết thúc và điểm số đạt được.
 * @author tdthang
 */
@Entity
@Table(name = "employees_certifications")
@Data
public class EmployeeCertification implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_certification_id")
    private Long employeeCertificationId;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
    @ManyToOne
    @JoinColumn(name = "certification_id")
    private Certification certification;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "score")
    private BigDecimal score;
}
