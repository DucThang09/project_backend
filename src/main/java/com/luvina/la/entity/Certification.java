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
import lombok.Data;

/**
 * Entity đại diện cho bảng chứng chỉ.
 * Chứa thông tin về tên chứng chỉ và cấp độ của nó.
 */
@Entity
@Table(name = "certifications")
@Data
public class Certification implements Serializable {

    /** Serial version UID cho việc tuần tự hóa. */
    private static final long serialVersionUID = 1L;

    /** ID duy nhất của chứng chỉ, tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certification_id")
    private Long certificationId;

    /** Tên của chứng chỉ. */
    @Column(name = "certification_name")
    private String certificationName;

    /** Cấp độ của chứng chỉ (số nguyên). */
    @Column(name = "certification_level")
    private Integer certificationLevel;
}
