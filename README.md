# ỨNG DỤNG QUẢN LÝ TIẾN ĐỘ ĐỒ ÁN (ANDROID - JAVA)

Ứng dụng di động giúp sinh viên theo dõi tiến độ hoàn thành các đồ án / đề tài tốt nghiệp, quản lý các đầu việc (nhiệm vụ) và lịch hẹn làm việc với giảng viên hướng dẫn.

Mã nguồn được viết lại theo đúng **phong cách bài giảng của thầy cô** (tương tự bài mẫu "Quản lý sinh viên"): code phẳng, dễ đọc, dễ hiểu, sử dụng các thành phần Android cốt lõi kinh điển.

---

## 1. THÔNG TIN CHUNG & CÔNG NGHỆ SỬ DỤNG
- **Ngôn ngữ**: 100% Java.
- **Package name**: `com.example.app` (toàn bộ mã nguồn nằm phẳng trong package này, không chia nhỏ nhiều tầng).
- **Cơ sở dữ liệu**: SQLite thuần thông qua `DBHelper extends SQLiteOpenHelper` (`tiendo.db`).
  - Đọc dữ liệu: `db.rawQuery(...)`, vòng lặp `while (cursor.moveToNext())`, `cursor.getInt(...)`, `cursor.getString(...)` theo chỉ số cột, `cursor.close()`.
  - Ghi dữ liệu: `db.execSQL(sql, new Object[]{...})` với dấu tham số `?` (tránh lỗi bảo mật SQL Injection).
  - Tự động xóa dữ liệu liên quan: Bật `db.setForeignKeyConstraintsEnabled(true)` trong `onConfigure` để kích hoạt `ON DELETE CASCADE`.
- **Giao diện**:
  - Ánh xạ View bằng `findViewById`.
  - Gắn sự kiện click bằng `setOnClickListener`.
  - Hiển thị danh sách bằng `RecyclerView.Adapter` và `ViewHolder` kinh điển.
  - Tự động cập nhật danh sách mới trong `onResume()` bằng `notifyDataSetChanged()`.
  - Chọn ngày bằng `DatePickerDialog`, chọn giờ bằng `TimePickerDialog`.
  - Hộp thoại xác nhận xóa bằng `AlertDialog.Builder`.
  - Thông báo nhanh bằng `Toast`.
- **Hướng màn hình**: Ứng dụng hỗ trợ cả hướng dọc và hướng ngang (tương thích tốt với điện thoại, máy tính bảng và các trình giả lập cửa sổ ngang như BlueStacks). Nhờ cấu hình `android:configChanges="orientation|screenSize|keyboardHidden"` trong `AndroidManifest.xml`, Activity không bị hủy và tạo lại khi xoay màn hình, giúp giữ nguyên toàn bộ dữ liệu đang nhập dở trong form mà không cần viết thêm mã `onSaveInstanceState` phức tạp. Toàn bộ form nhập liệu và chi tiết đều được bọc trong `ScrollView` để cuộn và thao tác mượt mà ngay cả khi chiều cao màn hình ngang bị thu hẹp.

---

## 2. CẤU TRÚC FILE MÃ NGUỒN

### Mã nguồn Java (`app/src/main/java/com/example/app/`):
1. **DBHelper.java**: Quản lý tạo bảng, nạp dữ liệu mẫu ban đầu, toàn bộ hàm CRUD cho 3 bảng và hàm tính tiến độ đồ án.
2. **DoAn.java**: Lớp thực thể đồ án (id, ten, moTa, giangVien, ngayBatDau, hanNop).
3. **NhiemVu.java**: Lớp thực thể nhiệm vụ (id, doAnId, ten, ghiChu, hanHoanThanh, daHoanThanh).
4. **CuocHen.java**: Lớp thực thể cuộc hẹn (id, doAnId, tieuDe, thoiGian, diaDiem, ghiChu).
5. **MainActivity.java**: Màn hình chính hiển thị danh sách đồ án.
6. **DoAnAdapter.java**: Adapter RecyclerView hiển thị các dòng đồ án và tiến độ.
7. **DoAnFormActivity.java**: Màn hình thêm mới / cập nhật đồ án.
8. **DoAnDetailActivity.java**: Màn hình xem chi tiết đồ án và điều hướng sang nhiệm vụ / cuộc hẹn.
9. **NhiemVuListActivity.java**: Màn hình danh sách nhiệm vụ của đồ án.
10. **NhiemVuAdapter.java**: Adapter RecyclerView hiển thị nhiệm vụ với CheckBox hoàn thành.
11. **NhiemVuFormActivity.java**: Màn hình thêm mới / cập nhật nhiệm vụ.
12. **CuocHenListActivity.java**: Màn hình danh sách lịch hẹn của đồ án.
13. **CuocHenAdapter.java**: Adapter RecyclerView hiển thị cuộc hẹn với nhãn "Đã qua" / "Sắp tới".
14. **CuocHenFormActivity.java**: Màn hình thêm mới / cập nhật cuộc hẹn.

### Giao diện XML (`app/src/main/res/layout/`):
- `activity_main.xml`: Danh sách đồ án + nút FAB thêm.
- `item_do_an.xml`: Dòng hiển thị đồ án, giảng viên, thời gian, thanh tiến độ.
- `activity_do_an_form.xml`: Form nhập liệu đồ án.
- `activity_do_an_detail.xml`: Thông tin chi tiết đồ án + 2 nút mở Nhiệm vụ / Lịch hẹn.
- `activity_nhiem_vu_list.xml`: Danh sách nhiệm vụ + nút FAB thêm.
- `item_nhiem_vu.xml`: Dòng nhiệm vụ có CheckBox hoàn thành, gạch ngang chữ khi xong.
- `activity_nhiem_vu_form.xml`: Form nhập liệu nhiệm vụ.
- `activity_cuoc_hen_list.xml`: Danh sách cuộc hẹn + nút FAB thêm.
- `item_cuoc_hen.xml`: Dòng cuộc hẹn có nhãn badge "Đã qua" / "Sắp tới".
- `activity_cuoc_hen_form.xml`: Form nhập liệu cuộc hẹn (chọn ngày & giờ).

---

## 3. LƯỢC ĐỒ CƠ SỞ DỮ LIỆU (SQLite: `tiendo.db`)

```sql
-- 1. Bảng Đồ án
CREATE TABLE DoAn (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ten TEXT NOT NULL,
    mo_ta TEXT,
    giang_vien TEXT,
    ngay_bat_dau TEXT,   -- định dạng yyyy-MM-dd
    han_nop TEXT         -- định dạng yyyy-MM-dd
);

-- 2. Bảng Nhiệm vụ (khóa ngoại tham chiếu DoAn)
CREATE TABLE NhiemVu (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    do_an_id INTEGER NOT NULL REFERENCES DoAn(id) ON DELETE CASCADE,
    ten TEXT NOT NULL,
    ghi_chu TEXT,
    han_hoan_thanh TEXT, -- định dạng yyyy-MM-dd
    da_hoan_thanh INTEGER NOT NULL DEFAULT 0  -- 0: chưa xong, 1: đã xong
);

-- 3. Bảng Cuộc hẹn (khóa ngoại tham chiếu DoAn)
CREATE TABLE CuocHen (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    do_an_id INTEGER NOT NULL REFERENCES DoAn(id) ON DELETE CASCADE,
    tieu_de TEXT NOT NULL,
    thoi_gian TEXT NOT NULL, -- định dạng yyyy-MM-dd HH:mm
    dia_diem TEXT,
    ghi_chu TEXT
);
```

### Cách tính tiến độ đồ án:
Được tính trực tiếp trong hàm `dbHelper.getTienDoDoAn(doAnId)`:
```sql
SELECT COUNT(*), SUM(da_hoan_thanh) FROM NhiemVu WHERE do_an_id = ?
```
- Kết quả trả về chuỗi trực quan: `"x/y (z%)"` (ví dụ `"3/5 (60%)"`).
- Nếu chưa có nhiệm vụ nào (`y == 0`), trả về an toàn `"0/0 (0%)"`.

---

## 4. CÁCH CHẠY DỰ ÁN

### Cách 1: Mở bằng Android Studio
1. Mở Android Studio -> Chọn `Open` -> Trỏ đến thư mục `D:\Kì 7\Bài tập lớn LTTBDD`.
2. Đợi Gradle đồng bộ xong (Sync Project with Gradle Files).
3. Chọn máy ảo Android (Emulator) hoặc cắm thiết bị thật đã bật USB Debugging.
4. Bấm nút **Run** (tam giác xanh) để cài đặt và khởi chạy.

### Cách 2: Biên dịch qua dòng lệnh (CLI)
```bash
# Biên dịch file APK Debug
.\gradlew.bat assembleDebug

# File APK được xuất ra tại:
# app/build/outputs/apk/debug/app-debug.apk
```
