package com.luvina.la.entity;

/**
 * Entity đại diện cho bảng nhân viên trong cơ sở dữ liệu.
 * Lưu trữ thông tin cá nhân và công việc của nhân viên.
 *
 * @author tdthang
 * @version 1.0
 * @since April 13, 2026
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

/**
 * Entity đại diện cho bảng nhân viên.
 * Chứa thông tin chi tiết về nhân viên bao gồm thông tin cá nhân,
 * thông tin đăng nhập và mối quan hệ với phòng ban và chứng chỉ.
 */
@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
public class Employee implements Serializable {

    /** Serial version UID cho việc tuần tự hóa. */
    private static final long serialVersionUID = 5771173953267484096L;

    /** ID duy nhất của nhân viên. */
    @Id
    @Column(name = "employee_id")
    private Long employeeId;

    /** Phòng ban mà nhân viên thuộc về. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    /** Tên đầy đủ của nhân viên. */
    @Column(name = "employee_name")
    private String employeeName;

    /** Tên nhân viên bằng chữ kana (Tiếng Nhật). */
    @Column(name = "employee_name_kana")
    private String employeeNameKana;

    /** Ngày sinh của nhân viên. */
    @Column(name = "employee_birth_date")
    private LocalDate employeeBirthDate;

    /** Địa chỉ email của nhân viên. */
    @Column(name = "employee_email")
    private String employeeEmail;

    /** Số điện thoại của nhân viên. */
    @Column(name = "employee_telephone")
    private String employeeTelephone;

    /** ID đăng nhập của nhân viên. */
    @Column(name = "employee_login_id")
    private String employeeLoginId;

    /** Mật khẩu đăng nhập của nhân viên (đã mã hóa). */
    @Column(name = "employee_login_password")
    private String employeeLoginPassword;

    /** Vai trò của nhân viên trong hệ thống. */
    @Column(name = "employee_role")
    private String role;

    /** Danh sách chứng chỉ của nhân viên. */
    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<EmployeeCertification> employeeCertifications;
}
