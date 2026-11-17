# TÀI LIỆU GIẢI THÍCH CHI TIẾT MÃ NGUỒN – NHÓM G: KIỂM THỬ (TESTS)
**Thư mục:** `app/src/test/` (Unit Test) và `app/src/androidTest/` (Instrumented Test)

---

## TỔNG QUAN NHÓM KIỂM THỬ
Dự án áp dụng chiến lược kiểm thử 2 tầng theo tiêu chuẩn phát triển phần mềm Android:
1. **Local Unit Tests (`app/src/test/`):** Chạy trực tiếp trên máy trạm lập trình viên (Host JVM), không cần máy ảo Android, tốc độ thực thi chỉ vài giây. Dùng để kiểm chứng các thuật toán tính toán toán học, quy tắc logic ngày tháng, kiểm tra tính toàn vẹn đa múi giờ và cấu trúc tệp dữ liệu mẫu.
2. **Instrumented Tests (`app/src/androidTest/`):** Chạy trên môi trường Android thực tế (thiết bị thật hoặc emulator), có quyền truy cập vào `Context` hệ điều hành và động cơ CSDL SQLite thực sự. Dùng để kiểm thử toàn diện vòng đời CRUD và ràng buộc khóa ngoại `ON DELETE CASCADE`.

---

## 1. LogicUnitTest.java (`app/src/test/java/com/example/app/LogicUnitTest.java`)

### 1.1. Vai trò
Kiểm thử đơn vị tập trung toàn bộ các lớp nghiệp vụ thuần Java (không phụ thuộc vào Android Framework mock): `TienDoInfo`, `DateUtils`, `DoAnWithTienDo` và tệp dữ liệu mẫu `du_lieu_mau.json`.

### 1.2. Mối quan hệ
* **Thư viện sử dụng:** `org.junit.Test`, `static org.junit.Assert.*`.
* **Lớp được kiểm thử:** `TienDoInfo`, `DateUtils`, `DoAnWithTienDo`, `DoAn`.

### 1.3. Bảng giải thích chi tiết từng hàm trong `LogicUnitTest.java`

| STT | Tên hàm kiểm thử | Công dụng | Kịch bản và các bước kiểm thử chi tiết | Kết quả chạy thực tế |
| :---: | :--- | :--- | :--- | :---: |
| 1 | `testTienDoInfoCalculation()` | Kiểm tra công thức tính % tiến độ và định dạng chuỗi | **Ca 1 (Chia cho 0):** `TienDoInfo(0, 0)` $\rightarrow$ Khẳng định `getPercent() == 0` và `getFormattedText().equals("0/0 (0%)")`. Không bị sập ứng dụng.<br>**Ca 2 (Phần trăm thông thường):** `TienDoInfo(2, 5)` $\rightarrow$ Khẳng định đạt 40% và chuỗi `"2/5 (40%)"`.<br>**Ca 3 (Đạt 100%):** `TienDoInfo(3, 3)` $\rightarrow$ Khẳng định 100% và `"3/3 (100%)"`. | **PASS** (Đã chạy thật) |
| 2 | `testDateUtilsValidation()` | Kiểm tra logic kiểm định ngày và so sánh mốc thời gian | **Ca 1:** `isValidDateRange("2026-03-01", "2026-03-01")` $\rightarrow$ `true` (hạn cùng ngày hợp lệ).<br>**Ca 2:** Hạn sau ngày bắt đầu $\rightarrow$ `true`.<br>**Ca 3:** Hạn trước ngày bắt đầu $\rightarrow$ `false` (bắt lỗi thành công).<br>**Ca 4:** Regex kiểm tra format `yyyy-MM-dd` và `yyyy-MM-dd HH:mm`.<br>**Ca 5:** `isPastDateTime("2020-01-01 ...")` $\rightarrow$ `true`; năm 2099 $\rightarrow$ `false`. | **PASS** (Đã chạy thật) |
| 3 | `testDoAnWithTienDoModel()` | Kiểm tra hàm `equals` của DTO hiển thị `DoAnWithTienDo` | Tạo 2 đối tượng `DoAnWithTienDo` độc lập mang cùng giá trị các trường. Gọi `assertEquals(item1, item2)` $\rightarrow$ Khẳng định hàm `equals` sâu hoạt động chính xác phục vụ cho `DiffUtil`. | **PASS** (Đã chạy thật) |
| 4 | `testUtcConversionFixedValueAndMultipleTimeZones()` | Kiểm tra chuyển đổi UTC bất biến trước mọi múi giờ | Lưu lại `originalTimeZone`. Lần lượt đổi `TimeZone.setDefault(...)` sang `"Asia/Ho_Chi_Minh"`, `"America/Los_Angeles"`, `"UTC"`. Với cùng một ngày `"2026-05-15"`: Khẳng định `dateStringToUtcMillis` luôn trả về đúng `1778803200000L` và `formatUtcDate` luôn trả về `"2026-05-15"`. Khôi phục múi giờ gốc trong `finally`. | **PASS** (Đã chạy thật) |
| 5 | `testSampleDataIntegrity()` | Kiểm tra cấu trúc và tính toàn vẹn của tệp `du_lieu_mau.json` | Đọc tệp từ `assets/du_lieu_mau.json`. Dùng biểu thức chính quy (Regex) và phân tích cấu trúc để kiểm tra: đúng 4 đồ án, đúng 14 nhiệm vụ, đúng 7 cuộc hẹn, có đủ các trạng thái hoàn thành và chưa hoàn thành. Kiểm tra sự tồn tại của `docs/du_lieu_mau.sql`. | **PASS** (Đã chạy thật) |

---

## 2. DatabaseTest.java (`app/src/androidTest/java/com/example/app/DatabaseTest.java`)

### 2.1. Vai trò
Kiểm thử tích hợp (Instrumented Test) chạy trên thiết bị Android thật hoặc máy ảo, xác minh tính toàn vẹn của tệp CSDL `tiendo.db`, kiểm tra câu truy vấn `LEFT JOIN` của DAO và khẳng định cơ chế xóa xếp tầng `ON DELETE CASCADE`.

### 2.2. Mối quan hệ
* **Thư viện sử dụng:** `AndroidJUnit4`, `ApplicationProvider`, `org.junit.Before`, `org.junit.After`, `org.junit.Test`.
* **Lớp được kiểm thử:** `DatabaseHelper`, `DoAnDao`, `NhiemVuDao`, `CuocHenDao`, `SampleDataLoader`.

### 2.3. Bảng giải thích chi tiết từng hàm trong `DatabaseTest.java`

| STT | Tên hàm kiểm thử | Công dụng | Kịch bản và các bước kiểm thử chi tiết | Trạng thái kiểm chứng |
| :---: | :--- | :--- | :--- | :---: |
| 1 | `setUp()` | (Before) Chuẩn bị môi trường trước mỗi ca test | **Bước 1:** Lấy `ApplicationProvider.getApplicationContext()`.<br>**Bước 2:** Gọi `context.deleteDatabase(DatabaseHelper.DATABASE_NAME)` để xóa sạch CSDL cũ, đảm bảo môi trường độc lập.<br>**Bước 3:** Khởi tạo lại cả 3 DAO. | Sẵn sàng cho androidTest |
| 2 | `tearDown()` | (After) Dọn dẹp sau khi chạy test | Gọi `dbHelper.close()` để đóng kết nối CSDL. | Sẵn sàng cho androidTest |
| 3 | `testFullDatabaseOperations()` | Kiểm thử liên hoàn 9 bước toàn bộ chức năng CSDL | **Bước 1:** Chèn đồ án mới $\rightarrow$ Kiểm tra `id > 0`.<br>**Bước 2:** Đọc đồ án vừa chèn bằng `getById()` $\rightarrow$ Khẳng định tên và GVHD khớp.<br>**Bước 3:** Chèn 5 nhiệm vụ cho đồ án $\rightarrow$ `getByDoAnId()` trả về đủ 5.<br>**Bước 4:** Cập nhật nhanh trạng thái qua `updateStatus(nv3Id, true)` $\rightarrow$ Kiểm tra `daHoanThanh == 1`.<br>**Bước 5:** `getTienDo()` $\rightarrow$ Khẳng định đạt 2/5 (40%).<br>**Bước 6:** `getAllWithTienDo()` $\rightarrow$ Khẳng định câu truy vấn `LEFT JOIN` trả về đúng 40%.<br>**Bước 7:** Chèn 3 cuộc hẹn lộn xộn ngày $\rightarrow$ Khẳng định `getByDoAnId()` sắp xếp tăng dần theo `thoi_gian ASC`.<br>**Bước 8:** Sửa và xóa cuộc hẹn $\rightarrow$ Khẳng định cập nhật và xóa thành công.<br>**Bước 9 (CASCADE):** Gọi `doAnDao.delete(doAnId)`. Khẳng định: `doAnDao.getById` trả về null; danh sách nhiệm vụ của đồ án = 0; danh sách cuộc hẹn của đồ án = 0 (khóa ngoại xóa tầng thành công 100%). | Đã biên dịch thành công (`compileDebugAndroidTestSources` PASS). `[bổ sung kết quả sau khi chạy trên emulator]` |
| 4 | `testSampleDataLoaderImport()` | Kiểm thử nạp dữ liệu mẫu vào CSDL SQLite | **Bước 1:** Gọi `SampleDataLoader.importFromAssets(context)`. Khẳng định `result.isSuccess() == true`.<br>**Bước 2:** Khẳng định số lượng trả về: 4 đồ án, 14 nhiệm vụ, 7 cuộc hẹn.<br>**Bước 3:** Truy vấn đếm thực tế trực tiếp từ SQLite: `doAnDao.getCount() == 4`, `nhiemVuDao.getCount() == 14`, `cuocHenDao.getCount() == 7`. | Đã biên dịch thành công. `[bổ sung kết quả sau khi chạy trên emulator]` |
