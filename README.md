# Ứng Dụng Theo Dõi Tiến Độ Đồ Án / Thực Tập

Ứng dụng di động Android giúp sinh viên ghi chú các đầu việc, theo dõi tiến độ hoàn thành đồ án và quản lý lịch hẹn làm việc với giảng viên hướng dẫn.

---

## 1. Thông Tin Chung
- **Nền tảng**: Android (Min SDK 24, Target SDK 34 / 37).
- **Ngôn ngữ**: 100% Java (không dùng Kotlin trong mã ứng dụng; Gradle build script dùng Kotlin DSL).
- **Kiến trúc giao diện**: XML Views + Material Components + ViewBinding.
- **Lưu trữ dữ liệu**: SQLite thuần qua `SQLiteOpenHelper` (không dùng Room, không dùng ORM bên ngoài).
- **Hiển thị danh sách**: `RecyclerView` với kiến trúc hiện đại `ListAdapter` và `DiffUtil`.
- **Package name**: `com.example.app`.

---

## 2. Cấu Trúc Thư Mục & Mã Nguồn

```
app/src/main/java/com/example/app/
├── adapter/
│   ├── CuocHenAdapter.java            # ListAdapter + DiffUtil hiển thị danh sách lịch hẹn
│   ├── DoAnAdapter.java              # ListAdapter + DiffUtil hiển thị danh sách đồ án & tiến độ
│   └── NhiemVuAdapter.java           # ListAdapter + DiffUtil hiển thị nhiệm vụ kèm checkbox hoàn thành
├── db/
│   ├── CuocHenDao.java               # Xử lý CRUD lịch hẹn giảng viên (thoi_gian ASC)
│   ├── DatabaseHelper.java           # Quản lý vòng đời SQLite (Singleton, PRAGMA foreign_keys = ON)
│   ├── DoAnDao.java                  # Xử lý CRUD đồ án, truy vấn LEFT JOIN + GROUP BY tính tiến độ
│   └── NhiemVuDao.java               # Xử lý CRUD nhiệm vụ, cập nhật nhanh trạng thái hoàn thành
├── model/
│   ├── CuocHen.java                  # Thực thể lịch hẹn giảng viên
│   ├── DoAn.java                     # Thực thể đồ án / thực tập
│   ├── DoAnWithTienDo.java           # Đồ án kết hợp dữ liệu tiến độ (completedTasks / totalTasks)
│   ├── NhiemVu.java                  # Thực thể nhiệm vụ / đầu việc
│   └── TienDoInfo.java               # DTO chứa số lượng nhiệm vụ đã xong & tổng số nhiệm vụ
├── ui/
│   ├── AddEditCuocHenDialogFragment.java   # DialogFragment thêm/sửa lịch hẹn (DatePicker + 24h TimePicker)
│   ├── AddEditDoAnDialogFragment.java     # DialogFragment thêm/sửa đồ án kèm kiểm tra ngày
│   ├── AddEditNhiemVuDialogFragment.java  # DialogFragment thêm/sửa nhiệm vụ kèm kiểm tra hạn hoàn thành
│   ├── CuocHenFragment.java               # Tab hiển thị danh sách lịch hẹn trong chi tiết đồ án
│   ├── DoAnDetailActivity.java            # Màn hình chi tiết đồ án (Header tiến độ + TabLayout & ViewPager2)
│   ├── MainActivity.java                  # Màn hình chính hiển thị danh sách đồ án và thanh tìm kiếm
│   └── NhiemVuFragment.java               # Tab hiển thị danh sách nhiệm vụ trong chi tiết đồ án
└── util/
    └── DateUtils.java                # Tiện ích định dạng ngày giờ, so sánh và kiểm tra hạn
```

---

## 3. Lược Đồ Cơ Sở Dữ Liệu (SQLite - `tiendo.db`, version 1)

Ràng buộc toàn vẹn khóa ngoại được bật tự động trong `onConfigure`:
```sql
PRAGMA foreign_keys = ON;
```

### 3.1. Bảng `DoAn`
```sql
CREATE TABLE DoAn (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ten TEXT NOT NULL,
    mo_ta TEXT,
    giang_vien TEXT,
    ngay_bat_dau TEXT,   -- định dạng yyyy-MM-dd
    han_nop TEXT         -- định dạng yyyy-MM-dd
);
```

### 3.2. Bảng `NhiemVu`
```sql
CREATE TABLE NhiemVu (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    do_an_id INTEGER NOT NULL REFERENCES DoAn(id) ON DELETE CASCADE,
    ten TEXT NOT NULL,
    ghi_chu TEXT,
    han_hoan_thanh TEXT, -- định dạng yyyy-MM-dd
    da_hoan_thanh INTEGER NOT NULL DEFAULT 0   -- 0 (chưa xong) / 1 (đã xong)
);
```

### 3.3. Bảng `CuocHen`
```sql
CREATE TABLE CuocHen (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    do_an_id INTEGER NOT NULL REFERENCES DoAn(id) ON DELETE CASCADE,
    tieu_de TEXT NOT NULL,
    thoi_gian TEXT NOT NULL,  -- định dạng yyyy-MM-dd HH:mm
    dia_diem TEXT,
    ghi_chu TEXT
);
```

### 3.4. Công thức tính tiến độ
Tiến độ của một đồ án được tính toán động (không lưu cứng vào DB) bằng câu truy vấn `LEFT JOIN` và `GROUP BY` trong `DoAnDao`:
$$\text{Tỷ lệ \%} = \frac{\text{Số NhiemVu hoàn thành}}{\text{Tổng số NhiemVu}} \times 100$$
- Trả về an toàn `0/0 (0%)` khi đồ án chưa có nhiệm vụ nào.

---

## 4. Danh Sách Tính Năng

### 4.1. Quản lý Đồ án (Màn hình chính)
- Xem danh sách đồ án dưới dạng thẻ card Material Design với thanh tiến độ (`LinearProgressIndicator`) và tỷ lệ phần trăm trực quan.
- Thêm đồ án mới / Chỉnh sửa thông tin đồ án: Tên, mô tả, giảng viên hướng dẫn, ngày bắt đầu, hạn nộp.
- Xóa đồ án với hộp thoại xác nhận cảnh báo xóa liên hoàn (`CASCADE`).
- Cập nhật tự động tiến độ đồ án mỗi khi quay lại màn hình chính (`onResume`).

### 4.2. Màn hình Chi tiết Đồ án
- Hiển thị thông tin tổng quan, giảng viên, thời gian bắt đầu - hạn nộp, và tiến độ tổng thể `X/Y (Z%)`.
- Điều hướng mượt mà giữa hai Tab: **Nhiệm vụ** và **Lịch hẹn GV** qua `TabLayout` + `ViewPager2`.
- Duy trì đúng tab đang chọn khi xoay màn hình (Portrait $\leftrightarrow$ Landscape).

### 4.3. Quản lý Nhiệm vụ (Tab 1)
- Xem danh sách nhiệm vụ với thông tin trạng thái, hạn hoàn thành.
- Tick / Bỏ tick CheckBox hoàn thành trực tiếp trên từng mục:
  - Cập nhật ngay lập tức xuống SQLite qua hàm cập nhật nhanh trạng thái.
  - Tự động gạch ngang chữ nhiệm vụ khi hoàn thành.
  - Cập nhật ngay thanh tiến độ `X/Y (Z%)` ở header.
  - Xử lý listener an toàn, không bị lỗi gán sai trạng thái khi tái sử dụng ViewHolder trong RecyclerView.
- Thêm / Sửa / Xóa nhiệm vụ kèm xác nhận. Kiểm tra hợp lệ: Hạn hoàn thành của nhiệm vụ không được vượt quá hạn nộp của đồ án.

### 4.4. Quản lý Lịch hẹn Giảng viên (Tab 2)
- Danh sách cuộc hẹn sắp xếp tự động theo thời gian tăng dần (`thoi_gian ASC`).
- Chọn ngày qua `MaterialDatePicker` kết hợp chọn giờ qua `MaterialTimePicker` (24 giờ), lưu chuẩn `yyyy-MM-dd HH:mm`.
- Tự động gắn nhãn trạng thái:
  - **Sắp diễn ra**: Cuộc hẹn trong tương lai.
  - **Đã qua**: Cuộc hẹn có thời gian trước thời điểm hiện tại, thẻ hiển thị mờ nhẹ trực quan.
- Thêm / Sửa / Xóa cuộc hẹn kèm xác nhận.

### 4.5. Độ ổn định & Trải nghiệm người dùng
- Tất cả form nhập liệu và dialog xác nhận xóa được thiết kế để sống sót khi xoay màn hình: sử dụng kiến trúc `DialogFragment` (`ConfirmDeleteDialogFragment`, `AddEditDoAnDialogFragment`, `AddEditNhiemVuDialogFragment`, `AddEditCuocHenDialogFragment`), lưu giữ trạng thái trong `onSaveInstanceState`, gắn lại listener theo `TAG` trong `onStart()` đối với `MaterialDatePicker` / `MaterialTimePicker`, và giao tiếp qua Fragment Result API.
- Xử lý giá trị null/rỗng chu đáo: Nếu trường nào không có thông tin (giảng viên, mô tả, hạn nộp, ghi chú, địa điểm), giao diện ẩn gọn gàng hoặc hiển thị placeholder, tuyệt đối không xuất hiện chữ "null".
- Toàn bộ chuỗi giao diện được quản lý trong `strings.xml`.

---

## 5. Hướng Dẫn Build & Chạy

### 5.1. Yêu cầu môi trường
- Android Studio Iguana / Jellyfish / Koala hoặc mới hơn.
- JDK 17 (hoặc bundled JDK của Android Studio).
- Android SDK Platform 34+, Build Tools 34+.

### 5.2. Lệnh Build qua Terminal / Gradle
Do đường dẫn chứa ký tự tiếng Việt có dấu (`D:\Kì 7\Bài tập lớn LTTBDD`), khi chạy lệnh Gradle trên Windows qua Command Prompt (`cmd.exe`), khuyến nghị sử dụng lệnh ánh xạ ổ đĩa ảo tạm thời (`subst`) để tránh lỗi mã hóa ký tự đường dẫn của Gradle Wrapper:

```cmd
cmd.exe /c "subst X: . && X: && gradlew.bat assembleDebug && subst X: /D"
```

Hoặc mở dự án trực tiếp trong Android Studio và nhấn nút **Run** (Shift + F10).

---

## 6. Hướng Dẫn Chạy Kiểm Thử (Tests)

### 6.1. Unit Test (Chạy trực tiếp trên máy host / JVM, không cần emulator)
Tập tin kiểm thử: `app/src/test/java/com/example/app/LogicUnitTest.java`
Kiểm thử toàn diện các logic:
- Thuật toán tính tiến độ (chia cho 0, tỷ lệ hoàn thành, làm tròn).
- Tiện ích ngày tháng `DateUtils` (parse, format, so sánh, kiểm tra hạn).
- Ràng buộc hạn nộp đồ án và hạn nhiệm vụ.

Lệnh chạy:
```cmd
cmd.exe /c "subst X: . && X: && gradlew.bat testDebugUnitTest && subst X: /D"
```

### 6.2. Instrumentation Test (Chạy trên thiết bị thật hoặc Emulator)
Tập tin kiểm thử: `app/src/androidTest/java/com/example/app/DatabaseTest.java`
Kiểm thử trực tiếp trên SQLite thật trong môi trường Android:
- Bật khóa ngoại `PRAGMA foreign_keys = ON`.
- Xóa liên hoàn `ON DELETE CASCADE` từ `DoAn` sang `NhiemVu` và `CuocHen`.
- Truy vấn `getAllWithTienDo` với phép tính tiến độ chuẩn xác.
- Thao tác CRUD và sắp xếp `thoi_gian ASC` của `CuocHenDao`.
- Cập nhật nhanh trạng thái `da_hoan_thanh` của `NhiemVuDao`.

Lệnh chạy (khi đã kết nối thiết bị qua ADB):
```cmd
cmd.exe /c "subst X: . && X: && gradlew.bat connectedAndroidTest && subst X: /D"
```

---

## 7. Các Hạn Chế Đã Biết (Known Limitations)

1. **Xử lý `onUpgrade` của Database**: Trong phiên bản hiện tại (version 1), phương thức `onUpgrade` của `DatabaseHelper` đang cài đặt chiến lược đơn giản hóa cho môi trường phát triển: xóa các bảng cũ (`DROP TABLE IF EXISTS`) và tạo lại. Khi nâng cấp `DATABASE_VERSION` lên các phiên bản sau, cần triển khai các câu lệnh `ALTER TABLE` hoặc migrate dữ liệu cẩn thận để tránh mất dữ liệu của người dùng.
2. **Cập nhật nhãn "Đã qua" của Lịch hẹn**: Nhãn trạng thái "Sắp diễn ra" / "Đã qua" được tính toán tại thời điểm danh sách `CuocHenAdapter` gắn dữ liệu (`bind`). Nếu ứng dụng mở liên tục trên màn hình mà qua mốc giờ hẹn, nhãn sẽ không tự động nhảy sang "Đã qua" theo thời gian thực trừ khi người dùng thêm/sửa lịch hẹn, chuyển đổi tab hoặc mở lại màn hình.
3. **Mã hóa đường dẫn chứa dấu tiếng Việt trên Windows**: Khi chạy `gradlew.bat` trực tiếp từ cmd/PowerShell ngoài thư mục có dấu tiếng Việt, batch script của Windows `%~dp0` có thể hiểu nhầm ký tự ANSI. Cần mở trực tiếp qua Android Studio hoặc dùng giải pháp ánh xạ ổ đĩa ảo `subst` như đã hướng dẫn ở mục 5.2.
4. **Các ca xoay màn hình chưa kiểm chứng trên thiết bị thực**: Do môi trường dòng lệnh không có sẵn thiết bị/emulator đang chạy, các ca sau mới chỉ được đảm bảo về mặt kiến trúc mã nguồn nhưng chưa được kiểm chứng trực quan bằng tay:
   - Xoay màn hình khi `ConfirmDeleteDialogFragment` đang hiển thị.
   - Xoay màn hình khi `MaterialDatePicker` hoặc `MaterialTimePicker` đang mở.
   - Xoay màn hình khi đang nhập dở dữ liệu trong các dialog form.
