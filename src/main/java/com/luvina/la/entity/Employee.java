package com.luvina.la.entity;

/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * Employee.java, April 13, 2026 tdthang
 */
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Entity đại diện cho bảng nhân viên. */
@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
public class Employee implements Serializable {

    private static final long serialVersionUID = 5771173953267484096L;

    @Id
    @Column(name = "employee_id")
    private Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "employee_name_kana")
    private String employeeNameKana;

    @Column(name = "employee_birth_date")
    private LocalDate employeeBirthDate;

    @Column(name = "employee_email")
    private String employeeEmail;

    @Column(name = "employee_telephone")
    private String employeeTelephone;

    @Column(name = "employee_login_id")
    private String employeeLoginId;

    @Column(name = "employee_login_password")
    private String employeeLoginPassword;

    @Column(name = "employee_role")
    private String role;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<EmployeeCertification> employeeCertifications;
}
