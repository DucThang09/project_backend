package com.luvina.la.entity;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity đại diện cho bảng phòng ban.
 * Chứa thông tin về tên và ID của các phòng ban.
 */
@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
public class Department implements Serializable {

    /** Serial version UID cho việc tuần tự hóa. */
    private static final long serialVersionUID = 1L;

    /** ID duy nhất của phòng ban, tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Long departmentId;

    /** Tên của phòng ban. */
    @Column(name = "department_name")
    private String departmentName;
}