# ADM003 BE Review - Delete

| controller | service | validate | repository | dto/payload |
|---|---|---|---|---|
| `EmployeeController.deleteEmployee(@RequestParam String employeeId)` kiểm tra `null` -> `ER001`, parse `Long` lỗi -> `ER014`, service trả `false` -> `ER014`, success -> `MSG003`, exception -> `ER023`. Điểm cần lưu ý: `employeeId=""` hiện đi vào parse lỗi và trả `ER014` (không phải `ER001`). | `EmployeeServiceImpl.deleteEmployee(Long employeeId)` tìm employee theo id, không có thì `false`; nếu role `ADMIN` thì throw `IllegalArgumentException`; còn lại xóa `employee_certifications` trước rồi xóa employee và trả `true`. | Không có validator class riêng cho delete ADM003. Validate nằm trong controller (null + parse number) và service (exist/admin rule). Hiện chưa map riêng lỗi business cho admin delete ở controller. | Dùng `employeeRepository.findById(employeeId)`, `employeeCertificationRepository.deleteByEmployeeEmployeeId(employeeId)`, `employeeRepository.delete(employee)`. Thứ tự delete hiện hợp lý cho ràng buộc FK. | `EmployeeDeleteResponse` trả `code`, `employeeId`, `message`. Success dùng `MSG003`, error dùng mã tương ứng (`ER001/ER014/ER023`). Contract frontend hiện dựa vào `message.code` để điều hướng/hiển thị kết quả. |

## Test Coverage Check

| Item | Status |
|---|---|
| `employeeId` null -> `ER001` | Có (`EmployeeControllerTest`) |
| `employeeId` rỗng/không phải số -> `ER014` | Có (`EmployeeControllerTest`) |
| Service trả `false` -> `ER014` | Có (`EmployeeControllerTest`) |
| Delete success -> `MSG003` | Có (`EmployeeControllerTest`) |
| Service: employee không tồn tại -> `false` | Có (`EmployeeServiceImplTest`) |
| Service: employee tồn tại -> delete cert + delete employee | Có (`EmployeeServiceImplTest`) |
| Service: employee role ADMIN -> throw exception | Chưa thấy test trực tiếp |

## Notes

- Nếu đặc tả yêu cầu mã lỗi riêng cho xóa ADMIN (ví dụ `ER020`), cần bắt `IllegalArgumentException` ở controller và map sang mã lỗi đó thay vì rơi `ER023`.
- Nếu muốn chặt hơn về input, có thể cân nhắc `isBlank()` cho `employeeId` trước parse để tách rõ required vs invalid-format.
