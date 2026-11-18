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


---

# PHẦN 4: TOÀN BỘ BÀI GIẢNG RECYCLERVIEW (CHƯƠNG 11, ThS. NGUYỄN HỮU TRUNG, SLIDE 182–223)

---

## 1. BẢNG ĐỐI CHIẾU BÀI GIẢNG → DỰ ÁN THỰC TẾ

| Slide | Nội dung bài giảng | Áp dụng thực tế trong dự án |
| :--- | :--- | :--- |
| **182–183** | **RecyclerView vs ListView**: ViewHolder, LayoutManager, ItemAnimator, ItemDecoration, cách bắt sự kiện | - **ViewHolder**: Bắt buộc trong cả 5 Adapter: `DoAnAdapter.DoAnViewHolder`, `NhiemVuAdapter.NhiemVuViewHolder`, `CuocHenAdapter.CuocHenViewHolder`, `ThongKeAdapter.ThongKeViewHolder`, và 3 ViewHolder trong `TimelineAdapter`.<br>- **LayoutManager**: Dùng `LinearLayoutManager` (dọc, ngang) và `GridLayoutManager` (lưới 2 cột).<br>- **ItemAnimator**: Cài đặt `DefaultItemAnimator` trên tất cả RecyclerView danh sách.<br>- **ItemDecoration**: Sử dụng `DividerItemDecoration` trong danh sách nhiệm vụ (`NhiemVuListActivity`).<br>- **Cách bắt sự kiện**: Bắt ngay trong constructor ViewHolder, không đặt trong `onBindViewHolder`. |
| **184** | **Adapter**: `onCreateViewHolder`, `onBindViewHolder` | Đã chuẩn hóa trong 5 Adapter (`DoAnAdapter`, `NhiemVuAdapter`, `CuocHenAdapter`, `ThongKeAdapter`, `TimelineAdapter`). |
| **185, 192** | **LayoutManager**: Linear (dọc/ngang), Grid, StaggeredGrid; ItemAnimator | - Cuộn dọc: `MainActivity`, `NhiemVuListActivity`, `CuocHenListActivity`, `TimelineActivity`.<br>- Cuộn ngang: Thẻ thống kê tại `DoAnDetailActivity` (`rv_thong_ke`).<br>- Lưới 2 cột: Nút chuyển đổi chế độ xem trên Toolbar `MainActivity`.<br>- StaggeredGrid: Giải thích lý thuyết vấn đáp (không áp dụng do dữ liệu đồ án cần cấu trúc đều). |
| **186–193** | **5 bước làm RecyclerView cơ bản** | Áp dụng chuẩn chỉ 100% cho 3 thực thể chính: Đồ án, Nhiệm vụ, Cuộc hẹn (xem mục 3 bên dưới). |
| **194** | **Chọn item**: `setOnClickListener` trong constructor ViewHolder, `getAdapterPosition` | Đặt toàn bộ click/long-click trong constructor ViewHolder; kiểm tra `getAdapterPosition() != RecyclerView.NO_POSITION`; click đồ án mở `DoAnDetailActivity` bằng Intent. |
| **195–210** | **Multiple View Type** | Tạo `TimelineActivity` và `TimelineAdapter` gộp cả 3 kiểu dòng: Tiêu đề phân nhóm (`HEADER`), Nhiệm vụ (`TASK`), và Cuộc hẹn (`MEETING`) vào chung 1 RecyclerView. |
| **211–223** | **Animation**: ItemAnimator, DefaultItemAnimator, notifyItem*, addItem/removeItem/replaceItem, scrollToPosition | Viết đúng 3 hàm animation (`addItem`, `removeItem`, `replaceItem`) trong cả 3 Adapter; xóa gọi `removeItem`; tick CheckBox gọi `notifyItemChanged`; tắt nháy bằng `setSupportsChangeAnimations(false)`. |

---

## 2. RECYCLERVIEW SO VỚI LISTVIEW (6 ĐIỂM BÀI GIẢNG SLIDE 182–183)

1. **Bắt buộc sử dụng ViewHolder (Enforced ViewHolder Pattern)**:
   - *ListView*: Mô hình ViewHolder chỉ là khuyến khích (optional). Lập trình viên có thể viết ẩu bằng cách liên tục gọi `findViewById` trong hàm `getView()`, khiến ứng dụng bị giật lag nghiêm trọng khi cuộn nhanh.
   - *RecyclerView*: Cấu trúc lớp generic `RecyclerView.Adapter<VH>` **bắt buộc** phải khai báo lớp `ViewHolder`. Không có ViewHolder thì code không thể biên dịch được.
2. **Tách rời cơ chế bố cục bằng `LayoutManager`**:
   - *ListView*: Bị gắn cứng duy nhất một kiểu hiển thị là danh sách cuộn dọc 1 cột.
   - *RecyclerView*: Tách biệt hoàn toàn việc sắp xếp giao diện cho `LayoutManager`. Lập trình viên có thể linh hoạt chuyển sang cuộn dọc, cuộn ngang, dạng lưới nhiều cột (`GridLayoutManager`) hay dạng lưới so le (`StaggeredGridLayoutManager`) mà không cần sửa một dòng code nào trong Adapter.
3. **Tích hợp sẵn hệ thống hoạt ảnh (`ItemAnimator`)**:
   - *ListView*: Không có animation mặc định. Khi thêm/xóa dòng, dữ liệu biến mất hoặc nhảy ra đột ngột.
   - *RecyclerView*: Tích hợp sẵn `ItemAnimator` (mặc định là `DefaultItemAnimator`). Tự động xử lý chuyển động thêm (mờ sang rõ), xóa (mờ dần rồi co lại) và di chuyển các dòng xung quanh cực kỳ mượt mà.
4. **Trang trí và phân cách độc lập (`ItemDecoration`)**:
   - *ListView*: Đường kẻ phân cách (divider) bị gán cứng vào thuộc tính của ListView trong XML (`android:divider`).
   - *RecyclerView*: Sử dụng `ItemDecoration` độc lập hoàn toàn với Adapter. Ta có thể tự do thêm đường kẻ, căn chỉnh lề (margin/padding) cho từng dòng hoặc vẽ các thành phần trang trí đặc biệt.
5. **Cách bắt sự kiện click trên từng dòng**:
   - *ListView*: Hỗ trợ sẵn hàm `setOnItemClickListener` ở cấp độ danh sách.
   - *RecyclerView*: Bỏ hàm này, yêu cầu lập trình viên tự quản lý sự kiện. Cách làm chuẩn mực nhất theo bài giảng là **gắn sự kiện click ngay trong constructor của ViewHolder** để tái sử dụng lại đối tượng Listener, không tạo rác bộ nhớ.
6. **Khả năng hiển thị nhiều kiểu dòng (Multiple View Type) sạch sẽ**:
   - *RecyclerView* quản lý nhiều kiểu giao diện thông qua hàm `getItemViewType(position)`. Hệ thống tự động phân loại ViewType và lưu vào các ngăn đệm (Recycled Pool) riêng biệt, giúp tái chế View chính xác và tối ưu bộ nhớ RAM vượt trội so với ListView.

---

## 3. CÁC THÀNH PHẦN CỐT LÕI CỦA RECYCLERVIEW

### A. Adapter
Là cầu nối giữa tập dữ liệu (danh sách `List<T>`) và các View hiển thị:
- `onCreateViewHolder(ViewGroup parent, int viewType)`: Nạp layout XML thành View và bọc vào một `ViewHolder`. Hàm này **chỉ được gọi khi không có View cũ nào trong bộ nhớ đệm tái sử dụng được** (thường chỉ chạy 8–12 lần tương ứng với số dòng hiển thị vừa vặn trên màn hình).
- `onBindViewHolder(VH holder, int position)`: Lấy dữ liệu tại vị trí `position` trong danh sách và gán vào các View trong `holder`. Hàm này **chạy liên tục mỗi khi người dùng cuộn danh sách**.
- `getItemCount()`: Trả về tổng số phần tử của danh sách (`mList.size()`).

### B. LayoutManager
Chịu trách nhiệm định vị, đo đạc kích thước và sắp xếp vị trí của các dòng View:
- `LinearLayoutManager`: Cuộn dọc (`VERTICAL`) hoặc cuộn ngang (`HORIZONTAL`).
- `GridLayoutManager`: Bố trí thành lưới $N$ cột đều nhau (trong dự án dùng lưới 2 cột cho đồ án).
- `StaggeredGridLayoutManager`: Bố trí thành lưới so le (các ô có chiều cao khác nhau tự do như bảng ảnh Pinterest).

### C. ItemAnimator
Quản lý các hiệu ứng hoạt ảnh khi danh sách có sự biến động:
- `ItemAnimator`: Lớp cơ sở trừu tượng quy định vòng đời chuyển động (Add, Remove, Move, Change).
- `SimpleItemAnimator`: Lớp dẫn xuất giúp đơn giản hóa việc tính toán hoạt ảnh, cung cấp hàm `setSupportsChangeAnimations(boolean)` để tắt hiệu ứng nháy khi cập nhật dòng.
- `DefaultItemAnimator`: Lớp hoạt ảnh mặc định của Android:
  - Thêm phần tử: Alpha từ $0 \rightarrow 1$ (mờ sang rõ).
  - Xóa phần tử: Alpha từ $1 \rightarrow 0$ (rõ sang mờ rồi biến mất, các dòng dưới trượt lên).
  - Thay đổi phần tử: Alpha từ $1 \rightarrow 0$ rồi $0 \rightarrow 1$.

---

## 4. 5 BƯỚC LÀM RECYCLERVIEW CƠ BẢN TRONG BÀI GIẢNG & VỊ TRÍ CODE THẬT

- **Bước 1: Tạo Model implements Serializable**
  - *Mô tả*: Khai báo các thuộc tính, constructor có/không tham số, đầy đủ getter và setter theo chuẩn JavaBean.
  - *Code thật*: [DoAn.java](file:///d:/K%C3%AC%207/B%C3%A0i%20t%E1%BA%ADp%20l%E1%BB%9Bn%20LTTBDD/app/src/main/java/com/example/app/DoAn.java), [NhiemVu.java](file:///d:/K%C3%AC%207/B%C3%A0i%20t%E1%BA%ADp%20l%E1%BB%9Bn%20LTTBDD/app/src/main/java/com/example/app/NhiemVu.java), [CuocHen.java](file:///d:/K%C3%AC%207/B%C3%A0i%20t%E1%BA%ADp%20l%E1%BB%9Bn%20LTTBDD/app/src/main/java/com/example/app/CuocHen.java).
- **Bước 2: Khai báo thẻ RecyclerView trong Layout Activity**
  - *Mô tả*: Thêm `<androidx.recyclerview.widget.RecyclerView>` vào giao diện, đặt ID dạng `rv_...`.
  - *Code thật*: `android:id="@+id/rv_do_an"` trong [activity_main.xml](file:///d:/K%C3%AC%207/B%C3%A0i%20t%E1%BA%ADp%20l%E1%BB%9Bn%20LTTBDD/app/src/main/res/layout/activity_main.xml), `android:id="@+id/rv_nhiem_vu"` trong [activity_nhiem_vu_list.xml](file:///d:/K%C3%AC%207/B%C3%A0i%20t%E1%BA%ADp%20l%E1%BB%9Bn%20LTTBDD/app/src/main/res/layout/activity_nhiem_vu_list.xml), `android:id="@+id/rv_cuoc_hen"` trong [activity_cuoc_hen_list.xml](file:///d:/K%C3%AC%207/B%C3%A0i%20t%E1%BA%ADp%20l%E1%BB%9Bn%20LTTBDD/app/src/main/res/layout/activity_cuoc_hen_list.xml).
- **Bước 3: Thiết kế Layout cho một dòng (Row layout)**
  - *Mô tả*: Tệp XML riêng biệt chứa các View thành phần, TextView có thuộc tính `android:ellipsize="end"`.
  - *Code thật*: [row_do_an.xml](file:///d:/K%C3%AC%207/B%C3%A0i%20t%E1%BA%ADp%20l%E1%BB%9Bn%20LTTBDD/app/src/main/res/layout/row_do_an.xml), [row_nhiem_vu.xml](file:///d:/K%C3%AC%207/B%C3%A0i%20t%E1%BA%ADp%20l%E1%BB%9Bn%20LTTBDD/app/src/main/res/layout/row_nhiem_vu.xml), [row_cuoc_hen.xml](file:///d:/K%C3%AC%207/B%C3%A0i%20t%E1%BA%ADp%20l%E1%BB%9Bn%20LTTBDD/app/src/main/res/layout/row_cuoc_hen.xml).
- **Bước 4: Viết Adapter và ViewHolder**
  - *Mô tả*: Kế thừa `RecyclerView.Adapter<XxxAdapter.XxxViewHolder>`, biến thành viên tiền tố `m` (`mContext`, `mDoAns`, `mLayoutInflater`), `onCreateViewHolder` inflate `attachToRoot = false`, `onBindViewHolder` gán dữ liệu, ViewHolder là inner class gọi `findViewById`.
  - *Code thật*: [DoAnAdapter.java](file:///d:/K%C3%AC%207/B%C3%A0i%20t%E1%BA%ADp%20l%E1%BB%9Bn%20LTTBDD/app/src/main/java/com/example/app/DoAnAdapter.java), [NhiemVuAdapter.java](file:///d:/K%C3%AC%207/B%C3%A0i%20t%E1%BA%ADp%20l%E1%BB%9Bn%20LTTBDD/app/src/main/java/com/example/app/NhiemVuAdapter.java), [CuocHenAdapter.java](file:///d:/K%C3%AC%207/B%C3%A0i%20t%E1%BA%ADp%20l%E1%BB%9Bn%20LTTBDD/app/src/main/java/com/example/app/CuocHenAdapter.java).
- **Bước 5: Thiết lập trong Activity**
  - *Mô tả*: `findViewById` lấy RecyclerView, gán `setAdapter(mAdapter)` và `setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))`.
  - *Code thật*: `onCreate()` trong [MainActivity.java](file:///d:/K%C3%AC%207/B%C3%A0i%20t%E1%BA%ADp%20l%E1%BB%9Bn%20LTTBDD/app/src/main/java/com/example/app/MainActivity.java), [NhiemVuListActivity.java](file:///d:/K%C3%AC%207/B%C3%A0i%20t%E1%BA%ADp%20l%E1%BB%9Bn%20LTTBDD/app/src/main/java/com/example/app/NhiemVuListActivity.java), [CuocHenListActivity.java](file:///d:/K%C3%AC%207/B%C3%A0i%20t%E1%BA%ADp%20l%E1%BB%9Bn%20LTTBDD/app/src/main/java/com/example/app/CuocHenListActivity.java).

---

## 5. BẮT SỰ KIỆN CHỌN ITEM (SLIDE 194)

- **Vị trí bắt sự kiện chuẩn**: Đặt `itemView.setOnClickListener` và `setOnLongClickListener` **ngay bên trong constructor của ViewHolder**.
- **Vì sao không đặt trong `onBindViewHolder`?**: Vì `onBindViewHolder` chạy liên tục mỗi khi cuộn qua một dòng. Nếu đặt listener ở đó, mỗi lần bind hệ thống sẽ tạo mới một đối tượng anonymous listener trong bộ nhớ Heap, gây áp lực lớn cho bộ thu dọn rác (Garbage Collector). Đặt trong constructor của ViewHolder thì listener chỉ được tạo đúng một lần khi ViewHolder sinh ra.
- **Cách lấy đối tượng an toàn**:
  ```java
  int position = getAdapterPosition();
  if (position != RecyclerView.NO_POSITION && mDoAns != null && position < mDoAns.size()) {
      DoAn doAn = mDoAns.get(position);
      // Mở Activity bằng Intent
      Intent intent = new Intent(mContext, DoAnDetailActivity.class);
      intent.putExtra("do_an_id", doAn.getId());
      mContext.startActivity(intent);
  }
  ```
  *Lưu ý*: Bắt buộc phải kiểm tra `position != RecyclerView.NO_POSITION` để tránh lỗi ứng dụng khi người dùng vô tình bấm vào dòng đang trong hiệu ứng bị xóa khỏi danh sách.

---

## 6. KỸ THUẬT MULTIPLE VIEW TYPE (SLIDE 195–210) & TIMELINEADAPTER

- **Mục đích**: Hiển thị nhiều kiểu dòng có cấu trúc giao diện hoàn toàn khác nhau trong cùng một danh sách RecyclerView duy nhất.
- **Áp dụng trong bài**: Màn hình Dòng thời gian đồ án ([TimelineActivity.java](file:///d:/K%C3%AC%207/B%C3%A0i%20t%E1%BA%ADp%20l%E1%BB%9Bn%20LTTBDD/app/src/main/java/com/example/app/TimelineActivity.java)) sử dụng [TimelineAdapter.java](file:///d:/K%C3%AC%207/B%C3%A0i%20t%E1%BA%ADp%20l%E1%BB%9Bn%20LTTBDD/app/src/main/java/com/example/app/TimelineAdapter.java):
  - Dữ liệu: `List<Object> mObjects` chứa lẫn chuỗi `String` (tiêu đề nhóm), đối tượng `NhiemVu` và đối tượng `CuocHen`.
  - Hằng số định danh: `HEADER = 0`, `TASK = 1`, `MEETING = 2`.
  - Hàm quyết định kiểu:
    ```java
    @Override
    public int getItemViewType(int position) {
        Object obj = mObjects.get(position);
        if (obj instanceof String) return HEADER;
        else if (obj instanceof NhiemVu) return TASK;
        else if (obj instanceof CuocHen) return MEETING;
        return -1;
    }
    ```
  - `onCreateViewHolder`: Dùng `switch(viewType)` để nạp `row_header.xml`, `row_timeline_task.xml` hoặc `row_timeline_meeting.xml`.
  - `onBindViewHolder`: Dùng `switch(getItemViewType(position))` để ép kiểu đối tượng `HeaderViewHolder`, `TaskViewHolder` hoặc `MeetingViewHolder` tương ứng.

---

## 7. ANIMATION VÀ CÁC HÀM NOTIFY TRONG BÀI GIẢNG (SLIDE 211–223)

### A. So sánh `notifyDataSetChanged()` và các hàm `notifyItem*()`
- `notifyDataSetChanged()`: Thông báo toàn bộ tập dữ liệu đã thay đổi hoàn toàn. RecyclerView buộc phải hủy hết tất cả các View hiện tại và vẽ lại từ đầu. **Hàm này không có bất kỳ hiệu ứng hoạt ảnh nào** (slide 212). Thường dùng khi tải mới toàn bộ dữ liệu từ CSDL vào `onResume()`.
- `notifyItemInserted(int position)`: Báo chèn 1 phần tử mới tại `position`. Kích hoạt hiệu ứng fade-in và đẩy các phần tử phía dưới xuống.
- `notifyItemRemoved(int position)`: Báo xóa 1 phần tử tại `position`. Kích hoạt hiệu ứng fade-out và kéo các phần tử phía dưới lên lấp chỗ trống.
- `notifyItemChanged(int position)`: Báo 1 phần tử thay đổi nội dung. Kích hoạt hiệu ứng mờ và hiện lại nội dung mới.
- `notifyItemRangeInserted`, `notifyItemRangeRemoved`, `notifyItemRangeChanged`: Báo biến động trên một khoảng liên tiếp nhiều phần tử.

### B. 3 Hàm cốt lõi theo mẫu bài giảng trong Adapter
Đã được cài đặt đầy đủ trong `DoAnAdapter`, `NhiemVuAdapter`, `CuocHenAdapter`:
```java
// 1. Thêm phần tử có animation (slide 213–215)
public void addItem(int position, T item) {
    if (mList != null && position >= 0 && position <= mList.size()) {
        mList.add(position, item);
        notifyItemInserted(position);
    }
}

// 2. Xóa phần tử có animation (slide 216–218)
public void removeItem(int position) {
    if (mList != null && position >= 0 && position < mList.size()) {
        mList.remove(position);
        notifyItemRemoved(position);
    }
}

// 3. Thay thế/Cập nhật phần tử có animation (slide 219–221)
public void replaceItem(int position, T item) {
    if (mList != null && position >= 0 && position < mList.size()) {
        mList.remove(position);
        mList.add(position, item);
        notifyItemChanged(position);
    }
}
```

### C. Quy tắc vàng khi gọi Animation
**Bắt buộc phải cập nhật dữ liệu trong List trước rồi mới được gọi hàm notify**:
1. `mList.remove(position);` $\rightarrow$ rồi mới `notifyItemRemoved(position);`
2. `mList.add(position, item);` $\rightarrow$ rồi mới `notifyItemInserted(position);`
*Hậu quả nếu làm ngược*: RecyclerView đối chiếu kích thước tập dữ liệu của Adapter với danh sách thực tế thấy không khớp, sẽ ném ngoại lệ `java.lang.IndexOutOfBoundsException` làm sập ứng dụng ngay lập tức!

### D. Tắt hiệu ứng nháy màn hình khi tick CheckBox (Slide 219)
Khi người dùng tick vào CheckBox của nhiệm vụ, ta gọi `notifyItemChanged(position)`. Mặc định `DefaultItemAnimator` sẽ làm mờ dòng đó và hiện lại (alpha $1 \rightarrow 0 \rightarrow 1$), gây cảm giác nhấp nháy khó chịu. Ta khắc phục bằng cách tắt hoạt ảnh thay đổi:
```java
if (mRvNhiemVu.getItemAnimator() instanceof SimpleItemAnimator) {
    ((SimpleItemAnimator) mRvNhiemVu.getItemAnimator()).setSupportsChangeAnimations(false);
}
```

---

## 8. BỘ 17 CÂU HỎI & TRẢ LỜI VẤN ĐÁP BẢO VỆ CHUYÊN SÂU VỀ RECYCLERVIEW

#### Câu 1: Vì sao RecyclerView lại bắt buộc phải dùng mô hình ViewHolder?
> **Trả lời:** Trong ListView cũ, lập trình viên có thể bỏ qua ViewHolder và liên tục gọi `findViewById()` trong `getView()`. Mỗi lần gọi `findViewById()`, hệ thống phải duyệt cây giao diện (View Tree) tốn rất nhiều CPU. RecyclerView bắt buộc ViewHolder nhằm đảm bảo các tham chiếu View chỉ được tìm kiếm 1 lần khi View tạo ra, giúp quá trình cuộn danh sách đạt tốc độ 60fps mượt mà.

#### Câu 2: Phương thức `onCreateViewHolder()` có được gọi cho mọi phần tử trong danh sách không?
> **Trả lời:** **Không**. Nó chỉ được gọi khoảng 8 đến 12 lần tương ứng với số dòng đủ lấp đầy màn hình điện thoại (cộng thêm 1–2 View đệm ở hai đầu). Khi người dùng cuộn, các dòng cuộn ra khỏi màn hình sẽ được đưa vào ngăn đệm tái chế (Recycler Pool) và chuyển cho `onBindViewHolder()` nạp dữ liệu mới vào mà không hề gọi lại `onCreateViewHolder()`.

#### Câu 3: Phương thức `onBindViewHolder()` được gọi khi nào?
> **Trả lời:** Được gọi liên tục mỗi khi có một dòng sắp sửa xuất hiện trên màn hình khi người dùng cuộn tới, hoặc khi lập trình viên chủ động gọi các hàm `notifyItemChanged()`, `notifyDataSetChanged()`.

#### Câu 4: Phương thức `getAdapterPosition()` (hoặc `getBindingAdapterPosition()`) dùng để làm gì và vì sao cần kiểm tra `!= RecyclerView.NO_POSITION`?
> **Trả lời:** Dùng để lấy chỉ số vị trí thực tế của dòng hiện tại trong Adapter tại thời điểm người dùng click chuột. Cần kiểm tra `!= NO_POSITION` (giá trị `-1`) vì khi một dòng đang chạy hiệu ứng xóa hoặc di chuyển (animation), vị trí của nó tạm thời không xác định; nếu không kiểm tra sẽ gây văng app khi truy cập `mList.get(-1)`.

#### Câu 5: Vì sao RecyclerView không thể hiển thị được nếu thiếu `LayoutManager`?
> **Trả lời:** Bản thân RecyclerView chỉ là một container rỗng làm nhiệm vụ tái chế View. Nó không tự tính toán tọa độ $X, Y$ của từng phần tử. Trách nhiệm đo đạc (measure) và bố trí (layout) vị trí các View con hoàn toàn do `LayoutManager` đảm nhiệm. Nếu không có `LayoutManager`, RecyclerView sẽ không biết phải vẽ các dòng theo chiều dọc, chiều ngang hay lưới nên màn hình sẽ trắng trơn.

#### Câu 6: `GridLayoutManager` khác với `StaggeredGridLayoutManager` ở điểm nào?
> **Trả lời:**
> - `GridLayoutManager`: Chia thành lưới các ô có kích thước đều nhau trên cùng một hàng. Chiều cao của hàng sẽ bị cố định theo ô có chiều cao lớn nhất trong hàng đó.
> - `StaggeredGridLayoutManager`: Lưới so le dạng thác nước (như Pinterest). Các ô trên cùng một cột có chiều cao tự do khác nhau, ô phía dưới sẽ tự động chèn vào khoảng trống sát đáy của ô phía trên mà không bị ép đều theo hàng ngang.

#### Câu 7: Kỹ thuật Multiple View Type dùng để giải quyết bài toán gì?
> **Trả lời:** Dùng khi trong cùng một danh sách cần hiển thị các dòng có cấu trúc giao diện và layout XML hoàn toàn khác nhau. Trong dự án, em dùng nó cho màn hình `TimelineActivity` để hiển thị dòng tiêu đề nhóm (`row_header.xml`), dòng nhiệm vụ (`row_timeline_task.xml`) và dòng cuộc hẹn (`row_timeline_meeting.xml`).

#### Câu 8: Trong Multiple View Type, vì sao danh sách dữ liệu truyền vào Adapter lại là `List<Object>`?
> **Trả lời:** Vì danh sách này chứa nhiều kiểu đối tượng khác nhau (vừa có `String` làm tiêu đề, vừa có `NhiemVu`, vừa có `CuocHen`). Do đó, kiểu generic `Object` (lớp cha của mọi lớp trong Java) là kiểu dữ liệu phù hợp nhất để gom chung các đối tượng này vào cùng một `ArrayList`.

#### Câu 9: Hàm `getItemViewType(int position)` đóng vai trò gì trong Multiple View Type?
> **Trả lời:** Là hàm quyết định kiểu dòng. RecyclerView gọi hàm này trước tiên để biết phần tử tại `position` là kiểu gì (trả về hằng số 0, 1 hoặc 2), từ đó hệ thống chuyển đúng mã kiểu này sang tham số `viewType` của `onCreateViewHolder()` để inflate đúng tệp layout XML tương ứng.

#### Câu 10: `notifyItemRemoved()` khác với `notifyDataSetChanged()` ở những điểm nào?
> **Trả lời:**
> - `notifyDataSetChanged()`: Làm mới toàn bộ danh sách, ép RecyclerView vẽ lại từ đầu, không xác định được vị trí nào vừa biến động và **hoàn toàn không có animation**.
> - `notifyItemRemoved(position)`: Chỉ định rõ dòng tại vị trí `position` bị xóa. RecyclerView giữ nguyên toàn bộ các View khác, kích hoạt hiệu ứng mờ dần biến mất của dòng bị xóa và tự động kéo các dòng phía dưới trượt lên rất mượt mà.

#### Câu 11: Vì sao bắt buộc phải xóa phần tử trong `ArrayList` trước rồi mới gọi `notifyItemRemoved()`?
> **Trả lời:** Vì khi gọi `notifyItemRemoved(position)`, RecyclerView sẽ kiểm tra lại kích thước dữ liệu thông qua hàm `getItemCount()`. Nếu ta chưa xóa khỏi `ArrayList`, kích thước dữ liệu cũ và mới bị lệch nhau, RecyclerView sẽ phát hiện bất đồng bộ dữ liệu và ném ngoại lệ `IndexOutOfBoundsException` làm sập ứng dụng.

#### Câu 12: `DefaultItemAnimator` xử lý hiệu ứng "thêm", "xóa", "sửa" như thế nào?
> **Trả lời:**
> - Thêm: Điều chỉnh thuộc tính Alpha mờ sang rõ từ $0 \rightarrow 1$.
> - Xóa: Điều chỉnh thuộc tính Alpha từ $1 \rightarrow 0$ rồi co chiều cao dòng lại.
> - Sửa/Đổi: Làm mờ nội dung cũ (Alpha $1 \rightarrow 0$) rồi làm rõ nội dung mới (Alpha $0 \rightarrow 1$).

#### Câu 13: Vì sao khi tick CheckBox hoàn thành nhiệm vụ, dòng chữ bị nháy sáng và làm sao để khắc phục?
> **Trả lời:** Khi gọi `notifyItemChanged()`, `DefaultItemAnimator` mặc định sẽ chạy hiệu ứng chuyển đổi Alpha từ $1 \rightarrow 0 \rightarrow 1$ nên mắt nhìn thấy như bị chớp nháy. Để khắc phục, ta ép kiểu sang `SimpleItemAnimator` và gọi `setSupportsChangeAnimations(false)` trong Activity.

#### Câu 14: `ItemDecoration` là gì và mang lại lợi ích gì so với vẽ trực tiếp vào layout dòng?
> **Trả lời:** `ItemDecoration` là lớp chuyên trách trang trí hoặc tạo khoảng đệm cho các ô trong RecyclerView mà không cần can thiệp vào mã nguồn của Adapter hay layout XML. Trong bài, em dùng `DividerItemDecoration` để tự động vẽ đường gạch ngang phân cách giữa các dòng nhiệm vụ một cách chuẩn mực.

#### Câu 15: Hàm `scrollToPosition(int position)` dùng để làm gì?
> **Trả lời:** Dùng để cuộn danh sách tức thì đến vị trí dòng chỉ định. Ví dụ khi người dùng thêm một nhiệm vụ mới vào cuối danh sách bằng hàm `addItem()`, ta gọi `rv.scrollToPosition(adapter.getItemCount() - 1)` để màn hình tự cuộn xuống dòng mới thêm, giúp người dùng nhìn thấy ngay kết quả.

#### Câu 16: Khi xoay ngang màn hình điện thoại, RecyclerView hoạt động ra sao nếu dùng GridLayoutManager?
> **Trả lời:** Khi xoay ngang, chiều rộng màn hình tăng lên. Nếu muốn tối ưu giao diện, ta có thể tự động tăng số cột `spanCount` (ví dụ từ 2 cột lên 3 hoặc 4 cột) để tận dụng không gian chiều ngang. Trong bài, em cấu hình thẻ `card_do_an.xml` với lề co giãn linh hoạt nên dù ở chế độ 2 cột dọc hay ngang thì các thẻ đồ án đều hiển thị vừa vặn, không bị tràn hay méo hình.

#### Câu 17: Làm thế nào để tự tạo một hiệu ứng hoạt ảnh riêng cho RecyclerView nếu không muốn dùng `DefaultItemAnimator`?
> **Trả lời:** Ta tạo một lớp Java mới kế thừa từ `SimpleItemAnimator` (hoặc `ItemAnimator`), sau đó ghi đè (override) 4 phương thức xử lý hoạt ảnh: `animateAdd()`, `animateRemove()`, `animateMove()` và `animateChange()`. Cuối cùng, gán hoạt ảnh tùy biến đó vào danh sách bằng lệnh `recyclerView.setItemAnimator(new MyCustomItemAnimator())`.

---

## 9. HẠN CHẾ ĐÃ BIẾT & PHẦN CHỈ GIẢI THÍCH LÝ THUYẾT

Theo đúng nguyên tắc giữ ứng dụng ổn định, thực tế và dễ bảo vệ trước hội đồng, các nội dung sau **chỉ được giải thích lý thuyết chi tiết trong tài liệu này mà không cài đặt trực tiếp vào ứng dụng**:
1. **`StaggeredGridLayoutManager` (Lưới so le)**:
   - *Lý do*: Mô hình này chỉ phù hợp với các ứng dụng trưng bày tranh ảnh có chiều cao bất quy tắc (như Pinterest, Unsplash). Với ứng dụng quản lý tiến độ đồ án tốt nghiệp, các thẻ thông tin cần có độ đồng đều, ngay ngắn để sinh viên và giảng viên dễ theo dõi số liệu. Việc dùng Staggered Grid sẽ làm giao diện lộn xộn, mất thẩm mỹ và không đúng nghiệp vụ.
2. **Kế thừa và viết riêng lớp con `SimpleItemAnimator` tùy biến**:
   - *Lý do*: Hoạt ảnh mặc định `DefaultItemAnimator` của Android đã quá đủ mượt mà, chuyên nghiệp và ổn định. Việc tự viết lớp con kế thừa `SimpleItemAnimator` đòi hỏi can thiệp sâu vào ma trận render đồ họa và `ViewPropertyAnimator`, làm mã nguồn cồng kềnh, dễ phát sinh lỗi rò rỉ bộ nhớ (Memory Leak) mà không đem lại giá trị thực tế cho bài tập lớn.
3. **Viết riêng lớp `ItemDecoration` tự vẽ Canvas tùy biến**:
   - *Lý do*: Lớp có sẵn `DividerItemDecoration` của Android Jetpack đã giải quyết hoàn hảo bài toán vẽ vạch ngăn cách danh sách chỉ với 2 dòng lệnh gọn gàng.
