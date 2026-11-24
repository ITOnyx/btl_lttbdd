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
1. **DBHelper.java**: Tạo bảng, nạp dữ liệu mẫu (`chenDuLieuMau`, `napLaiDuLieuMau`), toàn bộ hàm CRUD cho 3 bảng, tính tiến độ và thống kê đồ án.
2. **DoAn.java**: Lớp thực thể đồ án (id, ten, moTa, giangVien, ngayBatDau, hanNop + tongNhiemVu, soNhiemVuXong).
3. **NhiemVu.java**: Lớp thực thể nhiệm vụ (id, doAnId, ten, ghiChu, hanHoanThanh, daHoanThanh).
4. **CuocHen.java**: Lớp thực thể cuộc hẹn (id, doAnId, tieuDe, thoiGian, diaDiem, ghiChu).
5. **ThongKeItem.java**: Lớp dữ liệu cho thẻ thống kê (tiêu đề, giá trị, icon).
6. **MainActivity.java**: Danh sách đồ án; chuyển chế độ Danh sách / Lưới; menu ⋮ "Nạp dữ liệu mẫu".
7. **DoAnAdapter.java**: Adapter hiển thị các dòng đồ án và tiến độ.
8. **DoAnFormActivity.java**: Thêm / cập nhật đồ án (hạn nộp ≥ ngày bắt đầu và ≥ hạn nhiệm vụ muộn nhất).
9. **DoAnDetailActivity.java**: Chi tiết đồ án, thẻ thống kê cuộn ngang, điều hướng sang Nhiệm vụ / Cuộc hẹn / Dòng thời gian.
10. **ThongKeAdapter.java**: Adapter cho RecyclerView ngang 4 thẻ thống kê.
11. **NhiemVuListActivity.java**: Danh sách nhiệm vụ (chưa xong lên trước); tick xong thì dòng trượt về đúng nhóm.
12. **NhiemVuAdapter.java**: Adapter hiển thị nhiệm vụ với CheckBox hoàn thành và gạch ngang chữ.
13. **NhiemVuFormActivity.java**: Thêm / cập nhật nhiệm vụ (hạn hoàn thành không được sau hạn nộp đồ án).
14. **CuocHenListActivity.java**: Danh sách lịch hẹn của đồ án.
15. **CuocHenAdapter.java**: Adapter hiển thị cuộc hẹn với nhãn "Đã qua" / "Sắp tới".
16. **CuocHenFormActivity.java**: Thêm / cập nhật cuộc hẹn (chọn ngày & giờ).
17. **TimelineActivity.java**: Dòng thời gian gộp nhiệm vụ và lịch hẹn.
18. **TimelineAdapter.java**: Adapter nhiều kiểu dòng (HEADER / TASK / MEETING).

### Giao diện XML (`app/src/main/res/`):
- `layout/activity_main.xml` + `row_do_an.xml`: Danh sách đồ án, thanh tiến độ, nút FAB thêm.
- `layout/activity_do_an_form.xml`: Form nhập liệu đồ án.
- `layout/activity_do_an_detail.xml` + `row_thong_ke.xml`: Chi tiết đồ án, thẻ thống kê, các nút điều hướng.
- `layout/activity_nhiem_vu_list.xml` + `row_nhiem_vu.xml`: Danh sách nhiệm vụ có CheckBox, gạch ngang chữ khi xong.
- `layout/activity_nhiem_vu_form.xml`: Form nhập liệu nhiệm vụ.
- `layout/activity_cuoc_hen_list.xml` + `row_cuoc_hen.xml`: Danh sách cuộc hẹn có nhãn "Đã qua" / "Sắp tới".
- `layout/activity_cuoc_hen_form.xml`: Form nhập liệu cuộc hẹn (chọn ngày & giờ).
- `layout/activity_timeline.xml` + `row_header.xml`, `row_timeline_task.xml`, `row_timeline_meeting.xml`: Dòng thời gian.
- `menu/menu_main.xml`: Nút chuyển Danh sách / Lưới và mục "Nạp dữ liệu mẫu".

### Dữ liệu mẫu:
- Có sẵn khi cài lần đầu: 4 đồ án, 14 nhiệm vụ, 7 cuộc hẹn (25 bản ghi).
- Nạp lại bất cứ lúc nào: màn hình chính → menu ⋮ → **Nạp dữ liệu mẫu** (có hộp thoại xác nhận vì sẽ thay thế toàn bộ dữ liệu hiện có).
- Bản SQL / JSON độc lập: `docs/du_lieu_mau.sql`, `deliverables/du_lieu_mau/`.

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
SELECT COUNT(*), IFNULL(SUM(da_hoan_thanh), 0) FROM NhiemVu WHERE do_an_id = ?
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
