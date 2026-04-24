package com.luvina.la.service.impl;

import com.luvina.la.config.Constants;
import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.dto.EmployeeDetailDTO;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
     * Constructor de inject cac dependency can dung cho luong employee.
     *
     * @param employeeRepository repository truy cap du lieu nhan vien
     * @param departmentRepository repository truy cap du lieu phong ban
     * @param certificationRepository repository truy cap du lieu chung chi
     * @param employeeCertificationRepository repository truy cap du lieu employee certification
     * @param passwordEncoder component ma hoa mat khau truoc khi luu
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
     * Dem tong so nhan vien theo dieu kien tim kiem.
     *
     * @param departmentId ID phong ban can loc
     * @param employeeName ten nhan vien can tim
     * @return tong so ban ghi phu hop
     */
    @Override
    public Long getTotalEmployees(Long departmentId, String employeeName) {
        return employeeRepository.countTotalEmployees(departmentId, employeeName);
    }

    /**
     * Lay danh sach nhan vien theo dieu kien tim kiem, sap xep va phan trang.
     *
     * @param departmentId ID phong ban can loc
     * @param employeeName ten nhan vien can tim
     * @param ordEmployeeName thu tu sap xep theo ten nhan vien
     * @param ordCertificationName thu tu sap xep theo ten chung chi
     * @param ordEndDate thu tu sap xep theo ngay het han chung chi
     * @param offset vi tri bat dau lay du lieu
     * @param limit so ban ghi toi da can lay
     * @return danh sach nhan vien phu hop
     */
    @Override
    public List<EmployeeDTO> getEmployees(
            Long departmentId,
            String employeeName,
            String ordEmployeeName,
            String ordCertificationName,
            String ordEndDate,
            Integer offset,
            Integer limit
    ) {
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
     * Lay thong tin chi tiet cua mot nhan vien theo ID.
     *
     * @param employeeId ID nhan vien can lay chi tiet
     * @return thong tin chi tiet neu ton tai
     */
    @Override
    @Transactional
    public Optional<EmployeeDetailDTO> getEmployeeDetail(Long employeeId) {
        Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);
        if (employeeOptional.isEmpty()) {
            return Optional.empty();
        }

        Employee employee = employeeOptional.get();
        String role = employee.getRole() == null ? "USER" : employee.getRole().trim().toUpperCase();
        if ("ADMIN".equals(role)) {
            return Optional.empty();
        }

        return Optional.of(toEmployeeDetailDTO(employee));
    }

    /**
     * Them moi nhan vien va chung chi neu request co chon chung chi.
     *
     * @param request du lieu nhan vien da duoc validate
     */
    @Override
    @Transactional
    public void addEmployee(EmployeeValidationRequest request) {
        Employee employee = new Employee();
        applyEmployeeValues(employee, request);
        employee.setRole("USER");
        Employee savedEmployee = employeeRepository.save(employee);
        replaceCertification(savedEmployee, request);
    }

    /**
     * Cap nhat nhan vien va ghi de lai thong tin chung chi hien tai.
     *
     * @param employeeId ID nhan vien can cap nhat
     * @param request du lieu nhan vien da duoc validate
     */
    @Override
    @Transactional
    public void updateEmployee(Long employeeId, EmployeeValidationRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        applyEmployeeValues(employee, request);
        employeeRepository.save(employee);
        replaceCertification(employee, request);
    }

    private void applyEmployeeValues(Employee employee, EmployeeValidationRequest request) {
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
        employeeCertificationRepository.deleteByEmployeeEmployeeId(employee.getEmployeeId());

        String certificationId = request.getCertificationId() == null ? "" : request.getCertificationId().trim();
        if (certificationId.isEmpty()) {
            return;
        }

        Certification certification = certificationRepository.findById(Long.parseLong(certificationId))
                .orElseThrow(() -> new IllegalArgumentException("Certification not found"));

        EmployeeCertification employeeCertification = new EmployeeCertification();
        employeeCertification.setEmployee(employee);
        employeeCertification.setCertification(certification);
        employeeCertification.setStartDate(LocalDate.parse(request.getCertificationStartDate().trim()));
        employeeCertification.setEndDate(LocalDate.parse(request.getCertificationEndDate().trim()));
        employeeCertification.setScore(new BigDecimal(request.getScore().trim()));
        employeeCertificationRepository.save(employeeCertification);
    }

    private EmployeeDetailDTO toEmployeeDetailDTO(Employee employee) {
        EmployeeDetailDTO dto = new EmployeeDetailDTO();
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setEmployeeLoginId(employee.getEmployeeLoginId());
        dto.setDepartmentId(employee.getDepartment().getDepartmentId());
        dto.setDepartmentName(employee.getDepartment().getDepartmentName());
        dto.setEmployeeName(employee.getEmployeeName());
        dto.setEmployeeNameKana(employee.getEmployeeNameKana());
        dto.setEmployeeBirthDate(employee.getEmployeeBirthDate());
        dto.setEmployeeEmail(employee.getEmployeeEmail());
        dto.setEmployeeTelephone(employee.getEmployeeTelephone());

        EmployeeCertification selectedCertification = selectEmployeeCertification(employee.getEmployeeCertifications());
        if (selectedCertification != null) {
            dto.setCertificationId(selectedCertification.getCertification().getCertificationId());
            dto.setCertificationName(selectedCertification.getCertification().getCertificationName());
            dto.setCertificationStartDate(selectedCertification.getStartDate());
            dto.setCertificationEndDate(selectedCertification.getEndDate());
            dto.setScore(selectedCertification.getScore());
        }

        return dto;
    }

    private EmployeeCertification selectEmployeeCertification(List<EmployeeCertification> employeeCertifications) {
        if (employeeCertifications == null || employeeCertifications.isEmpty()) {
            return null;
        }

        return employeeCertifications.stream()
                .filter(employeeCertification -> employeeCertification.getCertification() != null)
                .max(Comparator
                        .comparing((EmployeeCertification employeeCertification)
                                -> employeeCertification.getCertification().getCertificationLevel(),
                                Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(EmployeeCertification::getEndDate, Comparator.nullsLast(LocalDate::compareTo))
                        .thenComparing(EmployeeCertification::getEmployeeCertificationId,
                                Comparator.nullsLast(Long::compareTo)))
                .orElse(null);
    }
}
