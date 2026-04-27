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

    /** Repository truy cập dữ liệu nhân viên. */
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
     * Khởi tạo service với các repository và component cần dùng cho nghiệp vụ nhân viên.
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
            Integer limit
    ) {
        // Nếu client không truyền limit/offset hợp lệ thì dùng giá trị mặc định của hệ thống.
        int finalLimit = (limit == null || limit <= 0) ? Constants.DEFAULT_LIMIT : limit;
        int finalOffset = (offset == null || offset < 0) ? Constants.DEFAULT_OFFSET : offset;

        // Query dữ liệu dạng Object[] từ repository theo điều kiện đã validate ở controller.
        List<Object[]> results = employeeRepository.findEmployees(
                departmentId,
                employeeName,
                ordEmployeeName,
                ordCertificationName,
                ordEndDate,
                finalLimit,
                finalOffset
        );

        // Convert kết quả native query sang DTO trả về frontend.
        List<EmployeeDTO> dtos = new ArrayList<>(results.size());
        for (Object[] row : results) {
            dtos.add(EmployeeDTO.fromRow(row));
        }
        return dtos;
    }

    /**
     * Lấy thông tin chi tiết của một nhân viên theo ID.
     *
     * @param employeeId ID nhân viên cần lấy chi tiết
     * @return thông tin chi tiết nếu tồn tại và không phải admin
     */
    @Override
    @Transactional
    public Optional<EmployeeDetailDTO> getEmployeeDetail(Long employeeId) {
        // Tìm nhân viên theo ID. Không có dữ liệu thì trả Optional rỗng để controller trả lỗi.
        Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);
        if (employeeOptional.isEmpty()) {
            return Optional.empty();
        }

        Employee employee = employeeOptional.get();

        // Không cho phép hiển thị/sửa tài khoản admin ở luồng quản lý nhân viên thường.
        String role = employee.getRole() == null ? "USER" : employee.getRole().trim().toUpperCase();
        if ("ADMIN".equals(role)) {
            return Optional.empty();
        }

        // Convert entity sang DTO chi tiết để trả về ADM003/ADM004.
        return Optional.of(toEmployeeDetailDTO(employee));
    }

    /**
     * Thêm mới nhân viên và chứng chỉ nếu request có chọn chứng chỉ.
     *
     * @param request dữ liệu nhân viên đã được validate
     */
    @Override
    @Transactional
    public void addEmployee(EmployeeValidationRequest request) {
        // Tạo entity mới và map dữ liệu từ request sang entity.
        Employee employee = new Employee();
        applyEmployeeValues(employee, request, true);

        // Nhân viên tạo từ ADM004 luôn là user thường.
        employee.setRole("USER");

        // Lưu employee trước để có employeeId dùng cho bảng liên kết chứng chỉ.
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
        // Tìm nhân viên cần sửa, không tồn tại thì throw để controller trả lỗi hệ thống.
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        // Map dữ liệu edit sang entity nhưng giữ nguyên account/password.
        applyEmployeeValues(employee, request, false);
        employeeRepository.save(employee);

        // Ghi đè lại thông tin chứng chỉ theo dữ liệu mới trên form.
        replaceCertification(employee, request);
    }

    /**
     * Map các giá trị từ request sang entity Employee.
     *
     * @param employee entity cần cập nhật dữ liệu
     * @param request dữ liệu form đã validate
     * @param updateAccountAndPassword true khi thêm mới, false khi chỉnh sửa
     */
    private void applyEmployeeValues(
            Employee employee,
            EmployeeValidationRequest request,
            boolean updateAccountAndPassword
    ) {
        // DepartmentId đã được validate tồn tại trước đó, ở đây lấy entity để set quan hệ.
        Department department = departmentRepository.findById(Long.parseLong(request.getDepartmentId().trim()))
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        // Các field thông tin cơ bản được cập nhật cho cả add và edit.
        employee.setDepartment(department);
        employee.setEmployeeName(request.getEmployeeName().trim());
        employee.setEmployeeNameKana(request.getEmployeeNameKana().trim());
        employee.setEmployeeBirthDate(LocalDate.parse(request.getEmployeeBirthDate().trim()));
        employee.setEmployeeEmail(request.getEmployeeEmail().trim());
        employee.setEmployeeTelephone(request.getEmployeeTelephone().trim());

        // Khi edit, màn hình chỉ hiển thị account/password dạng disabled nên giữ nguyên dữ liệu cũ.
        if (updateAccountAndPassword) {
            employee.setEmployeeLoginId(request.getEmployeeLoginId().trim());
            employee.setEmployeeLoginPassword(passwordEncoder.encode(request.getEmployeeLoginPassword()));
        }

        // Nếu dữ liệu cũ chưa có role thì mặc định là USER.
        if (employee.getRole() == null || employee.getRole().isBlank()) {
            employee.setRole("USER");
        }
    }

    /**
     * Ghi đè thông tin chứng chỉ của nhân viên.
     * Luồng hiện tại chỉ lưu một chứng chỉ được chọn trên form, nên xóa dữ liệu cũ rồi insert lại.
     *
     * @param employee nhân viên cần cập nhật chứng chỉ
     * @param request dữ liệu form đã validate
     */
    private void replaceCertification(Employee employee, EmployeeValidationRequest request) {
        // Xóa toàn bộ chứng chỉ cũ của nhân viên trước khi lưu dữ liệu mới.
        employeeCertificationRepository.deleteByEmployeeEmployeeId(employee.getEmployeeId());

        // Nếu form không chọn chứng chỉ thì kết thúc sau khi đã xóa dữ liệu cũ.
        String certificationId = request.getCertificationId() == null ? "" : request.getCertificationId().trim();
        if (certificationId.isEmpty()) {
            return;
        }

        // CertificationId đã validate tồn tại trước đó, ở đây lấy entity để set quan hệ.
        Certification certification = certificationRepository.findById(Long.parseLong(certificationId))
                .orElseThrow(() -> new IllegalArgumentException("Certification not found"));

        // Tạo bản ghi liên kết employee - certification từ dữ liệu form.
        EmployeeCertification employeeCertification = new EmployeeCertification();
        employeeCertification.setEmployee(employee);
        employeeCertification.setCertification(certification);
        employeeCertification.setStartDate(LocalDate.parse(request.getCertificationStartDate().trim()));
        employeeCertification.setEndDate(LocalDate.parse(request.getCertificationEndDate().trim()));
        employeeCertification.setScore(new BigDecimal(request.getScore().trim()));
        employeeCertificationRepository.save(employeeCertification);
    }

    /**
     * Chuyển Employee entity sang DTO chi tiết cho ADM003/ADM004.
     *
     * @param employee entity nhân viên
     * @return DTO chi tiết nhân viên
     */
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

        // Chọn một chứng chỉ đại diện để hiển thị trong detail/edit.
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

    /**
     * Chọn chứng chỉ đại diện của nhân viên.
     * Ưu tiên chứng chỉ có level cao hơn, nếu bằng nhau thì lấy ngày hết hạn lớn hơn,
     * cuối cùng dùng ID liên kết để ổn định kết quả.
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
                        .comparing((EmployeeCertification employeeCertification)
                                -> employeeCertification.getCertification().getCertificationLevel(),
                                Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(EmployeeCertification::getEndDate, Comparator.nullsLast(LocalDate::compareTo))
                        .thenComparing(EmployeeCertification::getEmployeeCertificationId,
                                Comparator.nullsLast(Long::compareTo)))
                .orElse(null);
    }
}
