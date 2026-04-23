package com.luvina.la.service.impl;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */
import com.luvina.la.config.Constants;
import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.entity.Certification;
import com.luvina.la.entity.Department;
import com.luvina.la.entity.Employee;
import com.luvina.la.entity.EmployeeCertification;
import com.luvina.la.payload.EmployeeValidationRequest;
import com.luvina.la.repository.CertificationRepository;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.repository.EmployeeCertificationRepository;
import com.luvina.la.repository.EmployeeRepository;
import com.luvina.la.service.EmployeeService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final CertificationRepository certificationRepository;
    private final EmployeeCertificationRepository employeeCertificationRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor để inject các dependency cần dùng cho luồng employee.
     *
     * @param employeeRepository repository truy cập dữ liệu nhân viên
     * @param departmentRepository repository truy cập dữ liệu phòng ban
     * @param certificationRepository repository truy cập dữ liệu chứng chỉ
     * @param employeeCertificationRepository repository truy cập dữ liệu employee certification
     * @param passwordEncoder component mã hóa mật khẩu trước khi lưu
     */
    public EmployeeServiceImpl(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            CertificationRepository certificationRepository,
            EmployeeCertificationRepository employeeCertificationRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.certificationRepository = certificationRepository;
        this.employeeCertificationRepository = employeeCertificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Đếm tổng số nhân viên theo điều kiện tìm kiếm.
     *
     * @param departmentId ID phòng ban cần lọc
     * @param employeeName tên nhân viên cần tìm
     * @return tổng số bản ghi phù hợp
     */
    @Override
    public Long getTotalEmployees(Long departmentId, String employeeName) {
        return employeeRepository.countTotalEmployees(departmentId, employeeName);
    }

    /**
     * Lấy danh sách nhân viên theo điều kiện tìm kiếm, sắp xếp và phân trang.
     *
     * @param departmentId ID phòng ban cần lọc
     * @param employeeName tên nhân viên cần tìm
     * @param ordEmployeeName thứ tự sắp xếp theo tên nhân viên
     * @param ordCertificationName thứ tự sắp xếp theo tên chứng chỉ
     * @param ordEndDate thứ tự sắp xếp theo ngày hết hạn chứng chỉ
     * @param offset vị trí bắt đầu lấy dữ liệu
     * @param limit số bản ghi tối đa cần lấy
     * @return danh sách nhân viên phù hợp
     */
    @Override
    public List<EmployeeDTO> getEmployees(
            Long departmentId,
            String employeeName,
            String ordEmployeeName,
            String ordCertificationName,
            String ordEndDate,
            Integer offset,
            Integer limit) {

        // Nếu offset/limit không hợp lệ thì quay về giá trị mặc định.
        int finalLimit = (limit == null || limit <= 0) ? Constants.DEFAULT_LIMIT : limit;
        int finalOffset = (offset == null || offset < 0) ? Constants.DEFAULT_OFFSET : offset;

        List<Object[]> results = employeeRepository.findEmployees(
                departmentId,
                employeeName,
                ordEmployeeName,
                ordCertificationName,
                ordEndDate,
                finalLimit,
                finalOffset
        );

        List<EmployeeDTO> dtos = new ArrayList<>(results.size());
        for (Object[] row : results) {
            dtos.add(EmployeeDTO.fromRow(row));
        }
        return dtos;
    }

    /**
     * Thêm mới nhân viên và chứng chỉ nếu request có chọn chứng chỉ.
     *
     * @param request dữ liệu nhân viên đã được validate
     */
    @Override
    @Transactional
    public void addEmployee(EmployeeValidationRequest request) {
        // Luồng thêm mới luôn tạo employee trước, sau đó mới gắn chứng chỉ.
        Employee employee = new Employee();
        applyEmployeeValues(employee, request);
        employee.setRole("USER");
        Employee savedEmployee = employeeRepository.save(employee);
        replaceCertification(savedEmployee, request);
    }

    /**
     * Cập nhật nhân viên và ghi đè lại thông tin chứng chỉ hiện tại.
     *
     * @param employeeId ID nhân viên cần cập nhật
     * @param request dữ liệu nhân viên đã được validate
     */
    @Override
    @Transactional
    public void updateEmployee(Long employeeId, EmployeeValidationRequest request) {
        // Luồng cập nhật dùng lại employee hiện có rồi ghi đè thông tin mới.
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        applyEmployeeValues(employee, request);
        employeeRepository.save(employee);
        replaceCertification(employee, request);
    }

    private void applyEmployeeValues(Employee employee, EmployeeValidationRequest request) {
        // Tìm phòng ban hợp lệ rồi gán lại toàn bộ thông tin cơ bản của nhân viên.
        Department department = departmentRepository.findById(Long.parseLong(request.getDepartmentId().trim()))
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        employee.setDepartment(department);
        employee.setEmployeeName(request.getEmployeeName().trim());
        employee.setEmployeeNameKana(request.getEmployeeNameKana().trim());
        employee.setEmployeeBirthDate(LocalDate.parse(request.getEmployeeBirthDate().trim()));
        employee.setEmployeeEmail(request.getEmployeeEmail().trim());
        employee.setEmployeeTelephone(request.getEmployeeTelephone().trim());
        employee.setEmployeeLoginId(request.getEmployeeLoginId().trim());
        employee.setEmployeeLoginPassword(passwordEncoder.encode(request.getEmployeeLoginPassword()));
        if (employee.getRole() == null || employee.getRole().isBlank()) {
            employee.setRole("USER");
        }
    }

    private void replaceCertification(Employee employee, EmployeeValidationRequest request) {
        // Xóa toàn bộ chứng chỉ cũ trước khi ghi lại trạng thái mới nhất.
        employeeCertificationRepository.deleteByEmployeeEmployeeId(employee.getEmployeeId());

        String certificationId = request.getCertificationId() == null ? "" : request.getCertificationId().trim();
        if (certificationId.isEmpty()) {
            // Không chọn chứng chỉ thì chỉ cần giữ employee không có certification.
            return;
        }

        Certification certification = certificationRepository.findById(Long.parseLong(certificationId))
                .orElseThrow(() -> new IllegalArgumentException("Certification not found"));

        // Tạo lại bản ghi chứng chỉ theo dữ liệu vừa submit.
        EmployeeCertification employeeCertification = new EmployeeCertification();
        employeeCertification.setEmployee(employee);
        employeeCertification.setCertification(certification);
        employeeCertification.setStartDate(LocalDate.parse(request.getCertificationStartDate().trim()));
        employeeCertification.setEndDate(LocalDate.parse(request.getCertificationEndDate().trim()));
        employeeCertification.setScore(new BigDecimal(request.getScore().trim()));
        employeeCertificationRepository.save(employeeCertification);
    }
}
