/**
 * Copyright(C) 2026 Luvina Software Company
 *
 * EmployeeMapper.java, 10/05/2026 tdthang
 */
package com.luvina.la.mapper;

import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.dto.EmployeeDetailDTO;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * Mapper chuyển dữ liệu truy vấn native sang DTO nhân viên.
 *
 * @author tdthang
 */
public interface EmployeeMapper {

    EmployeeMapper MAPPER = new EmployeeMapper() {
    };

    /**
     * Map một dòng dữ liệu danh sách nhân viên sang EmployeeDTO.
     *
     * @param row dòng dữ liệu native query
     * @return DTO danh sách nhân viên
     */
    default EmployeeDTO toEmployeeDto(Object[] row) {
        EmployeeDTO dto = new EmployeeDTO();
        int index = 0;
        dto.setEmployeeId(toLong(row[index++]));
        dto.setEmployeeName(toStringValue(row[index++]));
        dto.setEmployeeBirthDate(toLocalDate(row[index++]));
        dto.setDepartmentName(toStringValue(row[index++]));
        dto.setEmployeeEmail(toStringValue(row[index++]));
        dto.setEmployeeTelephone(toStringValue(row[index++]));
        dto.setCertificationName(toStringValue(row[index++]));
        dto.setEndDate(toLocalDate(row[index++]));
        dto.setScore(toBigDecimal(row[index++]));
        return dto;
    }

    /**
     * Map một dòng dữ liệu chi tiết nhân viên sang EmployeeDetailDTO.
     *
     * @param row dòng dữ liệu native query
     * @return DTO chi tiết nhân viên
     */
    default EmployeeDetailDTO toEmployeeDetailDto(Object[] row) {
        EmployeeDetailDTO dto = new EmployeeDetailDTO();
        int index = 0;
        dto.setEmployeeId(toLong(row[index++]));
        dto.setEmployeeLoginId(toStringValue(row[index++]));
        dto.setDepartmentId(toLong(row[index++]));
        dto.setDepartmentName(toStringValue(row[index++]));
        dto.setEmployeeName(toStringValue(row[index++]));
        dto.setEmployeeNameKana(toStringValue(row[index++]));
        dto.setEmployeeBirthDate(toLocalDate(row[index++]));
        dto.setEmployeeEmail(toStringValue(row[index++]));
        dto.setEmployeeTelephone(toStringValue(row[index++]));
        dto.setCertificationId(toLong(row[index++]));
        dto.setCertificationName(toStringValue(row[index++]));
        dto.setCertificationStartDate(toLocalDate(row[index++]));
        dto.setCertificationEndDate(toLocalDate(row[index++]));
        dto.setScore(toBigDecimal(row[index++]));
        return dto;
    }

    /**
     * Chuyển Object thành Long.
     *
     * @param value giá trị cần chuyển đổi
     * @return Long hoặc null
     */
    default Long toLong(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.longValue();
        }

        return Long.valueOf(value.toString());
    }

    /**
     * Chuyển Object thành String.
     *
     * @param value giá trị cần chuyển đổi
     * @return String hoặc null
     */
    default String toStringValue(Object value) {
        return value == null ? null : value.toString();
    }

    /**
     * Chuyển Object thành LocalDate.
     *
     * @param value giá trị cần chuyển đổi
     * @return LocalDate hoặc null
     */
    default LocalDate toLocalDate(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof LocalDate localDate) {
            return localDate;
        }

        if (value instanceof Date date) {
            return date.toLocalDate();
        }

        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime().toLocalDate();
        }

        return LocalDate.parse(value.toString());
    }

    /**
     * Chuyển Object thành BigDecimal.
     *
     * @param value giá trị cần chuyển đổi
     * @return BigDecimal hoặc null
     */
    default BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }

        if (value instanceof BigInteger bigInteger) {
            return new BigDecimal(bigInteger);
        }

        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }

        return new BigDecimal(value.toString());
    }
}
