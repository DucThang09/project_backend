package com.luvina.la.mapper;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * EmployeeController.java, April 13, 2026 tdthang
 */
import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Interface mapper cho Employee.
 * Cung cấp các phương thức chuyển đổi giữa entity và DTO.
 *
 * Cách sử dụng:
 * - EmployeeMapper.MAPPER.toEntity(dto);
 * - EmployeeMapper.MAPPER.toList(list);
 */
@Mapper
public interface EmployeeMapper {

    /** Instance của mapper được tạo bởi MapStruct. */
    EmployeeMapper MAPPER = Mappers.getMapper( EmployeeMapper.class );

    /**
     * Chuyển đổi từ EmployeeDTO sang Employee entity.
     *
     * @param entity EmployeeDTO cần chuyển đổi
     * @return Employee entity
     */
    Employee toEntity(EmployeeDTO entity);

    /**
     * Chuyển đổi từ EmployeeDTO sang Employee entity (đổi tên method).
     * Lưu ý: method này có tên không chính xác, nên đổi thành toEntity.
     *
     * @param entity EmployeeDTO cần chuyển đổi
     * @return Employee entity
     */
    Employee toDto(EmployeeDTO entity);

    /**
     * Chuyển đổi từ Iterable<Employee> sang Iterable<EmployeeDTO>.
     *
     * @param list Danh sách Employee entities
     * @return Danh sách EmployeeDTOs
     */
    Iterable<EmployeeDTO> toList(Iterable<Employee> list);

}
