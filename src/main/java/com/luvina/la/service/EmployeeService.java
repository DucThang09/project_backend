package com.luvina.la.service;

import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.dto.EmployeeDetailDTO;
import com.luvina.la.payload.EmployeeValidationRequest;
import java.util.List;
import java.util.Optional;

/**
 * Service xu ly nghiep vu nhan vien.
 */
public interface EmployeeService {

    /**
     * Dem tong so nhan vien theo dieu kien tim kiem hien tai.
     *
     * @param departmentId ID phong ban can loc
     * @param employeeName ten nhan vien can tim
     * @return tong so ban ghi phu hop
     */
    Long getTotalEmployees(Long departmentId, String employeeName);

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
    List<EmployeeDTO> getEmployees(
            Long departmentId,
            String employeeName,
            String ordEmployeeName,
            String ordCertificationName,
            String ordEndDate,
            Integer offset,
            Integer limit
    );

    /**
     * Lay thong tin chi tiet cua mot nhan vien theo ID.
     *
     * @param employeeId ID nhan vien can lay chi tiet
     * @return thong tin chi tiet neu ton tai
     */
    Optional<EmployeeDetailDTO> getEmployeeDetail(Long employeeId);

    /**
     * Them moi nhan vien va thong tin chung chi neu co.
     *
     * @param request du lieu nhan vien da duoc validate
     */
    void addEmployee(EmployeeValidationRequest request);

    /**
     * Cap nhat nhan vien theo ID va ghi de lai thong tin chung chi hien tai.
     *
     * @param employeeId ID nhan vien can cap nhat
     * @param request du lieu nhan vien da duoc validate
     */
    void updateEmployee(Long employeeId, EmployeeValidationRequest request);

    /**
     * Xoa nhan vien theo ID.
     *
     * @param employeeId ID nhan vien can xoa
     */
    boolean deleteEmployee(Long employeeId);
}
