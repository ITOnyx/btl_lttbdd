# TÀI LIỆU GIẢI THÍCH CHI TIẾT MÃ NGUỒN – NHÓM C: UTILITIES
**Thư mục:** `app/src/main/java/com/example/app/util/`

---

## TỔNG QUAN NHÓM UTILITIES
Tầng tiện ích (Utilities) chứa các lớp dùng chung hỗ trợ cho toàn bộ ứng dụng, giải quyết 2 bài toán kỹ thuật đặc thù:
1. `DateUtils`: Xử lý định dạng thời gian, chuyển đổi múi giờ UTC cho `MaterialDatePicker`, kiểm tra tính hợp lệ của khoảng thời gian và hiển thị các hộp thoại chọn ngày/giờ truyền thống.
2. `SampleDataLoader`: Bộ nạp dữ liệu mẫu đọc từ tệp tin assets JSON, thực hiện chèn đồng thời 3 bảng trong một Transaction SQLite an toàn.

---

## 1. DateUtils.java (`app/src/main/java/com/example/app/util/DateUtils.java`)

### 1.1. Vai trò
`DateUtils` là lớp tiện ích tĩnh (Static Utility Class) tập trung toàn bộ logic thao tác với ngày tháng và thời gian trong ứng dụng. Lớp này đóng vai trò cầu nối định dạng giữa tầng hiển thị (chuỗi người dùng nhìn thấy) và tầng lưu trữ CSDL (chuỗi chuẩn `yyyy-MM-dd` và `yyyy-MM-dd HH:mm`), đồng thời giải quyết triệt để lỗi lệch ngày do múi giờ khi làm việc với `MaterialDatePicker`.

### 1.2. Mối quan hệ
* **Các lớp gọi `DateUtils`:**
  * `AddEditDoAnDialogFragment`: Dùng `dateStringToUtcMillis()` và `formatUtcDate()` khi mở `MaterialDatePicker`; gọi `isValidDateRange()` để kiểm tra hạn nộp $\ge$ ngày bắt đầu.
  * `AddEditNhiemVuDialogFragment`: Dùng `formatUtcDate()` khi chọn hạn chót; dùng `parseDate()` để so sánh hạn chót nhiệm vụ không vượt quá hạn nộp đồ án.
  * `AddEditCuocHenDialogFragment`: Gọi `showDateTimePicker()` để mở bộ chọn ngày và giờ nối tiếp nhau.
  * `CuocHenAdapter`: Gọi `isPastDateTime()` để quyết định hiển thị badge "Sắp diễn ra" (xanh lá) hay "Đã qua" (xám).
  * `LogicUnitTest`: Kiểm thử tính hợp lệ và độ chính xác đa múi giờ.
* **Lớp mà `DateUtils` gọi:** `java.text.SimpleDateFormat`, `java.util.Calendar`, `java.util.Date`, `java.util.TimeZone`, `android.app.DatePickerDialog`, `android.app.TimePickerDialog`.

### 1.3. Thuộc tính và hằng số quan trọng
* `DATE_FORMAT` (`"yyyy-MM-dd"`): Định dạng ngày chuẩn ISO-8601 dùng cho đồ án và nhiệm vụ.
* `DATETIME_FORMAT` (`"yyyy-MM-dd HH:mm"`): Định dạng ngày giờ 24h dùng cho cuộc hẹn.
* `dateFormat` (`SimpleDateFormat`): Đối tượng phân tích cú pháp ngày với Locale máy.
* `dateTimeFormat` (`SimpleDateFormat`): Đối tượng phân tích cú pháp ngày giờ với Locale máy.

### 1.4. Giao diện lồng nhau (Nested Interfaces)
* `OnDateSelectedListener`: Callback trả về ngày được chọn `void onDateSelected(String dateStr)`.
* `OnDateTimeSelectedListener`: Callback trả về ngày giờ được chọn `void onDateTimeSelected(String dateTimeStr)`.

### 1.5. Bảng giải thích chi tiết từng hàm trong `DateUtils.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `getCurrentDate()` | Không có $\rightarrow$ `String` | Lấy ngày hiện tại dạng chuỗi | Gọi `dateFormat.format(new Date())`. Dùng từ khóa `synchronized` đảm bảo an toàn đa luồng. | Gán giá trị mặc định khi tạo mới |
| 2 | `getCurrentDateTime()` | Không có $\rightarrow$ `String` | Lấy ngày giờ hiện tại dạng chuỗi | Gọi `dateTimeFormat.format(new Date())`. | Gán giá trị mặc định cuộc hẹn |
| 3 | `formatDate(Date)` | `Date date` $\rightarrow$ `String` | Định dạng Date thành chuỗi `yyyy-MM-dd` | Nếu date null trả về `""`, ngược lại gọi `dateFormat.format(date)`. | Tầng hiển thị |
| 4 | `formatDateTime(Date)` | `Date date` $\rightarrow$ `String` | Định dạng Date thành chuỗi `yyyy-MM-dd HH:mm` | Nếu date null trả về `""`, ngược lại gọi `dateTimeFormat.format(date)`. | Tầng hiển thị |
| 5 | `formatUtcDate(Long)` | `Long utcMillis` $\rightarrow$ `String` | Chuyển đổi epoch millis UTC thành chuỗi ngày không bị lệch múi giờ | **Bước 1:** Nếu `utcMillis == null` trả về `""`.<br>**Bước 2:** Khởi tạo `SimpleDateFormat` với `TimeZone.getTimeZone("UTC")`.<br>**Bước 3:** Format `new Date(utcMillis)` và trả về. | `AddEditDoAnDialogFragment`, `AddEditNhiemVuDialogFragment` (nhận kết quả từ `MaterialDatePicker`) |
| 6 | `dateStringToUtcMillis(String)` | `String dateStr` $\rightarrow$ `Long` | Chuyển chuỗi `yyyy-MM-dd` thành epoch millis tại 00:00:00 UTC | **Bước 1:** Kiểm tra null/rỗng.<br>**Bước 2:** Tạo `SimpleDateFormat` đặt múi giờ UTC.<br>**Bước 3:** `parse(dateStr.trim()).getTime()` và trả về. | `AddEditDoAnDialogFragment` (đặt ngày chọn mặc định cho `MaterialDatePicker`) |
| 7 | `parseDate(String)` | `String dateStr` $\rightarrow$ `Date` | Phân tích chuỗi `yyyy-MM-dd` thành đối tượng `Date` | Gọi `dateFormat.parse(dateStr.trim())`. Nếu lỗi `ParseException` trả về `null`. | `isValidDateRange()`, `showDatePicker()` |
| 8 | `parseDateTime(String)` | `String dateTimeStr` $\rightarrow$ `Date` | Phân tích chuỗi `yyyy-MM-dd HH:mm` thành `Date` | Gọi `dateTimeFormat.parse(dateTimeStr.trim())`. Nếu lỗi trả về `null`. | `isPastDateTime()`, `showDateTimePicker()` |
| 9 | `isValidDateRange(String, String)` | `String startDateStr, String endDateStr` $\rightarrow$ `boolean` | Kiểm tra tính hợp lệ của khoảng thời gian | **Bước 1:** `parseDate` cả 2 chuỗi.<br>**Bước 2:** Nếu 1 trong 2 null trả về `true` (cho phép để trống).<br>**Bước 3:** Trả về `!end.before(start)` (hạn nộp $\ge$ ngày bắt đầu). | `AddEditDoAnDialogFragment.validateInput()`, `LogicUnitTest` |
| 10 | `isPastDateTime(String)` | `String dateTimeStr` $\rightarrow$ `boolean` | Kiểm tra thời điểm hẹn đã qua hay chưa | **Bước 1:** `parseDateTime(dateTimeStr)`.<br>**Bước 2:** Nếu null trả về `false`.<br>**Bước 3:** Trả về `date.before(new Date())`. | `CuocHenAdapter.onBindViewHolder()`, `LogicUnitTest` |
| 11 | `showDatePicker(Context, String, OnDateSelectedListener)` | `Context context, String initialDate, OnDateSelectedListener listener` $\rightarrow$ `void` | Mở hộp thoại chọn ngày dạng pop-up `DatePickerDialog` | **Bước 1:** Khởi tạo `Calendar`, parse `initialDate` làm ngày mặc định.<br>**Bước 2:** Tạo `DatePickerDialog`, khi người dùng chọn ngày thì gọi `listener.onDateSelected(dateFormat.format(...))`.<br>**Bước 3:** `dialog.show()`. | Hộp thoại chọn ngày pop-up |
| 12 | `showDateTimePicker(Context, String, OnDateTimeSelectedListener)` | `Context context, String initialDateTime, OnDateTimeSelectedListener listener` $\rightarrow$ `void` | Mở liên tiếp bộ chọn ngày rồi đến bộ chọn giờ | **Bước 1:** Mở `DatePickerDialog`.<br>**Bước 2:** Khi người dùng chọn ngày xong, lưu lại năm/tháng/ngày và lập tức mở tiếp `TimePickerDialog`.<br>**Bước 3:** Khi người dùng chọn giờ/phút xong, ghép thành ngày giờ hoàn chỉnh và gọi `listener.onDateTimeSelected(...)`. | `AddEditCuocHenDialogFragment` |

### 1.6. Điểm kỹ thuật cốt lõi: Giải bài toán lệch ngày UTC của MaterialDatePicker
Thành phần `MaterialDatePicker` trong bộ thư viện Google Material Components hoạt động theo chuẩn quốc tế: Khi người dùng bấm chọn một ngày trên lịch (ví dụ `15/05/2026`), giá trị `selection` trả về luôn là số mili-giây tính từ mốc Unix Epoch tương ứng với **00:00:00 UTC** ngày 15/05/2026.
* **Nguyên nhân lỗi off-by-one:** Nếu lập trình viên sử dụng `SimpleDateFormat` thông thường (mặc định theo múi giờ địa phương của điện thoại, ví dụ Việt Nam là GMT+7 hoặc Mỹ là GMT-5 đến GMT-8):
  * Tại múi giờ Mỹ (ví dụ New York GMT-4): 00:00:00 ngày 15/05 UTC chuyển sang giờ địa phương sẽ bị lùi lại thành 20:00:00 ngày 14/05 $\rightarrow$ **Người dùng chọn ngày 15 nhưng màn hình bị nhảy thành ngày 14**.
* **Giải pháp trong DateUtils (dòng 46–63):** Hai hàm `formatUtcDate` và `dateStringToUtcMillis` gắn cứng `sdf.setTimeZone(TimeZone.getTimeZone("UTC"))`. Khi format hoặc parse, hệ thống ép xử lý trong hệ quy chiếu UTC, đảm bảo ngày người dùng nhìn thấy trên lịch và chuỗi lưu vào CSDL luôn trùng khớp 100% trên mọi múi giờ khắp thế giới.

---

## 2. SampleDataLoader.java (`app/src/main/java/com/example/app/util/SampleDataLoader.java`)

### 2.1. Vai trò
`SampleDataLoader` đóng vai trò là bộ nạp dữ liệu mẫu (Seeder / Importer). Lớp này đọc tệp dữ liệu có cấu trúc `du_lieu_mau.json` từ thư mục `assets`, giải mã cấu trúc JSON phân cấp và chèn đồng bộ 4 đồ án, 14 nhiệm vụ, 7 cuộc hẹn vào SQLite trong một Transaction duy nhất.

### 2.2. Mối quan hệ
* **Các lớp gọi `SampleDataLoader`:**
  * `MainActivity.importSampleData()`: Gọi hàm khi người dùng nhấn "Nạp dữ liệu mẫu" trên menu overflow và xác nhận trong hộp thoại.
  * `DatabaseTest.testSampleDataLoaderImport()`: Kiểm thử nạp dữ liệu trong môi trường androidTest.
* **Lớp mà `SampleDataLoader` gọi:** `DatabaseHelper`, `SQLiteDatabase`, `ContentValues`, `org.json.JSONObject`, `org.json.JSONArray`.

### 2.3. Lớp lồng nhau `SampleDataLoader.ImportResult`
Lớp đóng gói kết quả nạp dữ liệu:
* `success` (`boolean`): Cờ báo thành công hay thất bại.
* `projectsCount` (`int`): Số lượng đồ án đã chèn.
* `tasksCount` (`int`): Số lượng nhiệm vụ đã chèn.
* `meetingsCount` (`int`): Số lượng cuộc hẹn đã chèn.
* `errorMessage` (`String`): Thông điệp lỗi nếu xảy ra ngoại lệ.

### 2.4. Bảng giải thích chi tiết từng hàm trong `SampleDataLoader.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động (các bước chính) | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `ImportResult(...)` | `boolean success, int projectsCount, int tasksCount, int meetingsCount, String errorMessage` $\rightarrow$ `void` | Constructor khởi tạo kết quả | Gán toàn bộ 5 thuộc tính kết quả nạp dữ liệu. | `importFromAssets()` |
| 2 | `isSuccess()` | Không có $\rightarrow$ `boolean` | Kiểm tra nạp thành công | Trả về `this.success`. | `MainActivity.importSampleData()`, `DatabaseTest` |
| 3 | `getProjectsCount()` | Không có $\rightarrow$ `int` | Lấy số đồ án đã nạp | Trả về `this.projectsCount`. | `MainActivity` (hiển thị Snackbar), `DatabaseTest` |
| 4 | `getTasksCount()` | Không có $\rightarrow$ `int` | Lấy số nhiệm vụ đã nạp | Trả về `this.tasksCount`. | `MainActivity`, `DatabaseTest` |
| 5 | `getMeetingsCount()` | Không có $\rightarrow$ `int` | Lấy số cuộc hẹn đã nạp | Trả về `this.meetingsCount`. | `MainActivity`, `DatabaseTest` |
| 6 | `getErrorMessage()` | Không có $\rightarrow$ `String` | Lấy thông điệp lỗi nếu có | Trả về `this.errorMessage`. | `DatabaseTest` (thông báo khi assert fail) |
| 7 | `importFromAssets(Context)` | `Context context` $\rightarrow$ `ImportResult` | Thực hiện nạp toàn bộ dữ liệu mẫu trong Transaction | Xem phân tích chi tiết quy trình 5 bước ở mục 2.5 bên dưới. | `MainActivity`, `DatabaseTest` |
| 8 | `readAssetFile(Context, String)` | `Context context, String fileName` $\rightarrow$ `String` | (Private) Đọc nội dung tệp văn bản từ assets | **Bước 1:** Mở stream `context.getAssets().open(fileName)`.<br>**Bước 2:** Đọc từng dòng bằng `BufferedReader` với mã hóa UTF-8.<br>**Bước 3:** Ghép thành chuỗi bằng `StringBuilder` và trả về. | `importFromAssets()` |

### 2.5. Phân tích chi tiết quy trình nạp trong `importFromAssets(Context)` (dòng 55–164)
* **Bước 1 - Khởi tạo Transaction (dòng 56–63):** Lấy `db = helper.getWritableDatabase()` và gọi `db.beginTransaction()`. Việc mở Transaction đảm bảo tốc độ ghi siêu nhanh (không phải ghi đĩa từng lệnh riêng lẻ) và đảm bảo tính nguyên tử (All-or-Nothing).
* **Bước 2 - Đọc và giải mã JSON (dòng 65–68):** Gọi `readAssetFile` đọc `du_lieu_mau.json`, phân tích thành `JSONObject root` và trích xuất mảng `do_an`.
* **Bước 3 - Duyệt từng đồ án và chèn bản ghi (dòng 69–100):** Đưa thông tin đồ án vào `ContentValues`. Đặc biệt kiểm tra `doAnObj.isNull("mo_ta")` để gọi `putNull()` cho các trường để trống. Gọi `long doAnId = db.insertOrThrow(...)`.
* **Bước 4 - Chèn nhiệm vụ và cuộc hẹn con dựa trên ID mới (dòng 103–154):** Lấy `doAnId` vừa được sinh tự động gán vào `COLUMN_NHIEM_VU_DO_AN_ID` và `COLUMN_CUOC_HEN_DO_AN_ID`. Nhờ vậy, ngay cả khi CSDL đã có sẵn dữ liệu trước đó, các bản ghi mẫu mới vẫn được chèn hoàn toàn chính xác mà không bao giờ bị trùng khóa chính (Primary Key Conflict).
* **Bước 5 - Cam kết (Commit) hoặc Hoàn tác (Rollback) (dòng 157–163):**
  * Nếu toàn bộ quá trình không có lỗi: Gọi `db.setTransactionSuccessful()`, trả về `ImportResult(true, 4, 14, 7, null)`.
  * Nếu có lỗi ngoại lệ (`catch Exception e`): Bỏ qua lệnh cam kết, trả về `ImportResult(false, 0, 0, 0, e.getMessage())`.
  * Khối `finally`: Luôn luôn gọi `db.endTransaction()`. Nếu chưa đánh dấu successful, SQLite sẽ tự động rollback toàn bộ dữ liệu về trạng thái ban đầu.
