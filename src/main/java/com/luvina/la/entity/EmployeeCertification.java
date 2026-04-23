package com.luvina.la.entity;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */
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
 * Entity đại diện cho bảng employee_certifications.
 * Liên kết nhân viên với chứng chỉ của họ, bao gồm ngày bắt đầu,
 * ngày kết thúc và điểm số đạt được.
 */
@Entity
@Table(name = "employee_certifications")
@Data
public class EmployeeCertification implements Serializable {

    /** Serial version UID cho việc tuần tự hóa. */
    private static final long serialVersionUID = 1L;

    /** ID duy nhất của mối quan hệ nhân viên-chứng chỉ, tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_certification_id")
    private Long employeeCertificationId;

    /** Nhân viên sở hữu chứng chỉ này. */
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    /** Chứng chỉ mà nhân viên sở hữu. */
    @ManyToOne
    @JoinColumn(name = "certification_id")
    private Certification certification;

    /** Ngày bắt đầu hiệu lực của chứng chỉ. */
    @Column(name = "start_date")
    private LocalDate startDate;

    /** Ngày kết thúc hiệu lực của chứng chỉ. */
    @Column(name = "end_date")
    private LocalDate endDate;

    /** Điểm số đạt được trong kỳ thi chứng chỉ. */
    @Column(name = "score")
    private BigDecimal score;
}
