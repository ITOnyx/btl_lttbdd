# TÀI LIỆU GIẢI THÍCH CHI TIẾT MÃ NGUỒN – NHÓM F: TÀI NGUYÊN (RESOURCES) & CẤU HÌNH
**Thư mục:** `app/src/main/res/`, `AndroidManifest.xml` và `app/build.gradle.kts`

---

## 1. CÁC TỆP GIAO DIỆN LAYOUT XML (`app/src/main/res/layout/`)

### 1.1. `activity_main.xml`
* **File Java sử dụng qua ViewBinding:** `MainActivity.java` (thông qua `ActivityMainBinding`).
* **Các View quan trọng:**
  * `main` (`CoordinatorLayout`): Layout gốc hỗ trợ tương tác nâng cao với AppBar và hiển thị `Snackbar`.
  * `toolbar` (`MaterialToolbar`): Thanh tiêu đề màu xanh `#2563EB`, hiển thị title *"Theo dõi đồ án"* và menu 3 chấm.
  * `rvDoAn` (`RecyclerView`): Danh sách cuộn hiển thị các card đồ án (`item_do_an.xml`).
  * `layoutEmpty` (`LinearLayout`): Khối giao diện hiển thị khi CSDL chưa có dữ liệu (hình tròn rỗng và thông điệp hướng dẫn).
  * `fabAddDoAn` (`FloatingActionButton`): Nút tròn nổi góc dưới bên phải để kích hoạt thêm đồ án mới.

### 1.2. `activity_do_an_detail.xml`
* **File Java sử dụng:** `DoAnDetailActivity.java` (`ActivityDoAnDetailBinding`).
* **Các View quan trọng:**
  * `toolbarDetail` (`MaterialToolbar`): Toolbar có nút Back điều hướng về màn hình chính.
  * `cardDetailHeader` (`MaterialCardView`): Khối thẻ chứa thông tin tóm tắt đề tài.
  * `tvDetailTen` (`TextView`): Tên đề tài đồ án chữ đậm cỡ lớn.
  * `tvDetailGiangVien` (`TextView`): Tên giảng viên hướng dẫn.
  * `tvDetailThoiGian` (`TextView`): Khoảng thời gian từ ngày bắt đầu đến hạn nộp.
  * `progressDetail` (`ProgressBar`): Thanh tiến độ tổng quát đồ án dùng `@drawable/custom_progress_drawable`.
  * `tvDetailTienDoValue` (`TextView`): Nhãn tỷ lệ tiến độ hoàn thành dạng chuỗi `"X/Y (Z%)"`.
  * `tabLayoutDetail` (`TabLayout`): Thanh 2 tab ("Nhiệm vụ" và "Lịch hẹn GV").
  * `viewPagerDetail` (`ViewPager2`): Vùng chứa hỗ trợ vuốt chuyển đổi giữa 2 Fragment con.

### 1.3. `fragment_nhiem_vu.xml`
* **File Java sử dụng:** `NhiemVuFragment.java` (`FragmentNhiemVuBinding`).
* **Các View quan trọng:**
  * `rvNhiemVu` (`RecyclerView`): Danh sách các đầu mục nhiệm vụ của đồ án.
  * `layoutEmptyTasks` (`LinearLayout`): Giao diện rỗng khi đồ án chưa có nhiệm vụ nào.
  * `fabAddNhiemVu` (`FloatingActionButton`): Nút thêm nhiệm vụ mới.

### 1.4. `fragment_cuoc_hen.xml`
* **File Java sử dụng:** `CuocHenFragment.java` (`FragmentCuocHenBinding`).
* **Các View quan trọng:**
  * `rvCuocHen` (`RecyclerView`): Danh sách các buổi hẹn với giảng viên sắp xếp theo thời gian tăng dần.
  * `layoutEmptyMeetings` (`LinearLayout`): Giao diện rỗng khi chưa có lịch hẹn.
  * `fabAddCuocHen` (`FloatingActionButton`): Nút thêm lịch hẹn mới.

### 1.5. `dialog_add_edit_do_an.xml`
* **File Java sử dụng:** `AddEditDoAnDialogFragment.java` (`DialogAddEditDoAnBinding`).
* **Các View quan trọng:**
  * `tvDialogTitle` (`TextView`): Tiêu đề hộp thoại ("Thêm đồ án" hoặc "Sửa đồ án").
  * `tilTen` / `etTen` (`TextInputLayout` / `TextInputEditText`): Nhập tên đồ án (bắt buộc).
  * `tilMoTa` / `etMoTa`: Nhập mô tả tóm tắt.
  * `tilGiangVien` / `etGiangVien`: Nhập tên giảng viên hướng dẫn.
  * `tilNgayBatDau` / `etNgayBatDau`: Hiển thị ngày bắt đầu; không cho gõ phím (`focusable="false"`), chỉ mở `MaterialDatePicker`.
  * `tilHanNop` / `etHanNop`: Hiển thị hạn nộp đồ án; mở `MaterialDatePicker`.

### 1.6. `dialog_add_edit_nhiem_vu.xml`
* **File Java sử dụng:** `AddEditNhiemVuDialogFragment.java` (`DialogAddEditNhiemVuBinding`).
* **Các View quan trọng:**
  * `tilTenNhiemVu` / `etTenNhiemVu`: Nhập tên đầu mục công việc.
  * `tilGhiChu` / `etGhiChu`: Nhập ghi chú chi tiết.
  * `tilHanHoanThanh` / `etHanHoanThanh`: Chọn hạn chót hoàn thành nhiệm vụ.
  * `cbDaHoanThanh` (`MaterialCheckBox`): Đánh dấu trạng thái đã hoàn thành hay chưa ngay trong form thêm/sửa.

### 1.7. `dialog_add_edit_cuoc_hen.xml`
* **File Java sử dụng:** `AddEditCuocHenDialogFragment.java` (`DialogAddEditCuocHenBinding`).
* **Các View quan trọng:**
  * `tilTieuDe` / `etTieuDe`: Nhập tiêu đề cuộc hẹn.
  * `tilThoiGian` / `etThoiGian`: Chọn thời gian hẹn (mở liên tiếp bộ chọn ngày rồi đến bộ chọn giờ).
  * `tilDiaDiem` / `etDiaDiem`: Nhập phòng học / link Google Meet.
  * `tilGhiChu` / `etGhiChu`: Nhập nội dung cần trao đổi.

### 1.8. `item_do_an.xml`
* **File Java sử dụng:** `DoAnAdapter.java` (`ItemDoAnBinding`).
* **Các View quan trọng:**
  * `cardDoAn` (`MaterialCardView`): Khối thẻ chứa viền bo tròn 12dp và hiệu ứng đổ bóng.
  * `tvTen` (`TextView`): Tên đồ án.
  * `tvMoTa` (`TextView`): Mô tả (tự ẩn nếu rỗng).
  * `tvGiangVien` (`TextView`): GVHD.
  * `tvThoiGian` (`TextView`): Khoảng thời gian hoặc hạn nộp.
  * `progressBar` (`ProgressBar`): Thanh hiển thị % hoàn thành đồ án.
  * `tvTienDo` (`TextView`): Nhãn số `"X/Y (Z%)"`.
  * `btnEdit` (`ImageButton`): Nút biểu tượng bút chì để sửa nhanh đồ án.
  * `btnDelete` (`ImageButton`): Nút biểu tượng thùng rác để xóa đồ án.

### 1.9. `item_nhiem_vu.xml`
* **File Java sử dụng:** `NhiemVuAdapter.java` (`ItemNhiemVuBinding`).
* **Các View quan trọng:**
  * `cbHoanThanh` (`MaterialCheckBox`): Ô đánh dấu hoàn thành nhanh.
  * `tvTenNhiemVu` (`TextView`): Tên nhiệm vụ (gạch ngang khi đã xong).
  * `tvGhiChu` (`TextView`): Ghi chú (ẩn nếu rỗng).
  * `tvHanHoanThanh` (`TextView`): Hạn chót.
  * `btnEditTask` (`ImageButton`): Nút sửa nhiệm vụ.
  * `btnDeleteTask` (`ImageButton`): Nút xóa nhiệm vụ.

### 1.10. `item_cuoc_hen.xml`
* **File Java sử dụng:** `CuocHenAdapter.java` (`ItemCuocHenBinding`).
* **Các View quan trọng:**
  * `cardCuocHen` (`MaterialCardView`): Thẻ chứa cuộc hẹn (làm mờ alpha 0.65 khi đã qua).
  * `tvTieuDe` (`TextView`): Tiêu đề buổi hẹn.
  * `tvThoiGian` (`TextView`): Ngày giờ hẹn.
  * `tvStatusBadge` (`TextView`): Nhãn trạng thái màu sắc ("Sắp diễn ra" / "Đã qua").
  * `layoutDiaDiem` / `tvDiaDiem`: Khối hiển thị địa điểm kèm icon vị trí.
  * `btnEditMeeting` / `btnDeleteMeeting`: Nút sửa/xóa cuộc hẹn.

---

## 2. BẢNG TÀI NGUYÊN ĐỒ HỌA (DRAWABLE)
*Thư mục: `app/src/main/res/drawable/`*

| Tên tệp Drawable | Loại tệp | Công dụng |
| :--- | :---: | :--- |
| `custom_progress_drawable.xml` | Layer-list XML | Tạo thanh ProgressBar có góc bo tròn, nền màu xám nhạt và dải tiến độ màu xanh `#2563EB`. |
| `bg_badge_upcoming.xml` | Shape XML | Nền bo góc 4dp màu xanh lá nhạt (`#DCFCE7`) cho nhãn "Sắp diễn ra". |
| `bg_badge_passed.xml` | Shape XML | Nền bo góc 4dp màu xám nhạt (`#F1F5F9`) cho nhãn "Đã qua". |
| `bg_circle_icon.xml` | Shape XML | Hình tròn bo góc làm nền cho icon trạng thái rỗng (Empty State). |
| `ic_add.xml` | Vector Drawable | Biểu tượng dấu cộng (+) cho các nút FloatingActionButton. |
| `ic_edit.xml` | Vector Drawable | Biểu tượng cây bút chì cho các nút sửa. |
| `ic_delete.xml` | Vector Drawable | Biểu tượng thùng rác cho các nút xóa. |
| `ic_calendar.xml` | Vector Drawable | Biểu tượng tờ lịch đặt cạnh ngày tháng. |
| `ic_clock.xml` | Vector Drawable | Biểu tượng đồng hồ đặt cạnh mốc giờ hẹn. |
| `ic_location.xml` | Vector Drawable | Biểu tượng ghim vị trí đặt cạnh địa điểm. |
| `ic_person.xml` | Vector Drawable | Biểu tượng người dùng đặt cạnh tên GVHD. |
| `ic_empty_state.xml` | Vector Drawable | Đồ họa hộp tài liệu rỗng biểu thị màn hình chưa có dữ liệu. |
| `ic_arrow_back.xml` | Vector Drawable | Biểu tượng mũi tên quay lại trên Toolbar của màn hình chi tiết. |

---

## 3. BẢNG TÀI NGUYÊN GIÁ TRỊ (VALUES)
*Thư mục: `app/src/main/res/values/` và `values-night/`*

### 3.1. `colors.xml`
Hệ thống màu sắc tuân thủ thiết kế Material 3:
* `primary` (`#2563EB`): Màu xanh dương chủ đạo của thanh Toolbar, nút chính và thanh tiến độ.
* `secondary` (`#059669`): Màu xanh lục bảo phụ trợ.
* `background` (`#F8FAFC`): Màu nền ứng dụng sáng dịu mắt.
* `surface` (`#FFFFFF`): Màu trắng của các thẻ CardView và Dialog.
* `text_primary` (`#0F172A`): Màu chữ đậm chính tương phản cao.
* `text_secondary` (`#475569`): Màu chữ phụ cho mô tả và ngày tháng.
* `status_completed` (`#16A34A`): Màu xanh lá báo trạng thái hoàn thành / cuộc hẹn sắp diễn ra.
* `badge_passed_text` (`#64748B`): Màu xám cho cuộc hẹn đã qua.

### 3.2. `strings.xml`
Chứa 100% chuỗi văn bản bằng **tiếng Việt có dấu**, tuyệt đối không hardcode chuỗi trong mã Java:
* Nhóm chung: `app_name` ("Theo dõi đồ án"), `save` ("Lưu"), `cancel` ("Hủy"), `delete` ("Xóa"), `edit` ("Sửa").
* Nhóm định dạng thông số có tham số truyền: `supervisor_format` ("GVHD: %s"), `date_range_format` ("%1$s → %2$s"), `sample_data_success_format` ("Đã nạp thành công %1$d đồ án, %2$d nhiệm vụ và %3$d cuộc hẹn mẫu.").
* Nhóm thông báo lỗi ràng buộc: `error_empty_project_name`, `error_invalid_due_date`, `error_task_due_after_project`.

### 3.3. `themes.xml` và `values-night/themes.xml`
* Sử dụng theme cha `Theme.Material3.DayNight.NoActionBar`.
* Tự động điều chỉnh màu nền và màu bề mặt thẻ khi người dùng chuyển đổi giữa Chế độ Sáng (Light Mode) và Chế độ Tối (Dark Mode).

---

## 4. TỆP CẤU HÌNH MANIFEST (`AndroidManifest.xml`)
* **Thuộc tính ứng dụng:**
  * `android:icon="@mipmap/ic_launcher"` và `android:roundIcon="@mipmap/ic_launcher_round"`: Icon hiển thị của app.
  * `android:label="@string/app_name"`: Tên ứng dụng tiếng Việt "Theo dõi đồ án".
  * `android:theme="@style/Theme.App"`: Áp dụng theme Material 3 cho toàn ứng dụng.
* **Khai báo Activity:**
  1. `com.example.app.MainActivity`:
     * `android:exported="true"`: Cho phép hệ điều hành Android khởi chạy từ màn hình Launcher.
     * `<intent-filter>`: Khai báo action `MAIN` và category `LAUNCHER`.
     * `android:windowSoftInputMode="adjustResize"`: Đẩy giao diện lên khi bàn phím ảo xuất hiện, chống che khuất nút FAB.
  2. `com.example.app.ui.DoAnDetailActivity`:
     * `android:exported="false"`: Chỉ được gọi nội bộ từ trong ứng dụng (bảo mật Component).
     * `android:windowSoftInputMode="adjustResize"`.

---

## 5. TỆP CẤU HÌNH BUILD GRADLE (`app/build.gradle.kts`)
* **Phiên bản SDK:**
  * `compileSdk = 37`: Tương thích với bộ công cụ biên dịch Android SDK mới nhất.
  * `minSdk = 24`: Chạy được trên các thiết bị Android từ phiên bản 7.0 (Nougat) trở lên (bao phủ >95% thiết bị trên thị trường).
  * `targetSdk = 37`: Nhắm mục tiêu hành vi hệ điều hành Android mới nhất.
* **Tính năng biên dịch (Build Features):**
  * `viewBinding = true`: Kích hoạt cơ chế tạo mã ViewBinding tự động cho tất cả các file XML layout.
* **Tối ưu hóa thư viện phụ thuộc (Dependencies):**
  * Tuân thủ triệt để nguyên tắc không phát sinh thư viện ngoài không cần thiết: chỉ sử dụng các thư viện chính thống của Google (`androidx.appcompat`, `com.google.android.material`, `androidx.recyclerview`, `androidx.constraintlayout`, `androidx.activity:activity-ktx`).
