# TÀI LIỆU GIẢI THÍCH MÃ NGUỒN & HỎI ĐÁP BẢO VỆ ĐỒ ÁN

Tài liệu này được biên soạn dành riêng cho sinh viên để chuẩn bị trả lời vấn đáp trực tiếp với thầy cô. Toàn bộ nội dung dựa trên 100% mã nguồn thực tế đang chạy trong dự án.

---

# PHẦN 1: CHI TIẾT TỪNG TỆP & TỪNG HÀM TRONG DỰ ÁN

## 1. DBHelper.java
- **Vai trò**: Lớp trung tâm quản lý cơ sở dữ liệu SQLite (`tiendo.db`), chịu trách nhiệm tạo bảng, nạp dữ liệu mẫu ban đầu và cung cấp tất cả các hàm đọc/ghi dữ liệu (CRUD) cho cả 3 bảng `DoAn`, `NhiemVu`, `CuocHen`.
- **Chi tiết các hàm**:
  - `DBHelper(Context context)`:
    - *Làm gì*: Hàm khởi tạo, gọi constructor của lớp cha `SQLiteOpenHelper` với tên CSDL là `"tiendo.db"` và phiên bản `1`.
    - *Khi nào gọi*: Được gọi khi khởi tạo đối tượng DBHelper trong các Activity (MainActivity, DoAnFormActivity, DoAnDetailActivity, NhiemVuListActivity,...).
  - `onConfigure(SQLiteDatabase db)`:
    - *Làm gì*: Gọi `db.setForeignKeyConstraintsEnabled(true)` để kích hoạt tính năng kiểm tra ràng buộc khóa ngoại trong SQLite.
    - *Khi nào gọi*: Hệ thống tự động gọi ngay khi kết nối CSDL vừa được mở, trước khi gọi `onCreate` hoặc `onUpgrade`.
  - `onCreate(SQLiteDatabase db)`:
    - *Làm gì*: Thực thi các lệnh `db.execSQL(...)` để tạo 3 bảng `DoAn`, `NhiemVu`, `CuocHen` và chèn sẵn 4 đồ án, 14 nhiệm vụ, 7 cuộc hẹn mẫu.
    - *Khi nào gọi*: Hệ thống chỉ gọi duy nhất 1 lần khi tệp CSDL `tiendo.db` chưa từng tồn tại trên thiết bị.
    - *Câu SQL*:
      - `CREATE TABLE DoAn (...)`
      - `CREATE TABLE NhiemVu (... REFERENCES DoAn(id) ON DELETE CASCADE ...)`
      - `CREATE TABLE CuocHen (... REFERENCES DoAn(id) ON DELETE CASCADE ...)`
      - Các lệnh `INSERT INTO DoAn ...`, `INSERT INTO NhiemVu ...`, `INSERT INTO CuocHen ...`
  - `onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)`:
    - *Làm gì*: Xóa 3 bảng cũ bằng `DROP TABLE IF EXISTS` và gọi lại `onCreate(db)` để tạo lại từ đầu.
    - *Khi nào gọi*: Chạy khi lập trình viên tăng hằng số `DATABASE_VERSION`.
  - `getAllDoAn()`:
    - *Làm gì*: Lấy toàn bộ danh sách đồ án sắp xếp giảm dần theo ID để hiển thị đồ án mới nhất lên đầu.
    - *Khi nào gọi*: Được gọi trong hàm `loadData()` của `MainActivity`.
    - *Câu SQL*: `SELECT id, ten, mo_ta, giang_vien, ngay_bat_dau, han_nop FROM DoAn ORDER BY id DESC`
  - `getDoAnById(int id)`:
    - *Làm gì*: Lấy thông tin của 1 đồ án dựa theo ID truyền vào.
    - *Khi nào gọi*: Gọi khi mở `DoAnFormActivity` (chế độ sửa) hoặc `DoAnDetailActivity` (xem chi tiết) và `NhiemVuListActivity`/`CuocHenListActivity` (lấy tên đồ án làm tiêu đề).
    - *Câu SQL*: `SELECT id, ten, mo_ta, giang_vien, ngay_bat_dau, han_nop FROM DoAn WHERE id = ?`
  - `insertDoAn(String ten, String moTa, String giangVien, String ngayBatDau, String hanNop)`:
    - *Làm gì*: Thêm mới một bản ghi đồ án vào bảng `DoAn`.
    - *Khi nào gọi*: Gọi khi người dùng bấm nút "Thêm đồ án" trong `DoAnFormActivity`.
    - *Câu SQL*: `INSERT INTO DoAn (ten, mo_ta, giang_vien, ngay_bat_dau, han_nop) VALUES (?, ?, ?, ?, ?)`
  - `updateDoAn(int id, String ten, String moTa, String giangVien, String ngayBatDau, String hanNop)`:
    - *Làm gì*: Cập nhật thông tin đồ án đã có theo ID.
    - *Khi nào gọi*: Gọi khi người dùng bấm nút "Lưu thay đổi" trong `DoAnFormActivity`.
    - *Câu SQL*: `UPDATE DoAn SET ten = ?, mo_ta = ?, giang_vien = ?, ngay_bat_dau = ?, han_nop = ? WHERE id = ?`
  - `deleteDoAn(int id)`:
    - *Làm gì*: Xóa 1 đồ án theo ID. Nhờ cơ chế `ON DELETE CASCADE`, SQLite tự động xóa các nhiệm vụ và cuộc hẹn thuộc đồ án đó.
    - *Khi nào gọi*: Gọi khi người dùng xác nhận xóa trong `MainActivity` hoặc `DoAnDetailActivity`.
    - *Câu SQL*: `DELETE FROM DoAn WHERE id = ?`
  - `getTienDoDoAn(int doAnId)`:
    - *Làm gì*: Đếm tổng số nhiệm vụ và tổng số nhiệm vụ đã xong (`da_hoan_thanh = 1`) của đồ án, tính tỷ lệ phần trăm và trả về chuỗi định dạng `"x/y (z%)"`.
    - *Khi nào gọi*: Gọi khi hiển thị danh sách trong `DoAnAdapter` và màn hình chi tiết trong `DoAnDetailActivity`.
    - *Câu SQL*: `SELECT COUNT(*), SUM(da_hoan_thanh) FROM NhiemVu WHERE do_an_id = ?`
  - `getNhiemVuByDoAnId(int doAnId)`:
    - *Làm gì*: Lấy danh sách nhiệm vụ của 1 đồ án theo ID, sắp xếp tăng dần theo ID.
    - *Khi nào gọi*: Gọi trong `onResume()` của `NhiemVuListActivity`.
    - *Câu SQL*: `SELECT id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh FROM NhiemVu WHERE do_an_id = ? ORDER BY id ASC`
  - `getNhiemVuById(int id)`:
    - *Làm gì*: Lấy thông tin 1 nhiệm vụ theo ID.
    - *Khi nào gọi*: Gọi trong `NhiemVuFormActivity` khi mở chế độ sửa nhiệm vụ.
    - *Câu SQL*: `SELECT id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh FROM NhiemVu WHERE id = ?`
  - `insertNhiemVu(int doAnId, String ten, String ghiChu, String hanHoanThanh)`:
    - *Làm gì*: Thêm một nhiệm vụ mới với trạng thái mặc định chưa hoàn thành (`da_hoan_thanh = 0`).
    - *Khi nào gọi*: Gọi khi bấm nút "Thêm nhiệm vụ" trong `NhiemVuFormActivity`.
    - *Câu SQL*: `INSERT INTO NhiemVu (do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh) VALUES (?, ?, ?, ?, 0)`
  - `updateNhiemVu(int id, String ten, String ghiChu, String hanHoanThanh)`:
    - *Làm gì*: Cập nhật nội dung nhiệm vụ theo ID.
    - *Khi nào gọi*: Gọi khi bấm nút "Lưu thay đổi" trong `NhiemVuFormActivity`.
    - *Câu SQL*: `UPDATE NhiemVu SET ten = ?, ghi_chu = ?, han_hoan_thanh = ? WHERE id = ?`
  - `updateNhiemVuStatus(int id, int daHoanThanh)`:
    - *Làm gì*: Cập nhật nhanh cột `da_hoan_thanh` (giá trị 1 hoặc 0).
    - *Khi nào gọi*: Gọi ngay khi người dùng tick hoặc bỏ tick CheckBox trong `NhiemVuListActivity`.
    - *Câu SQL*: `UPDATE NhiemVu SET da_hoan_thanh = ? WHERE id = ?`
  - `deleteNhiemVu(int id)`:
    - *Làm gì*: Xóa 1 nhiệm vụ theo ID.
    - *Khi nào gọi*: Gọi khi người dùng xác nhận xóa nhiệm vụ trong `NhiemVuListActivity`.
    - *Câu SQL*: `DELETE FROM NhiemVu WHERE id = ?`
  - `getCuocHenByDoAnId(int doAnId)`:
    - *Làm gì*: Lấy danh sách cuộc hẹn của đồ án, sắp xếp tăng dần theo thời gian hẹn.
    - *Khi nào gọi*: Gọi trong `onResume()` của `CuocHenListActivity`.
    - *Câu SQL*: `SELECT id, do_an_id, tieu_de, thoi_gian, dia_diem, ghi_chu FROM CuocHen WHERE do_an_id = ? ORDER BY thoi_gian ASC`
  - `getCuocHenById(int id)`:
    - *Làm gì*: Lấy thông tin 1 cuộc hẹn theo ID để hiển thị lên form sửa.
    - *Khi nào gọi*: Gọi trong `CuocHenFormActivity` khi mở chế độ sửa cuộc hẹn.
    - *Câu SQL*: `SELECT id, do_an_id, tieu_de, thoi_gian, dia_diem, ghi_chu FROM CuocHen WHERE id = ?`
  - `insertCuocHen(int doAnId, String tieuDe, String thoiGian, String diaDiem, String ghiChu)`:
    - *Làm gì*: Thêm mới 1 cuộc hẹn vào SQLite.
    - *Khi nào gọi*: Gọi khi bấm nút "Thêm cuộc hẹn" trong `CuocHenFormActivity`.
    - *Câu SQL*: `INSERT INTO CuocHen (do_an_id, tieu_de, thoi_gian, dia_diem, ghi_chu) VALUES (?, ?, ?, ?, ?)`
  - `updateCuocHen(int id, String tieuDe, String thoiGian, String diaDiem, String ghiChu)`:
    - *Làm gì*: Cập nhật cuộc hẹn đã có theo ID.
    - *Khi nào gọi*: Gọi khi bấm nút "Lưu thay đổi" trong `CuocHenFormActivity`.
    - *Câu SQL*: `UPDATE CuocHen SET tieu_de = ?, thoi_gian = ?, dia_diem = ?, ghi_chu = ? WHERE id = ?`
  - `deleteCuocHen(int id)`:
    - *Làm gì*: Xóa 1 cuộc hẹn theo ID.
    - *Khi nào gọi*: Gọi khi người dùng xác nhận xóa trong `CuocHenListActivity`.
    - *Câu SQL*: `DELETE FROM CuocHen WHERE id = ?`

---

## 2. Các Lớp Dữ Liệu Thuần: DoAn.java, NhiemVu.java, CuocHen.java
- **DoAn.java**:
  - *Vai trò*: Lớp chứa thông tin đồ án (`id`, `ten`, `moTa`, `giangVien`, `ngayBatDau`, `hanNop`).
  - *Các hàm*: 2 hàm constructor (có ID và không có ID) và các hàm getter đọc giá trị (`getId()`, `getTen()`, `getMoTa()`, `getGiangVien()`, `getNgayBatDau()`, `getHanNop()`).
- **NhiemVu.java**:
  - *Vai trò*: Lớp chứa thông tin nhiệm vụ (`id`, `doAnId`, `ten`, `ghiChu`, `hanHoanThanh`, `daHoanThanh`).
  - *Các hàm*: Constructor và các getter tương ứng (`getId()`, `getDoAnId()`, `getTen()`, `getGhiChu()`, `getHanHoanThanh()`, `getDaHoanThanh()`).
- **CuocHen.java**:
  - *Vai trò*: Lớp chứa thông tin cuộc hẹn (`id`, `doAnId`, `tieuDe`, `thoiGian`, `diaDiem`, `ghiChu`).
  - *Các hàm*: Constructor và các getter tương ứng (`getId()`, `getDoAnId()`, `getTieuDe()`, `getThoiGian()`, `getDiaDiem()`, `getGhiChu()`).

---

## 3. MainActivity.java & DoAnAdapter.java
- **MainActivity.java**:
  - *Vai trò*: Màn hình khởi động ứng dụng, hiển thị danh sách các đồ án lên RecyclerView và nút bấm thêm đồ án mới.
  - *Các hàm*:
    - `onCreate(Bundle savedInstanceState)`: Thiết lập layout `activity_main`, ánh xạ RecyclerView, nút FAB, khởi tạo `DoAnAdapter` và gắn layout manager.
    - `onResume()`: Tự động chạy lại mỗi khi Activity hiển thị; gọi hàm `loadData()` để đảm bảo dữ liệu luôn mới nhất khi quay về từ màn hình khác.
    - `loadData()`: Đọc `dbHelper.getAllDoAn()`, nạp vào danh sách và gọi `adapter.notifyDataSetChanged()`; ẩn/hiện thông báo rỗng.
    - `onItemClick(DoAn doAn)`: Mở `DoAnDetailActivity` qua `Intent.putExtra("do_an_id", doAn.getId())`.
    - `onEditClick(DoAn doAn)`: Mở `DoAnFormActivity` qua `Intent.putExtra("do_an_id", doAn.getId())`.
    - `onDeleteClick(DoAn doAn)`: Bật hộp thoại `AlertDialog.Builder` xác nhận xóa đồ án; nếu người dùng chọn "Xóa", gọi `dbHelper.deleteDoAn(id)` và tải lại danh sách.
- **DoAnAdapter.java**:
  - *Vai trò*: Cầu nối hiển thị danh sách `ArrayList<DoAn>` lên các dòng của RecyclerView.
  - *Các hàm*:
    - `onCreateViewHolder(...)`: Nạp layout dòng `item_do_an.xml` và trả về một `ViewHolder`.
    - `onBindViewHolder(...)`: Đổ dữ liệu từ `DoAn` vào TextView tên, giảng viên, thời gian; gọi `dbHelper.getTienDoDoAn(id)` để lấy chuỗi tiến độ `"x/y (z%)"` và cập nhật thanh `ProgressBar`; gắn sự kiện click xem chi tiết, sửa và xóa.
    - `getItemCount()`: Trả về số lượng phần tử của danh sách (`list.size()`).
    - Lớp `ViewHolder`: Kế thừa `RecyclerView.ViewHolder`, dùng `findViewById` để lưu tham chiếu tới các View trên từng dòng.

---

## 4. DoAnFormActivity.java & DoAnDetailActivity.java
- **DoAnFormActivity.java**:
  - *Vai trò*: Màn hình form thêm mới hoặc chỉnh sửa đồ án.
  - *Các hàm*:
    - `onCreate(...)`: Ánh xạ View, kiểm tra `do_an_id` từ Intent. Nếu `do_an_id != -1` thì gọi `loadDoAnData` để nạp dữ liệu cũ; gắn sự kiện chọn ngày và lưu.
    - `loadDoAnData(int id)`: Đọc đồ án từ `dbHelper.getDoAnById(id)` và điền dữ liệu vào các ô EditText.
    - `moHopThoaiChonNgay(EditText targetEdt)`: Mở `DatePickerDialog` nguyên bản của Android, khi người dùng chọn xong sẽ format thành chuỗi `yyyy-MM-dd` và gán vào ô EditText.
    - `luuDoAn()`: Kiểm tra tên không được rỗng (`setError`), kiểm tra hạn nộp không trước ngày bắt đầu (`hanNop.compareTo(ngayBatDau) < 0`), gọi `dbHelper.insertDoAn` hoặc `dbHelper.updateDoAn`, sau đó gọi `finish()` để đóng màn hình.
- **DoAnDetailActivity.java**:
  - *Vai trò*: Màn hình xem chi tiết đồ án, xem tiến độ thực tế, cung cấp 2 nút điều hướng lớn tới danh sách Nhiệm vụ và Lịch hẹn.
  - *Các hàm*:
    - `onCreate(...)`: Đọc `do_an_id`, ánh xạ View, gán sự kiện mở danh sách nhiệm vụ, cuộc hẹn, nút sửa, nút xóa.
    - `onResume()`: Tự động gọi `loadDoAnData()` để cập nhật lại thông tin và tiến độ khi quay về từ màn hình Nhiệm vụ hoặc Form sửa.
    - `loadDoAnData()`: Lấy đồ án từ SQLite và chuỗi tiến độ từ `dbHelper.getTienDoDoAn(doAnId)`, hiển thị lên TextView và thanh ProgressBar.
    - `hienThiHopThoaiXacNhanXoa()`: Bật `AlertDialog` xác nhận xóa đồ án và toàn bộ nhiệm vụ/cuộc hẹn liên quan, gọi `dbHelper.deleteDoAn(doAnId)` rồi gọi `finish()`.

---

## 5. NhiemVuListActivity.java, NhiemVuAdapter.java & NhiemVuFormActivity.java
- **NhiemVuListActivity.java**:
  - *Vai trò*: Màn hình quản lý danh sách nhiệm vụ của đồ án được chọn.
  - *Các hàm*:
    - `onCreate(...)`: Đọc `do_an_id`, cài đặt Toolbar với tên đồ án, khởi tạo RecyclerView và `NhiemVuAdapter`.
    - `onResume()`: Tự động gọi `loadData()` để tải lại danh sách nhiệm vụ khi quay về từ màn hình form.
    - `loadData()`: Gọi `dbHelper.getNhiemVuByDoAnId(doAnId)` và `adapter.notifyDataSetChanged()`.
    - `onToggleStatus(NhiemVu nv, boolean isCompleted)`: Gọi `dbHelper.updateNhiemVuStatus(nv.getId(), isCompleted ? 1 : 0)` để lưu ngay trạng thái vào SQLite và tải lại danh sách.
    - `onEdit(NhiemVu nv)`: Mở `NhiemVuFormActivity` kèm `do_an_id` và `nhiem_vu_id`.
    - `onDelete(NhiemVu nv)`: Bật `AlertDialog` xác nhận xóa nhiệm vụ, gọi `dbHelper.deleteNhiemVu(nv.getId())`.
- **NhiemVuAdapter.java**:
  - *Vai trò*: Hiển thị danh sách nhiệm vụ lên RecyclerView.
  - *Các hàm*:
    - `onBindViewHolder(...)`: Đổ dữ liệu tên, ghi chú, hạn; kiểm tra trạng thái `daHoanThanh == 1` để tick CheckBox và bật cờ gạch ngang chữ (`STRIKE_THRU_TEXT_FLAG`); dùng `cbHoanThanh.setOnClickListener` để xử lý tick trạng thái an toàn.
- **NhiemVuFormActivity.java**:
  - *Vai trò*: Màn hình thêm mới / cập nhật nhiệm vụ.
  - *Các hàm*: `onCreate`, `loadNhiemVuData`, `moHopThoaiChonNgay` (mở `DatePickerDialog`), `luuNhiemVu` (kiểm tra tên không rỗng, gọi `insertNhiemVu` hoặc `updateNhiemVu`, kết thúc bằng `finish()`).

---

## 6. CuocHenListActivity.java, CuocHenAdapter.java & CuocHenFormActivity.java
- **CuocHenListActivity.java**:
  - *Vai trò*: Màn hình quản lý danh sách cuộc hẹn trao đổi với giảng viên của đồ án được chọn.
  - *Các hàm*:
    - `onCreate(...)`: Đọc `do_an_id`, đặt tiêu đề Toolbar theo tên đồ án, khởi tạo `CuocHenAdapter`.
    - `onResume()`: Gọi `loadData()` để đọc danh sách cuộc hẹn đã sắp xếp tăng dần theo thời gian (`ORDER BY thoi_gian ASC`).
    - `onEdit(CuocHen ch)`: Mở `CuocHenFormActivity` ở chế độ cập nhật.
    - `onDelete(CuocHen ch)`: Bật `AlertDialog` xác nhận xóa cuộc hẹn, gọi `dbHelper.deleteCuocHen(ch.getId())`.
- **CuocHenAdapter.java**:
  - *Vai trò*: Hiển thị danh sách cuộc hẹn.
  - *Các hàm*:
    - `onBindViewHolder(...)`: Gán tiêu đề, thời gian, địa điểm, ghi chú; lấy thời gian hiện tại qua `new SimpleDateFormat("yyyy-MM-dd HH:mm")` và so sánh `ch.getThoiGian().compareTo(currentDateTime) < 0` để đổi nhãn badge "Đã qua" (màu đỏ) hoặc "Sắp tới" (màu xanh).
- **CuocHenFormActivity.java**:
  - *Vai trò*: Màn hình thêm mới / cập nhật cuộc hẹn.
  - *Các hàm*:
    - `moHopThoaiChonNgayGio()`: Mở liên tiếp `DatePickerDialog` (chọn ngày) và `TimePickerDialog` (chọn giờ) để ghép thành chuỗi định dạng chuẩn `yyyy-MM-dd HH:mm`.
    - `luuCuocHen()`: Kiểm tra tiêu đề và thời gian không được để trống, gọi `dbHelper.insertCuocHen` hoặc `updateCuocHen`, kết thúc bằng `finish()`.

---

# PHẦN 2: LUỒNG CHẠY CỦA 4 THAO TÁC CHÍNH

### 1. Luồng Mở App Xem Danh Sách Đồ Án
1. Người dùng bấm biểu tượng ứng dụng -> Hệ điều hành kích hoạt `MainActivity`.
2. Trong `MainActivity.onCreate()`:
   - Gọi `new DBHelper(this)`. Nếu ứng dụng chạy lần đầu trên máy, `DBHelper.onCreate()` được kích hoạt, tạo 3 bảng và nạp sẵn 4 đồ án mẫu.
   - Ánh xạ RecyclerView và gắn `DoAnAdapter`.
3. Hàm `onResume()` của `MainActivity` được kích hoạt ngay sau đó:
   - Gọi `loadData()` -> gọi `dbHelper.getAllDoAn()` (thực thi `SELECT ... FROM DoAn ORDER BY id DESC`).
   - Kết quả trả về `ArrayList<DoAn>`.
   - `adapter.notifyDataSetChanged()` vẽ danh sách các đồ án lên màn hình.
   - Với mỗi dòng đồ án, `DoAnAdapter.onBindViewHolder()` gọi `dbHelper.getTienDoDoAn(id)` để đếm số nhiệm vụ hoàn thành và vẽ thanh tiến độ tương ứng.

### 2. Luồng Thêm Một Đồ Án Mới
1. Tại `MainActivity`, người dùng bấm nút tròn `+` (FloatingActionButton).
2. `MainActivity` tạo `Intent` chuyển sang `DoAnFormActivity` và gọi `startActivity(intent)`.
3. Trong `DoAnFormActivity`:
   - Người dùng nhập tên đề tài, giảng viên, mô tả.
   - Bấm nút "Chọn" ngày bắt đầu và hạn nộp -> `DatePickerDialog` hiện lên để chọn ngày -> điền chuỗi `yyyy-MM-dd` vào ô nhập.
   - Bấm nút "Thêm đồ án" -> Hàm `luuDoAn()` kiểm tra tên không rỗng, kiểm tra hạn nộp không trước ngày bắt đầu.
   - Thực thi `dbHelper.insertDoAn(...)` (chạy câu lệnh `INSERT INTO DoAn ...`).
   - Hiện `Toast` báo thành công và gọi `finish()` để đóng màn hình form.
4. Màn hình quay về `MainActivity` -> Hàm `onResume()` tự động chạy -> Gọi `loadData()` tải lại danh sách -> Đồ án mới xuất hiện ngay trên đầu danh sách.

### 3. Luồng Đánh Dấu (Tick) Hoàn Thành Một Nhiệm Vụ
1. Tại màn hình danh sách nhiệm vụ `NhiemVuListActivity`, người dùng chạm vào CheckBox bên cạnh một nhiệm vụ.
2. Sự kiện `setOnClickListener` trên CheckBox được kích hoạt -> Gọi hàm `onToggleStatus(nv, isChecked)`.
3. `NhiemVuListActivity` thực hiện:
   - Gọi `dbHelper.updateNhiemVuStatus(id, isChecked ? 1 : 0)`.
   - Lệnh SQL chạy: `UPDATE NhiemVu SET da_hoan_thanh = ? WHERE id = ?`.
   - Gọi lại `loadData()`: Dòng nhiệm vụ vừa tick được gạch ngang chữ và CheckBox giữ nguyên dấu tick.
4. Khi người dùng bấm nút "Quay lại" trên Toolbar để trở về `DoAnDetailActivity`:
   - Hàm `onResume()` của `DoAnDetailActivity` tự động chạy -> Gọi `loadDoAnData()`.
   - Gọi `dbHelper.getTienDoDoAn(doAnId)`: Câu lệnh `SELECT COUNT(*), SUM(da_hoan_thanh)...` tính ra số lượng hoàn thành mới.
   - TextView tiến độ và thanh ProgressBar tự động nhảy số phần trăm tăng lên ngay trước mắt người dùng.

### 4. Luồng Xóa Một Đồ Án (Cơ Chế Khóa Ngoại Cascade)
1. Người dùng bấm nút biểu tượng thùng rác (hoặc nhấn giữ) trên một đồ án tại `MainActivity` (hoặc trong `DoAnDetailActivity`).
2. Một hộp thoại `AlertDialog` xuất hiện cảnh báo: *"Bạn có chắc chắn muốn xóa đồ án này không? Toàn bộ nhiệm vụ và lịch hẹn liên quan sẽ tự động bị xóa."*
3. Người dùng chọn "Xóa":
   - Gọi `dbHelper.deleteDoAn(id)`.
   - Lệnh SQL thực thi: `DELETE FROM DoAn WHERE id = ?`.
   - **Cơ chế Cascade**: Nhờ có dòng `db.setForeignKeyConstraintsEnabled(true)` trong hàm `onConfigure` và khi tạo bảng có khai báo `REFERENCES DoAn(id) ON DELETE CASCADE`, SQLite nội bộ sẽ tự động tìm và xóa toàn bộ các bản ghi trong bảng `NhiemVu` và bảng `CuocHen` có `do_an_id` tương ứng, không để lại dữ liệu rác (dữ liệu mồ côi).
   - Danh sách gọi `loadData()` -> Đồ án bị xóa biến mất khỏi màn hình, `Toast` thông báo thành công.

---

# PHẦN 3: CÂU HỎI THẦY CÔ THƯỜNG HỎI & CÂU TRẢ LỜI NGẮN GỌN

#### Câu 1: Lớp `SQLiteOpenHelper` dùng để làm gì trong Android?
> **Trả lời:** `SQLiteOpenHelper` là lớp trừu tượng có sẵn của Android dùng để quản lý việc tạo mới, mở kết nối và nâng cấp phiên bản cho cơ sở dữ liệu SQLite một cách có hệ thống. Nó giúp lập trình viên không phải tự viết mã kiểm tra sự tồn tại của tệp tin CSDL.

#### Câu 2: Phương thức `onCreate(SQLiteDatabase db)` của `SQLiteOpenHelper` chạy khi nào?
> **Trả lời:** Phương thức `onCreate` chỉ chạy **duy nhất một lần** khi tệp CSDL (`tiendo.db`) chưa từng tồn tại trên thiết bị. Trong bài, em dùng nó để tạo 3 bảng `DoAn`, `NhiemVu`, `CuocHen` và nạp dữ liệu mẫu ban đầu.

#### Câu 3: Vì sao phải dùng dấu `?` trong câu lệnh SQL (`new Object[]{...}`) thay vì cộng chuỗi trực tiếp?
> **Trả lời:** Có 2 lý do cực kỳ quan trọng:
> 1. **Bảo mật**: Ngăn chặn hoàn toàn lỗi tấn công SQL Injection.
> 2. **Xử lý ký tự đặc biệt**: Nếu người dùng nhập chuỗi có dấu nháy đơn (`'`), dấu phẩy hoặc tiếng Việt, việc dùng tham số `?` sẽ tự động escape ký tự an toàn, tránh bị lỗi cú pháp làm sập ứng dụng (crash app).

#### Câu 4: Đối tượng `Cursor` là gì? Vì sao bắt buộc phải gọi `cursor.close()` sau khi dùng xong?
> **Trả lời:** `Cursor` là con trỏ duyệt qua tập hợp các dòng kết quả trả về từ câu truy vấn `rawQuery`. Phải gọi `cursor.close()` để giải phóng tài nguyên bộ nhớ đệm và kết nối của con trỏ hệ thống; nếu quên close sẽ gây rò rỉ bộ nhớ (Memory Leak).

#### Câu 5: `getReadableDatabase()` khác với `getWritableDatabase()` như thế nào?
> **Trả lời:** Cả 2 phương thức đều mở kết nối tới CSDL. Tuy nhiên, `getWritableDatabase()` mở kết nối để có thể ghi (thêm, sửa, xóa) dữ liệu. Nếu bộ nhớ thiết bị bị đầy hoặc ổ đĩa ở chế độ chỉ đọc (Read-only), `getWritableDatabase()` sẽ ném ngoại lệ lỗi, trong khi `getReadableDatabase()` vẫn có thể mở được để người dùng đọc dữ liệu bình thường.

#### Câu 6: Vì sao lại nạp lại dữ liệu trong `onResume()` thay vì chỉ gọi trong `onCreate()`?
> **Trả lời:** `onCreate()` chỉ chạy một lần duy nhất khi Activity được sinh ra. Khi người dùng chuyển sang màn hình khác để Thêm/Sửa/Xóa rồi bấm nút "Quay lại", Activity cũ không chạy lại `onCreate()` mà chỉ chạy qua `onResume()`. Vì vậy, gọi `loadData()` trong `onResume()` đảm bảo giao diện luôn được làm mới với dữ liệu mới nhất mà không cần truyền kết quả phức tạp.

#### Câu 7: Cơ chế hoạt động của `RecyclerView`, `Adapter` và `ViewHolder` là gì?
> **Trả lời:**
> - `RecyclerView`: Khung nhìn tái sử dụng các View đã cuộn ra khỏi màn hình thay vì liên tục tạo View mới, giúp tiết kiệm tối đa bộ nhớ RAM.
> - `ViewHolder`: Lưu trữ trực tiếp các tham chiếu View (`findViewById`) của một dòng để tránh phải tìm kiếm lại nhiều lần khi cuộn.
> - `Adapter`: Đóng vai trò cầu nối, lấy dữ liệu từ `ArrayList` nạp vào các View được giữ trong `ViewHolder` thông qua hàm `onBindViewHolder`.

#### Câu 8: Làm thế nào để truyền dữ liệu giữa các Activity? Cho ví dụ trong bài?
> **Trả lời:** Sử dụng đối tượng `Intent`. Ở Activity gửi, ta dùng `intent.putExtra("key", value)` (ví dụ `intent.putExtra("do_an_id", doAn.getId())`). Ở Activity nhận, ta dùng `getIntent().getIntExtra("key", defaultValue)` (ví dụ `getIntent().getIntExtra("do_an_id", -1)`).

#### Câu 9: Thuộc tính `ON DELETE CASCADE` và `setForeignKeyConstraintsEnabled(true)` có tác dụng gì?
> **Trả lời:** Mặc định trong Android SQLite, cơ chế khóa ngoại bị tắt để tương thích ngược. Hàm `db.setForeignKeyConstraintsEnabled(true)` trong `onConfigure` sẽ bật tính năng này lên. Khi đó, nhờ khai báo `ON DELETE CASCADE` ở bảng `NhiemVu` và `CuocHen`, khi ta xóa một `DoAn`, hệ thống SQLite sẽ tự động xóa sạch tất cả các nhiệm vụ và lịch hẹn con của đồ án đó, không để lại dữ liệu rác.

#### Câu 10: Tiến độ đồ án được tính toán như thế nào trong bài?
> **Trả lời:** Tiến độ không lưu cứng vào cột nào trong CSDL mà được tính toán động thông qua hàm `dbHelper.getTienDoDoAn(doAnId)`. Hàm này chạy câu truy vấn `SELECT COUNT(*), SUM(da_hoan_thanh) FROM NhiemVu WHERE do_an_id = ?`. Tỷ lệ % được tính bằng công thức: `(daHoanThanh * 100) / tong`. Nếu tổng bằng 0 thì trả về `"0/0 (0%)"`.

#### Câu 11: Vì sao ngày giờ trong ứng dụng lại được lưu trữ dưới dạng chuỗi TEXT (`yyyy-MM-dd` và `yyyy-MM-dd HH:mm`)?
> **Trả lời:** SQLite không có kiểu dữ liệu `DATE` hay `DATETIME` riêng mà lưu ngày giờ dưới dạng chuỗi `TEXT` theo chuẩn ISO-8601. Định dạng `yyyy-MM-dd HH:mm` có đặc điểm cực kỳ thuận lợi là **thứ tự thời gian trùng khớp với thứ tự chữ cái**, do đó ta có thể dễ dàng so sánh ngày trước/sau hoặc sắp xếp tăng dần (`ORDER BY thoi_gian ASC`) chỉ bằng phép so sánh chuỗi `compareTo()` thông thường mà không cần chuyển đổi phức tạp.

#### Câu 12: Vì sao dự án dùng `findViewById` và `setOnClickListener` thuần túy mà không dùng ViewBinding hay DataBinding?
> **Trả lời:** Việc sử dụng `findViewById` và `setOnClickListener` lambda là cách tiếp cận nguyên bản (Native) cơ bản nhất của Android, bám sát 100% nội dung giảng dạy trên lớp, giúp mã nguồn tường minh, dễ đọc, dễ giải thích cho thầy cô mà không phụ thuộc vào các lớp code sinh tự động (generated classes) của thư viện ngoài.

#### Câu 13: Ứng dụng xử lý việc xoay màn hình (ngang / dọc) như thế nào? Vì sao dùng `android:configChanges`?
> **Trả lời:** Mặc định khi người dùng xoay màn hình (hoặc chạy trên giả lập màn hình ngang như BlueStacks), Android sẽ hủy (destroy) và khởi tạo lại Activity từ đầu, dễ làm mất dữ liệu đang gõ dở nếu không viết mã lưu trạng thái phức tạp. Để xử lý gọn gàng và hiệu quả, em cấu hình `android:configChanges="orientation|screenSize|keyboardHidden"` trên các Activity trong `AndroidManifest.xml`. Thuộc tính này báo cho hệ điều hành không hủy/tạo lại Activity khi xoay, giữ nguyên toàn bộ chữ đang nhập trong form. Đồng thời, toàn bộ form và màn hình chi tiết đều được bọc trong `ScrollView` để người dùng cuộn và thao tác thoải mái trên cả màn hình dọc lẫn màn hình ngang có chiều cao thấp.

#### Câu 14: Vì sao các thao tác truy vấn SQLite trong bài được viết trực tiếp trên Main Thread mà không dùng Thread hay Coroutines?
> **Trả lời:** Vì đây là ứng dụng quản lý cá nhân với cơ sở dữ liệu SQLite cục bộ có quy mô vài chục đến vài trăm bản ghi, tốc độ đọc/ghi diễn ra tức thời (dưới vài mili-giây), không gây hiện tượng giật lag (ANR). Cách viết này giúp luồng code thẳng, dễ debug và đúng với chuẩn mực các bài tập lớn môn Lập trình thiết bị di động.
