/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * EmployeeServiceImpl.java, 10/05/2026 tdthang
 */
package com.luvina.la.service.impl;

import com.luvina.la.config.Constants;
import static com.luvina.la.util.ValidationUtils.escapeLikePattern;
import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.dto.EmployeeDetailDTO;
import com.luvina.la.entity.Certification;
import com.luvina.la.entity.Department;
import com.luvina.la.entity.Employee;
import com.luvina.la.entity.EmployeeCertification;
import com.luvina.la.mapper.EmployeeMapper;
import com.luvina.la.payload.request.EmployeeValidationRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cài đặt nghiệp vụ cho EmployeeServiceImpl.
 *
 * @author tdthang
 */

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final EmployeeMapper EMPLOYEE_MAPPER = EmployeeMapper.MAPPER;

    private final EmployeeRepository employeeRepository;
    /** Repository truy cập dữ liệu phòng ban. */
    private final DepartmentRepository departmentRepository;
    /** Repository truy cập dữ liệu chứng chỉ. */
    private final CertificationRepository certificationRepository;
    /** Repository truy cập dữ liệu liên kết nhân viên và chứng chỉ. */
    private final EmployeeCertificationRepository employeeCertificationRepository;
    /** Component mã hóa mật khẩu trước khi lưu DB. */
    private final PasswordEncoder passwordEncoder;

    /**
     * Khởi tạo service với các repository và component cần dùng cho nghiệp vụ nhân
     * viên.
     *
     * @param employeeRepository              repository truy cập dữ liệu nhân viên
     * @param departmentRepository            repository truy cập dữ liệu phòng ban
     * @param certificationRepository         repository truy cập dữ liệu chứng chỉ
     * @param employeeCertificationRepository repository truy cập dữ liệu employee
     *                                        certification
     * @param passwordEncoder                 component mã hóa mật khẩu trước khi
     *                                        lưu
     */
    public EmployeeServiceImpl(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            CertificationRepository certificationRepository,
            EmployeeCertificationRepository employeeCertificationRepository,
            PasswordEncoder passwordEncoder) {
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
        return employeeRepository.countTotalEmployees(departmentId, escapeLikePattern(employeeName));
    }

    /**
     * Lấy danh sách nhân viên theo điều kiện tìm kiếm, sắp xếp và phân trang.
     *
     * @param departmentId         ID phòng ban cần lọc
     * @param employeeName         tên nhân viên cần tìm
     * @param ordEmployeeName      thứ tự sắp xếp theo tên nhân viên
     * @param ordCertificationName thứ tự sắp xếp theo tên chứng chỉ
     * @param ordEndDate           thứ tự sắp xếp theo ngày hết hạn chứng chỉ
     * @param offset               vị trí bắt đầu lấy dữ liệu
     * @param limit                số bản ghi tối đa cần lấy
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
        // Nếu client không truyền limit/offset hợp lệ thì dùng giá trị mặc định của hệ
        // thống.
        int finalLimit = (limit == null || limit <= 0) ? Constants.DEFAULT_LIMIT : limit;
        int finalOffset = (offset == null || offset < 0) ? Constants.DEFAULT_OFFSET : offset;

        // Query dữ liệu dạng Object[] từ repository theo điều kiện đã validate ở
        // controller.
        List<Object[]> results = employeeRepository.findEmployees(
                departmentId,
                escapeLikePattern(employeeName),
                ordEmployeeName,
                ordCertificationName,
                ordEndDate,
                finalLimit,
                finalOffset);

        // Convert kết quả native query sang DTO trả về frontend.
        List<EmployeeDTO> employeeDtos = new ArrayList<>(results.size());
        for (Object[] row : results) {
            employeeDtos.add(EMPLOYEE_MAPPER.toEmployeeDto(row));
        }
        return employeeDtos;
    }

    /**
     * Lấy chi tiết nhân viên theo ID, bao gồm thông tin cơ bản và chứng chỉ đại diện.
     */
    @Override
    public Optional<EmployeeDetailDTO> getEmployeeDetail(Long employeeId) {
        // Tìm nhân viên theo ID. Không có dữ liệu thì trả Optional rỗng để controller trả lỗi.
        List<Object[]> rows = employeeRepository.findEmployeeDetail(employeeId);
        if (rows.isEmpty()) {
            return Optional.empty();
        }

        // Convert query row sang DTO chi tiết để trả về ADM003/ADM004.
        return Optional.of(EMPLOYEE_MAPPER.toEmployeeDetailDto(rows.get(0)));
    }

    /**
     * Thêm mới nhân viên và chứng chỉ nếu request có chọn chứng chỉ.
     *
     * @param employeeValidationRequest dữ liệu nhân viên đã được validate
     *
     * @return giá trị trả về sau khi xử lý
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addEmployee(EmployeeValidationRequest employeeValidationRequest) {
        // Tạo entity mới và map dữ liệu từ request sang entity.
        Employee employee = new Employee();
        applyEmployeeValues(employee, employeeValidationRequest, true);

        // Nhân viên tạo từ ADM004 luôn là user thường.
        employee.setRole(0);

        // Lưu employee trước để có employeeId dùng cho bảng liên kết chứng chỉ.
        Employee savedEmployee = employeeRepository.save(employee);
        updateEmployeeCertification(savedEmployee, employeeValidationRequest);
        return savedEmployee.getEmployeeId();
    }

    /**
     * Cập nhật nhân viên và ghi đè lại thông tin chứng chỉ hiện tại.
     *
     * @param employeeId                ID nhân viên cần cập nhật
     * @param employeeValidationRequest dữ liệu nhân viên đã được validate
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmployee(Long employeeId, EmployeeValidationRequest employeeValidationRequest) {
        // Tìm nhân viên cần sửa, không tồn tại thì throw để controller trả lỗi hệ thống.
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        // Map dữ liệu edit sang entity nhưng giữ nguyên account/password.
        applyEmployeeValues(employee, employeeValidationRequest, false);
        employeeRepository.save(employee);

        // Ghi đè lại thông tin chứng chỉ theo dữ liệu mới trên form.
        updateEmployeeCertification(employee, employeeValidationRequest);
    }

    /**
     * Xoá nhân viên cần xóa
     *
     * @param employeeId ID nhân viên cần xóa
     * @return giá trị trả về sau khi xử lý
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteEmployee(Long employeeId) {
        Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);
        if (employeeOptional.isEmpty()) {
            return false;
        }

        Employee employee = employeeOptional.get();

        Integer role = employee.getRole() == null ? 0 : employee.getRole();
        if (role == 1) {
            throw new IllegalArgumentException("Cannot delete admin employee");
        }

        employeeCertificationRepository.deleteByEmployeeEmployeeId(employeeId);
        employeeRepository.delete(employee);
        return true;
    }

    /**
     * Map các giá trị từ request sang entity Employee.
     *
     * @param employee                  entity cần cập nhật dữ liệu
     * @param employeeValidationRequest dữ liệu form đã validate
     * @param updateAccountAndPassword  true khi thêm mới, false khi chỉnh sửa
     */
    private void applyEmployeeValues(
            Employee employee,
            EmployeeValidationRequest employeeValidationRequest,
            boolean updateAccountAndPassword) {
        // DepartmentId đã được validate tồn tại trước đó, ở đây lấy entity để set quan hệ.
        Department department = departmentRepository
                .findById(Long.parseLong(employeeValidationRequest.getDepartmentId().trim()))
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        // Các field thông tin cơ bản được cập nhật cho cả add và edit.
        employee.setDepartment(department);
        employee.setEmployeeName(employeeValidationRequest.getEmployeeName().trim());
        employee.setEmployeeNameKana(employeeValidationRequest.getEmployeeNameKana().trim());
        employee.setEmployeeBirthDate(LocalDate.parse(employeeValidationRequest.getEmployeeBirthDate().trim()));
        employee.setEmployeeEmail(employeeValidationRequest.getEmployeeEmail().trim());
        employee.setEmployeeTelephone(employeeValidationRequest.getEmployeeTelephone().trim());

        // Khi edit, màn hình chỉ hiển thị account/password dạng disabled nên giữ nguyên
        // dữ liệu cũ.
        if (updateAccountAndPassword) {
            employee.setEmployeeLoginId(employeeValidationRequest.getEmployeeLoginId().trim());
            employee.setEmployeeLoginPassword(
                    passwordEncoder.encode(employeeValidationRequest.getEmployeeLoginPassword()));
        }
    }

    /**
     * update thông tin chứng chỉ của nhân viên.
     * Luồng hiện tại chỉ lưu một chứng chỉ được chọn trên form
     *
     * @param employee                  nhân viên cần cập nhật chứng chỉ
     * @param employeeValidationRequest dữ liệu form đã validate
     */
    private void updateEmployeeCertification(Employee employee, EmployeeValidationRequest employeeValidationRequest) {
        // Xóa toàn bộ chứng chỉ cũ của nhân viên trước khi lưu dữ liệu mới.
        employeeCertificationRepository.deleteByEmployeeEmployeeId(employee.getEmployeeId());

        // Nếu form không chọn chứng chỉ thì kết thúc sau khi đã xóa dữ liệu cũ.
        String certificationIdRaw = employeeValidationRequest.getCertificationId();
        String certificationId = certificationIdRaw == null
                ? ""
                : certificationIdRaw.trim();
        if (certificationId.isEmpty()) {
            return;
        }

        // CertificationId đã validate tồn tại trước đó, ở đây lấy entity để set quan
        // hệ.
        Certification certification = certificationRepository.findById(Long.parseLong(certificationId))
                .orElseThrow(() -> new IllegalArgumentException("Certification not found"));

        // Tạo bản ghi liên kết employee - certification từ dữ liệu form.
        EmployeeCertification employeeCertification = new EmployeeCertification();
        employeeCertification.setEmployee(employee);
        employeeCertification.setCertification(certification);
        String certificationStartDate = employeeValidationRequest.getCertificationStartDate().trim();
        String certificationEndDate = employeeValidationRequest.getCertificationEndDate().trim();
        String score = employeeValidationRequest.getScore().trim();
        employeeCertification.setStartDate(LocalDate.parse(certificationStartDate));
        employeeCertification.setEndDate(LocalDate.parse(certificationEndDate));
        employeeCertification.setScore(new BigDecimal(score));
        employeeCertificationRepository.save(employeeCertification);
    }

    /**
     * Chọn chứng chỉ đại diện của nhân viên.
     * Ưu tiên chứng chỉ có level cao hơn, nếu bằng nhau thì lấy ngày hết hạn lớn
     * hơn
     *
     * @param employeeCertifications danh sách chứng chỉ của nhân viên
     * @return chứng chỉ được chọn hoặc null nếu không có dữ liệu hợp lệ
     */
    private EmployeeCertification selectEmployeeCertification(List<EmployeeCertification> employeeCertifications) {
        if (employeeCertifications == null || employeeCertifications.isEmpty()) {
            return null;
        }

        // Bỏ qua bản ghi không có certification, sau đó chọn bản ghi tốt nhất theo thứ tự ưu tiên.
        return employeeCertifications.stream()
                .filter(employeeCertification -> employeeCertification.getCertification() != null)
                .max(Comparator
                        .comparing(
                                (EmployeeCertification employeeCertification) -> employeeCertification
                                        .getCertification().getCertificationLevel(),
                                Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(EmployeeCertification::getEndDate, Comparator.nullsLast(LocalDate::compareTo))
                        .thenComparing(EmployeeCertification::getEmployeeCertificationId,
                                Comparator.nullsLast(Long::compareTo)))
                .orElse(null);
    }
}
