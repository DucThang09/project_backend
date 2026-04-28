# ADM003 BE Review

| controller | service | validate | repository | dto/payload |
|---|---|---|---|---|
| `EmployeeController.getEmployeeDetail(@PathVariable Long employeeId)` gọi service và trả `EmployeeDetailResponse.success(...)` khi có dữ liệu; nếu `Optional.empty()` hoặc exception thì trả `ER023`. Rủi ro: cả not-found và lỗi hệ thống đều trả cùng `ER023`, khó phân biệt. | `EmployeeServiceImpl.getEmployeeDetail(Long employeeId)` dùng `employeeRepository.findById(...)`, chặn role `ADMIN`, map entity sang `EmployeeDetailDTO`, chọn chứng chỉ đại diện bằng `selectEmployeeCertification(...)` (ưu tiên level, rồi endDate, rồi id). | Không có validator riêng cho ADM003 detail. Validate hiện dựa vào kiểu `Long` ở path variable và logic service (tồn tại/admin). Trường hợp path không phải số sẽ fail ở Spring binding trước khi vào method. | Luồng thực tế đang dùng `employeeRepository.findById(...)`. Method native `findEmployeeDetail(...)` vẫn tồn tại nhưng không được sử dụng, cần quyết định giữ để dùng sau hay loại bỏ tránh code thừa. | `EmployeeDetailResponse` trả `code`, `employee`, `message`; `EmployeeDetailDTO` chứa đủ field ADM003. `EmployeeDetailDTO.fromRow(...)` hiện không dùng trong luồng hiện tại (service map trực tiếp từ entity). |

## Test Coverage Check

| Item | Status |
|---|---|
| Employee tồn tại -> trả detail | Có (`EmployeeServiceImplTest.shouldReturnEmployeeDetailWhenEmployeeExists`) |
| Employee không tồn tại -> empty | Có (`shouldReturnEmptyWhenEmployeeDoesNotExist`) |
| Employee role ADMIN -> empty | Có (`shouldReturnEmptyWhenEmployeeIsAdmin`) |
| Không có chứng chỉ -> các field chứng chỉ null | Có (`shouldReturnEmployeeDetailWithoutCertificationWhenEmployeeHasNoCertification`) |
| Controller mapping cho case invalid path / phân biệt not-found vs system error | Chưa có test tách bạch |

## Notes

- Nếu đặc tả yêu cầu phân biệt not-found và system error ở ADM003, cần tách mã lỗi tại controller/service thay vì dùng chung `ER023`.
- Nên dọn code không dùng (`EmployeeRepository.findEmployeeDetail`, `EmployeeDetailDTO.fromRow`) hoặc chuyển luồng qua query detail nếu muốn tối ưu số query.
