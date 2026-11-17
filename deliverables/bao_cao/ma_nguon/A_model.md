# TÀI LIỆU GIẢI THÍCH CHI TIẾT MÃ NGUỒN – NHÓM A: MODEL
**Thư mục:** `app/src/main/java/com/example/app/model/`

---

## TỔNG QUAN NHÓM MODEL
Tầng Model đại diện cho các thực thể dữ liệu (Entities / Data Transfer Objects - DTO) trong kiến trúc ứng dụng. Các lớp này định nghĩa cấu trúc dữ liệu ánh xạ trực tiếp từ các bảng trong cơ sở dữ liệu SQLite (`DoAn`, `NhiemVu`, `CuocHen`), đồng thời cung cấp các đối tượng phụ trợ (`TienDoInfo`, `DoAnWithTienDo`) phục vụ việc hiển thị dữ liệu tổng hợp trên giao diện và so sánh vi sai danh sách trong `DiffUtil`. Tất cả các lớp Model đều thực thi giao diện `java.io.Serializable` để có thể truyền qua lại giữa các thành phần Android (Activity, Fragment, Bundle, Intent) một cách an toàn.

---

## 1. DoAn.java (`app/src/main/java/com/example/app/model/DoAn.java`)

### 1.1. Vai trò
`DoAn` là lớp mô hình thực thể (POJO) biểu diễn một đề tài/đồ án học tập. Lớp này ánh xạ 1-1 với cấu trúc bảng `DoAn` trong CSDL SQLite `tiendo.db`, đóng vai trò là đơn vị lưu trữ dữ liệu chính xuyên suốt từ tầng CSDL (`DoAnDao`), tầng hiển thị (`DoAnAdapter`, `MainActivity`), đến các màn hình chi tiết và hộp thoại nhập liệu (`DoAnDetailActivity`, `AddEditDoAnDialogFragment`).

### 1.2. Mối quan hệ
* **Các lớp gọi `DoAn`:**
  * `DoAnDao`: Khởi tạo và đọc dữ liệu từ `Cursor` thành đối tượng `DoAn`, hoặc lấy dữ liệu từ `DoAn` đưa vào `ContentValues` để chèn/sửa.
  * `DoAnWithTienDo`: Chứa một thuộc tính tham chiếu kiểu `DoAn`.
  * `DoAnAdapter`: Hiển thị thông tin tên đồ án, GVHD, ngày tháng của `DoAn` lên từng item card.
  * `MainActivity`, `DoAnDetailActivity`, `AddEditDoAnDialogFragment`: Truyền đối tượng `DoAn` qua Intent/Bundle (`Serializable`).
* **Lớp mà `DoAn` gọi:** Chỉ sử dụng các kiểu dữ liệu cơ bản của Java (`int`, `String`) và `java.io.Serializable`.

### 1.3. Thuộc tính quan trọng
* `id` (`int`): Khóa chính của đồ án trong CSDL (tương ứng cột `id` trong bảng `DoAn`). Giá trị = 0 hoặc chưa gán khi là đồ án mới tạo chưa chèn vào DB; giá trị > 0 khi đã được lưu trong CSDL SQLite.
* `ten` (`String`): Tên đồ án / đề tài nghiên cứu (bắt buộc, không được để trống).
* `moTa` (`String`): Tóm tắt nội dung, mục tiêu của đồ án (có thể null).
* `giangVien` (`String`): Họ và tên, học hàm/học vị của giảng viên hướng dẫn (có thể null).
* `ngayBatDau` (`String`): Ngày bắt đầu làm đồ án theo định dạng chuẩn ISO-8601 `yyyy-MM-dd` (có thể null).
* `hanNop` (`String`): Hạn chót phải nộp đồ án theo định dạng `yyyy-MM-dd` (có thể null).

### 1.4. Bảng giải thích chi tiết từng hàm trong `DoAn.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `DoAn()` | Không có $\rightarrow$ `void` | Constructor mặc định không tham số | Khởi tạo đối tượng rỗng với các trường mang giá trị mặc định (`id = 0`, các chuỗi = `null`). Cần thiết cho việc khởi tạo từng bước khi đọc Cursor hoặc serialization. | `DoAnDao.cursorToDoAn()` |
| 2 | `DoAn(int, String, String, String, String, String)` | `int id, String ten, String moTa, String giangVien, String ngayBatDau, String hanNop` $\rightarrow$ `void` | Constructor đầy đủ thuộc tính | Gán toàn bộ 6 tham số truyền vào cho các trường tương ứng của đối tượng. Dùng khi lấy bản ghi đã có sẵn trong CSDL kèm ID. | `LogicUnitTest`, các bài kiểm thử |
| 3 | `DoAn(String, String, String, String, String)` | `String ten, String moTa, String giangVien, String ngayBatDau, String hanNop` $\rightarrow$ `void` | Constructor không có `id` | Gán 5 thuộc tính dữ liệu, trường `id` mặc định là 0. Dùng khi người dùng nhập dữ liệu tạo đồ án mới trên giao diện chưa lưu vào DB. | `AddEditDoAnDialogFragment`, `DatabaseTest` |
| 4 | `getId()` | Không có $\rightarrow$ `int` | Lấy mã ID của đồ án | Trả về giá trị của trường `this.id`. | `DoAnDao`, `MainActivity`, `DoAnAdapter`, `DoAnDetailActivity` |
| 5 | `setId(int)` | `int id` $\rightarrow$ `void` | Thiết lập mã ID cho đồ án | Gán giá trị tham số `id` vào trường `this.id`. | `DoAnDao.insert()`, `DoAnDao.cursorToDoAn()` |
| 6 | `getTen()` | Không có $\rightarrow$ `String` | Lấy tên đồ án | Trả về chuỗi `this.ten`. | `DoAnAdapter`, `DoAnDetailActivity`, `DoAnDao`, `MainActivity` |
| 7 | `setTen(String)` | `String ten` $\rightarrow$ `void` | Thiết lập tên đồ án | Gán tham số vào `this.ten`. | `AddEditDoAnDialogFragment`, `DoAnDao` |
| 8 | `getMoTa()` | Không có $\rightarrow$ `String` | Lấy mô tả đồ án | Trả về chuỗi `this.moTa`. | `DoAnDao`, `DoAnDetailActivity`, `AddEditDoAnDialogFragment` |
| 9 | `setMoTa(String)` | `String moTa` $\rightarrow$ `void` | Thiết lập mô tả đồ án | Gán tham số vào `this.moTa`. | `AddEditDoAnDialogFragment`, `DoAnDao` |
| 10 | `getGiangVien()` | Không có $\rightarrow$ `String` | Lấy tên GVHD | Trả về chuỗi `this.giangVien`. | `DoAnAdapter`, `DoAnDetailActivity`, `DoAnDao` |
| 11 | `setGiangVien(String)` | `String giangVien` $\rightarrow$ `void` | Thiết lập tên GVHD | Gán tham số vào `this.giangVien`. | `AddEditDoAnDialogFragment`, `DoAnDao` |
| 12 | `getNgayBatDau()` | Không có $\rightarrow$ `String` | Lấy ngày bắt đầu | Trả về chuỗi `this.ngayBatDau`. | `DoAnAdapter`, `DoAnDetailActivity`, `DoAnDao` |
| 13 | `setNgayBatDau(String)` | `String ngayBatDau` $\rightarrow$ `void` | Thiết lập ngày bắt đầu | Gán tham số vào `this.ngayBatDau`. | `AddEditDoAnDialogFragment`, `DoAnDao` |
| 14 | `getHanNop()` | Không có $\rightarrow$ `String` | Lấy hạn nộp đồ án | Trả về chuỗi `this.hanNop`. | `DoAnAdapter`, `DoAnDetailActivity`, `DoAnDao` |
| 15 | `setHanNop(String)` | `String hanNop` $\rightarrow$ `void` | Thiết lập hạn nộp đồ án | Gán tham số vào `this.hanNop`. | `AddEditDoAnDialogFragment`, `DoAnDao` |

### 1.5. Điểm kỹ thuật đáng chú ý
* **Thực thi `Serializable`:** Cho phép đóng gói đối tượng vào `Bundle` (`intent.putExtra(...)` hoặc `bundle.putSerializable(...)`) để truyền từ `MainActivity` sang `DoAnDetailActivity` và `AddEditDoAnDialogFragment` mà không cần cấu hình Parcelable thủ công phức tạp.
* **Lưu ngày dạng String ISO-8601:** Trường `ngayBatDau` và `hanNop` được lưu kiểu `String` thay vì `Date`, đồng bộ hoàn toàn với kiểu `TEXT` trong SQLite, giúp tránh các lỗi serialize ngày tháng khi chuyển đổi qua lại giữa các phiên bản JVM.

---

## 2. NhiemVu.java (`app/src/main/java/com/example/app/model/NhiemVu.java`)

### 2.1. Vai trò
`NhiemVu` là lớp mô hình đại diện cho một đầu mục công việc cụ thể trực thuộc một đồ án. Lớp này ánh xạ bảng `NhiemVu` trong CSDL, phục vụ chức năng quản lý danh sách việc cần làm, theo dõi hạn chót của từng việc và trạng thái hoàn thành trực tiếp trên giao diện tab Nhiệm vụ.

### 2.2. Mối quan hệ
* **Các lớp gọi `NhiemVu`:**
  * `NhiemVuDao`: Truy vấn, chèn, sửa, xóa và cập nhật trạng thái `da_hoan_thanh`.
  * `NhiemVuAdapter`: Hiển thị từng nhiệm vụ lên `RecyclerView`, bắt sự kiện tick CheckBox hoàn thành.
  * `NhiemVuFragment`, `AddEditNhiemVuDialogFragment`: Xử lý logic hiển thị và cập nhật đầu việc.
* **Lớp mà `NhiemVu` gọi:** `java.util.Objects` (dùng trong `equals` và `hashCode`), `java.io.Serializable`.

### 2.3. Thuộc tính quan trọng
* `id` (`int`): Khóa chính tự tăng của nhiệm vụ.
* `doAnId` (`int`): Khóa ngoại tham chiếu đến `DoAn(id)`.
* `ten` (`String`): Tên đầu việc cần làm (bắt buộc).
* `ghiChu` (`String`): Ghi chú, chỉ dẫn chi tiết (có thể null).
* `hanHoanThanh` (`String`): Hạn chót hoàn thành nhiệm vụ theo định dạng `yyyy-MM-dd` (có thể null).
* `daHoanThanh` (`int`): Cờ trạng thái lưu trong SQLite; `0` là chưa hoàn thành, `1` là đã hoàn thành (dùng kiểu số nguyên vì SQLite không có kiểu Boolean riêng biệt).

### 2.4. Bảng giải thích chi tiết từng hàm trong `NhiemVu.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `NhiemVu()` | Không có $\rightarrow$ `void` | Constructor mặc định | Khởi tạo đối tượng rỗng. | `NhiemVuDao.cursorToNhiemVu()` |
| 2 | `NhiemVu(int, int, String, String, String, int)` | `int id, int doAnId, String ten, String ghiChu, String hanHoanThanh, int daHoanThanh` $\rightarrow$ `void` | Constructor đầy đủ tham số | Gán cả 6 trường dữ liệu của nhiệm vụ. Dùng khi lấy dữ liệu đầy đủ từ DB. | Các bài kiểm thử |
| 3 | `NhiemVu(int, String, String, String, int)` | `int doAnId, String ten, String ghiChu, String hanHoanThanh, int daHoanThanh` $\rightarrow$ `void` | Constructor không có `id` | Khởi tạo nhiệm vụ mới chuẩn bị chèn vào DB (`id = 0`). | `AddEditNhiemVuDialogFragment`, `DatabaseTest` |
| 4 | `getId()` | Không có $\rightarrow$ `int` | Lấy mã nhiệm vụ | Trả về `this.id`. | `NhiemVuDao`, `NhiemVuAdapter`, `NhiemVuFragment` |
| 5 | `setId(int)` | `int id` $\rightarrow$ `void` | Gán mã nhiệm vụ | Gán tham số vào `this.id`. | `NhiemVuDao.insert()`, `NhiemVuDao.cursorToNhiemVu()` |
| 6 | `getDoAnId()` | Không có $\rightarrow$ `int` | Lấy mã đồ án chứa nhiệm vụ này | Trả về `this.doAnId`. | `NhiemVuDao`, `AddEditNhiemVuDialogFragment` |
| 7 | `setDoAnId(int)` | `int doAnId` $\rightarrow$ `void` | Gán mã đồ án cho nhiệm vụ | Gán tham số vào `this.doAnId`. | `NhiemVuDao.cursorToNhiemVu()` |
| 8 | `getTen()` | Không có $\rightarrow$ `String` | Lấy tên nhiệm vụ | Trả về `this.ten`. | `NhiemVuAdapter`, `NhiemVuDao`, `AddEditNhiemVuDialogFragment` |
| 9 | `setTen(String)` | `String ten` $\rightarrow$ `void` | Thiết lập tên nhiệm vụ | Gán tham số vào `this.ten`. | `AddEditNhiemVuDialogFragment`, `NhiemVuDao` |
| 10 | `getGhiChu()` | Không có $\rightarrow$ `String` | Lấy ghi chú của nhiệm vụ | Trả về `this.ghiChu`. | `NhiemVuAdapter`, `NhiemVuDao` |
| 11 | `setGhiChu(String)` | `String ghiChu` $\rightarrow$ `void` | Thiết lập ghi chú nhiệm vụ | Gán tham số vào `this.ghiChu`. | `AddEditNhiemVuDialogFragment`, `NhiemVuDao` |
| 12 | `getHanHoanThanh()` | Không có $\rightarrow$ `String` | Lấy ngày hạn hoàn thành | Trả về `this.hanHoanThanh`. | `NhiemVuAdapter`, `NhiemVuDao` |
| 13 | `setHanHoanThanh(String)` | `String hanHoanThanh` $\rightarrow$ `void` | Thiết lập hạn hoàn thành | Gán tham số vào `this.hanHoanThanh`. | `AddEditNhiemVuDialogFragment`, `NhiemVuDao` |
| 14 | `getDaHoanThanh()` | Không có $\rightarrow$ `int` | Lấy giá trị số của trạng thái (0/1) | Trả về `this.daHoanThanh` (0 hoặc 1). | `NhiemVuDao.insert()`, `NhiemVuDao.update()` |
| 15 | `setDaHoanThanh(int)` | `int daHoanThanh` $\rightarrow$ `void` | Gán trạng thái số | Gán tham số vào `this.daHoanThanh`. | `NhiemVuDao.cursorToNhiemVu()` |
| 16 | `isCompleted()` | Không có $\rightarrow$ `boolean` | Kiểm tra việc đã xong chưa | Trả về `true` nếu `daHoanThanh == 1`, ngược lại trả về `false`. | `NhiemVuAdapter.onBindViewHolder()` để đặt trạng thái `isChecked` cho CheckBox |
| 17 | `setCompleted(boolean)` | `boolean completed` $\rightarrow$ `void` | Đặt trạng thái theo boolean | Nếu `completed == true` gán `daHoanThanh = 1`, ngược lại gán `daHoanThanh = 0`. | Tầng UI và Adapter khi nhận sự kiện click CheckBox |
| 18 | `equals(Object)` | `Object o` $\rightarrow$ `boolean` | So sánh bằng nhau giữa hai đối tượng `NhiemVu` | So sánh lần lượt: tham chiếu địa chỉ ô nhớ $\rightarrow$ kiểu lớp $\rightarrow$ so sánh các trường nguyên thủy (`id`, `doAnId`, `daHoanThanh`) $\rightarrow$ so sánh các trường chuỗi bằng `Objects.equals`. | `NhiemVuAdapter.DIFF_CALLBACK.areContentsTheSame()` |
| 19 | `hashCode()` | Không có $\rightarrow$ `int` | Tạo mã băm cho đối tượng | Gọi `Objects.hash(...)` băm toàn bộ 6 trường dữ liệu của nhiệm vụ. | Các cấu trúc bảng băm (HashMap, HashSet) |

### 2.5. Điểm kỹ thuật đáng chú ý
* **Hàm tiện ích `isCompleted()` và `setCompleted(boolean)`:** Giúp che giấu chi tiết lưu trữ kiểu `INTEGER (0/1)` của SQLite, cung cấp giao diện kiểu `boolean` tự nhiên cho tầng View của Android (phù hợp với `CompoundButton.isChecked()`).
* **Cài đặt đầy đủ `equals` và `hashCode`:** Cực kỳ quan trọng đối với `ListAdapter` và `DiffUtil`. Khi trạng thái hoàn thành hoặc tiêu đề nhiệm vụ thay đổi, `DiffUtil` sẽ gọi `areContentsTheSame()`, hàm này dùng `equals()` để xác định xem ItemView đó có cần được vẽ lại hay không.

---

## 3. CuocHen.java (`app/src/main/java/com/example/app/model/CuocHen.java`)

### 3.1. Vai trò
`CuocHen` là lớp mô hình đại diện cho một buổi gặp mặt, trao đổi định kỳ hoặc báo cáo tiến độ với Giảng viên hướng dẫn. Lớp này ánh xạ bảng `CuocHen` trong CSDL, phục vụ việc quản lý thời gian, địa điểm gặp và nội dung chuẩn bị.

### 3.2. Mối quan hệ
* **Các lớp gọi `CuocHen`:**
  * `CuocHenDao`: Chèn, sửa, xóa, lấy danh sách cuộc hẹn sắp xếp theo thời gian tăng dần (`ORDER BY thoi_gian ASC`).
  * `CuocHenAdapter`: Hiển thị danh sách cuộc hẹn lên giao diện, gọi `DateUtils.isPastDateTime(cuocHen.getThoiGian())` để quyết định gắn nhãn "Sắp diễn ra" (màu xanh) hay "Đã qua" (màu xám).
  * `CuocHenFragment`, `AddEditCuocHenDialogFragment`: Quản lý hiển thị và thao tác thêm/sửa cuộc hẹn.
* **Lớp mà `CuocHen` gọi:** `java.util.Objects`, `java.io.Serializable`.

### 3.3. Thuộc tính quan trọng
* `id` (`int`): Khóa chính tự tăng của cuộc hẹn.
* `doAnId` (`int`): Khóa ngoại tham chiếu đến `DoAn(id)`.
* `tieuDe` (`String`): Tiêu đề, chủ đề cuộc hẹn (bắt buộc).
* `thoiGian` (`String`): Thời điểm hẹn đầy đủ cả ngày và giờ theo định dạng chuẩn `yyyy-MM-dd HH:mm` (bắt buộc).
* `diaDiem` (`String`): Địa điểm gặp (phòng học, văn phòng bộ môn hoặc link Google Meet; có thể null).
* `ghiChu` (`String`): Nội dung cần trao đổi hoặc lưu ý dặn dò của giảng viên (có thể null).

### 3.4. Bảng giải thích chi tiết từng hàm trong `CuocHen.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `CuocHen()` | Không có $\rightarrow$ `void` | Constructor mặc định | Khởi tạo đối tượng rỗng. | `CuocHenDao.cursorToCuocHen()` |
| 2 | `CuocHen(int, int, String, String, String, String)` | `int id, int doAnId, String tieuDe, String thoiGian, String diaDiem, String ghiChu` $\rightarrow$ `void` | Constructor đầy đủ thuộc tính | Gán toàn bộ 6 thuộc tính. Dùng khi đọc dữ liệu đầy đủ từ CSDL. | `DatabaseTest` |
| 3 | `CuocHen(int, String, String, String, String)` | `int doAnId, String tieuDe, String thoiGian, String diaDiem, String ghiChu` $\rightarrow$ `void` | Constructor không có `id` | Khởi tạo cuộc hẹn mới chuẩn bị chèn vào CSDL (`id = 0`). | `AddEditCuocHenDialogFragment`, `DatabaseTest` |
| 4 | `getId()` | Không có $\rightarrow$ `int` | Lấy mã cuộc hẹn | Trả về `this.id`. | `CuocHenDao`, `CuocHenAdapter`, `CuocHenFragment` |
| 5 | `setId(int)` | `int id` $\rightarrow$ `void` | Gán mã cuộc hẹn | Gán tham số vào `this.id`. | `CuocHenDao.insert()`, `CuocHenDao.cursorToCuocHen()` |
| 6 | `getDoAnId()` | Không có $\rightarrow$ `int` | Lấy mã đồ án | Trả về `this.doAnId`. | `CuocHenDao`, `AddEditCuocHenDialogFragment` |
| 7 | `setDoAnId(int)` | `int doAnId` $\rightarrow$ `void` | Gán mã đồ án | Gán tham số vào `this.doAnId`. | `CuocHenDao.cursorToCuocHen()` |
| 8 | `getTieuDe()` | Không có $\rightarrow$ `String` | Lấy tiêu đề cuộc hẹn | Trả về `this.tieuDe`. | `CuocHenAdapter`, `CuocHenDao`, `AddEditCuocHenDialogFragment` |
| 9 | `setTieuDe(String)` | `String tieuDe` $\rightarrow$ `void` | Gán tiêu đề cuộc hẹn | Gán tham số vào `this.tieuDe`. | `AddEditCuocHenDialogFragment`, `CuocHenDao` |
| 10 | `getThoiGian()` | Không có $\rightarrow$ `String` | Lấy chuỗi thời gian hẹn | Trả về `this.thoiGian` (chuỗi dạng `yyyy-MM-dd HH:mm`). | `CuocHenAdapter`, `CuocHenDao`, `AddEditCuocHenDialogFragment` |
| 11 | `setThoiGian(String)` | `String thoiGian` $\rightarrow$ `void` | Gán thời gian hẹn | Gán tham số vào `this.thoiGian`. | `AddEditCuocHenDialogFragment`, `CuocHenDao` |
| 12 | `getDiaDiem()` | Không có $\rightarrow$ `String` | Lấy địa điểm hẹn | Trả về `this.diaDiem`. | `CuocHenAdapter`, `CuocHenDao` |
| 13 | `setDiaDiem(String)` | `String diaDiem` $\rightarrow$ `void` | Gán địa điểm hẹn | Gán tham số vào `this.diaDiem`. | `AddEditCuocHenDialogFragment`, `CuocHenDao` |
| 14 | `getGhiChu()` | Không có $\rightarrow$ `String` | Lấy ghi chú cuộc hẹn | Trả về `this.ghiChu`. | `CuocHenAdapter`, `CuocHenDao` |
| 15 | `setGhiChu(String)` | `String ghiChu` $\rightarrow$ `void` | Gán ghi chú cuộc hẹn | Gán tham số vào `this.ghiChu`. | `AddEditCuocHenDialogFragment`, `CuocHenDao` |
| 16 | `equals(Object)` | `Object o` $\rightarrow$ `boolean` | So sánh bằng nhau giữa hai đối tượng `CuocHen` | So sánh địa chỉ $\rightarrow$ so sánh kiểu lớp $\rightarrow$ so sánh các trường nguyên thủy (`id`, `doAnId`) $\rightarrow$ so sánh các trường chuỗi bằng `Objects.equals`. | `CuocHenAdapter.DIFF_CALLBACK.areContentsTheSame()` |
| 17 | `hashCode()` | Không có $\rightarrow$ `int` | Tạo mã băm cho cuộc hẹn | Gọi `Objects.hash(id, doAnId, tieuDe, thoiGian, diaDiem, ghiChu)`. | Cấu trúc dữ liệu dạng băm |

### 3.5. Điểm kỹ thuật đáng chú ý
* **Định dạng thời gian cố định `yyyy-MM-dd HH:mm`:** Giúp việc sắp xếp theo thứ tự thời gian trong SQL (`ORDER BY thoi_gian ASC`) và việc so sánh với thời điểm hiện tại trong `DateUtils.isPastDateTime()` luôn chính xác mà không bị ảnh hưởng bởi định dạng ngôn ngữ máy tính.
* **Cài đặt `equals`:** Đảm bảo `DiffUtil` trong `CuocHenAdapter` nhận biết được sự thay đổi khi người dùng chỉnh sửa tiêu đề, giờ giấc hoặc địa điểm hẹn để kích hoạt animation cập nhật card view mượt mà.

---

## 4. TienDoInfo.java (`app/src/main/java/com/example/app/model/TienDoInfo.java`)

### 4.1. Vai trò
`TienDoInfo` là lớp giá trị (Value Object / Helper DTO) chuyên trách việc đóng gói và tính toán tiến độ hoàn thành của một đồ án. Lớp này không ánh xạ vào bảng nào trong CSDL mà là kết quả của các hàm tổng hợp SQL (`COUNT`, `SUM`), giúp tách biệt hoàn toàn logic tính toán toán học phần trăm (%) ra khỏi tầng hiển thị giao diện.

### 4.2. Mối quan hệ
* **Các lớp gọi `TienDoInfo`:**
  * `DoAnDao.getTienDo(int doAnId)` và `DoAnDao.getAllWithTienDo()`: Khởi tạo đối tượng này sau khi thực hiện truy vấn tổng hợp SQL.
  * `DoAnWithTienDo`: Chứa một thuộc tính kiểu `TienDoInfo`.
  * `DoAnAdapter`, `DoAnDetailActivity`: Gọi `getPercent()` để đặt giá trị cho `ProgressBar` và gọi `getFormattedText()` để gán nhãn chuỗi hiển thị.
  * `LogicUnitTest`: Kiểm thử đơn vị các trường hợp tính toán phần trăm.
* **Lớp mà `TienDoInfo` gọi:** `java.util.Locale`, `java.io.Serializable`.

### 4.3. Thuộc tính quan trọng
* `completedTasks` (`int`): Tổng số lượng nhiệm vụ đã hoàn thành (`da_hoan_thanh = 1`) của đồ án.
* `totalTasks` (`int`): Tổng số lượng nhiệm vụ hiện có (`da_hoan_thanh = 0` hoặc `1`) của đồ án.

### 4.4. Bảng giải thích chi tiết từng hàm trong `TienDoInfo.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `TienDoInfo()` | Không có $\rightarrow$ `void` | Constructor mặc định | Khởi tạo đối tượng với `completedTasks = 0` và `totalTasks = 0`. | Khi tạo dữ liệu rỗng |
| 2 | `TienDoInfo(int, int)` | `int completedTasks, int totalTasks` $\rightarrow$ `void` | Constructor khởi tạo số liệu | Gán trực tiếp 2 giá trị số việc hoàn thành và tổng số việc. | `DoAnDao.getAllWithTienDo()`, `DoAnDao.getTienDo()`, `LogicUnitTest` |
| 3 | `getCompletedTasks()` | Không có $\rightarrow$ `int` | Lấy số việc đã xong | Trả về `this.completedTasks`. | `DoAnWithTienDo.equals()`, `LogicUnitTest` |
| 4 | `setCompletedTasks(int)` | `int completedTasks` $\rightarrow$ `void` | Gán số việc đã xong | Gán tham số vào `this.completedTasks`. | Tầng logic khi cập nhật |
| 5 | `getTotalTasks()` | Không có $\rightarrow$ `int` | Lấy tổng số việc | Trả về `this.totalTasks`. | `DoAnWithTienDo.equals()`, `LogicUnitTest` |
| 6 | `setTotalTasks(int)` | `int totalTasks` $\rightarrow$ `void` | Gán tổng số việc | Gán tham số vào `this.totalTasks`. | Tầng logic khi cập nhật |
| 7 | `getPercent()` | Không có $\rightarrow$ `int` | Tính toán tỷ lệ phần trăm hoàn thành | **Bước 1:** Kiểm tra nếu `totalTasks <= 0`, lập tức trả về `0` (tránh lỗi chia cho 0).<br>**Bước 2:** Tính toán bằng công thức số nguyên: `(completedTasks * 100) / totalTasks` và trả về kết quả. | `DoAnAdapter`, `DoAnDetailActivity`, `getFormattedText()`, `LogicUnitTest` |
| 8 | `getFormattedText()` | Không có $\rightarrow$ `String` | Định dạng chuỗi hiển thị tiến độ | Gọi `String.format(Locale.getDefault(), "%d/%d (%d%%)", completedTasks, totalTasks, getPercent())` để trả về chuỗi trực quan dạng `"2/5 (40%)"` hoặc `"0/0 (0%)"`. | `DoAnAdapter`, `DoAnDetailActivity`, `LogicUnitTest` |

### 4.5. Điểm kỹ thuật đáng chú ý
* **Xử lý triệt để ngoại lệ chia cho 0 (`ArithmeticException: / by zero`):** Ở dòng 37–39, hàm `getPercent()` kiểm tra `if (totalTasks <= 0) return 0;`. Khi một đồ án vừa được tạo và chưa có nhiệm vụ nào (`0/0`), ứng dụng sẽ hiển thị `0%` một cách an toàn mà không bị crash ứng dụng.
* **Nhất quán hiển thị với `Locale.getDefault()`:** Hàm `getFormattedText()` chuẩn hóa chuỗi đầu ra thống nhất giữa màn hình danh sách chính và màn hình chi tiết đồ án.

---

## 5. DoAnWithTienDo.java (`app/src/main/java/com/example/app/model/DoAnWithTienDo.java`)

### 5.1. Vai trò
`DoAnWithTienDo` là lớp mô hình kết hợp (Composite Pattern / Composite DTO). Lớp này nhóm một đối tượng đồ án `DoAn` với thông tin tiến độ tương ứng `TienDoInfo` của nó thành một thể thống nhất. Lớp này là đơn vị dữ liệu chính mà `DoAnAdapter` tiếp nhận để hiển thị lên từng card đồ án trong màn hình chính `MainActivity`.

### 5.2. Mối quan hệ
* **Các lớp gọi `DoAnWithTienDo`:**
  * `DoAnDao.getAllWithTienDo()`: Truy vấn dữ liệu kết hợp từ SQLite và đóng gói vào danh sách `List<DoAnWithTienDo>`.
  * `DoAnAdapter`: Sử dụng làm kiểu dữ liệu generic cho `ListAdapter<DoAnWithTienDo, ...>`, sử dụng trong `DiffUtil.ItemCallback<DoAnWithTienDo>`.
  * `MainActivity`: Nhận danh sách `List<DoAnWithTienDo>` từ DAO và gửi vào Adapter qua `adapter.submitList(list)`.
  * `LogicUnitTest`: Kiểm tra tính tương đương và hàm so sánh của DTO.
* **Lớp mà `DoAnWithTienDo` gọi:** `DoAn`, `TienDoInfo`, `java.util.Objects`, `java.io.Serializable`.

### 5.3. Thuộc tính quan trọng
* `doAn` (`DoAn`): Đối tượng chứa toàn bộ thông tin cơ bản của đồ án (id, tên, giảng viên, thời gian).
* `tienDo` (`TienDoInfo`): Đối tượng chứa số liệu tính toán tiến độ tương ứng của đồ án đó.

### 5.4. Bảng giải thích chi tiết từng hàm trong `DoAnWithTienDo.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `DoAnWithTienDo()` | Không có $\rightarrow$ `void` | Constructor mặc định | Khởi tạo đối tượng rỗng (`doAn = null`, `tienDo = null`). | Khởi tạo từng bước |
| 2 | `DoAnWithTienDo(DoAn, TienDoInfo)` | `DoAn doAn, TienDoInfo tienDo` $\rightarrow$ `void` | Constructor đầy đủ thuộc tính | Gán đối tượng đồ án và đối tượng tiến độ vào 2 trường tương ứng. | `DoAnDao.getAllWithTienDo()`, `LogicUnitTest` |
| 3 | `getDoAn()` | Không có $\rightarrow$ `DoAn` | Lấy đối tượng đồ án | Trả về `this.doAn`. | `DoAnAdapter`, `MainActivity` (xử lý sự kiện click, sửa, xóa) |
| 4 | `setDoAn(DoAn)` | `DoAn doAn` $\rightarrow$ `void` | Gán đối tượng đồ án | Gán tham số vào `this.doAn`. | Tầng dữ liệu |
| 5 | `getTienDo()` | Không có $\rightarrow$ `TienDoInfo` | Lấy đối tượng tiến độ | Trả về `this.tienDo`. | `DoAnAdapter` (hiển thị ProgressBar và phần trăm) |
| 6 | `setTienDo(TienDoInfo)` | `TienDoInfo tienDo` $\rightarrow$ `void` | Gán đối tượng tiến độ | Gán tham số vào `this.tienDo`. | Tầng dữ liệu |
| 7 | `equals(Object)` | `Object o` $\rightarrow$ `boolean` | So sánh tính tương đương sâu (Deep equality) giữa 2 đối tượng | Xem giải thích chi tiết 3 bước ở mục 5.5 bên dưới. | `DoAnAdapter.DIFF_CALLBACK.areContentsTheSame()`, `LogicUnitTest` |
| 8 | `hashCode()` | Không có $\rightarrow$ `int` | Tạo mã băm cho đối tượng | Gọi `Objects.hash(doAn != null ? doAn.getId() : 0)`. | Cấu trúc dữ liệu băm |

### 5.5. Phân tích chi tiết thuật toán hàm `equals(Object o)` (dòng 35–55)
Hàm `equals` trong `DoAnWithTienDo` là mắt xích cốt lõi giúp `DiffUtil` nhận diện chính xác xem một card đồ án có cần vẽ lại hay không khi người dùng cập nhật thông tin hoặc hoàn thành một nhiệm vụ:
* **Bước 1 (dòng 36–37):** Kiểm tra tham chiếu cùng ô nhớ (`this == o` $\rightarrow$ trả về `true`), kiểm tra null hoặc khác kiểu lớp (`o == null || getClass() != o.getClass()` $\rightarrow$ trả về `false`).
* **Bước 2 - So sánh thông tin đồ án (dòng 39–48):** Nếu cả 2 đối tượng `doAn` đều khác null, so sánh từng trường: `id`, `ten`, `giangVien`, `ngayBatDau`, `hanNop`, `moTa`. Nếu có bất kỳ trường nào khác nhau, trả về `false`.
* **Bước 3 - So sánh tiến độ (dòng 50–54):** Nếu cả 2 đối tượng `tienDo` đều khác null, so sánh `completedTasks` và `totalTasks`. Chỉ khi cả số việc xong và tổng số việc bằng nhau mới tiếp tục.
* **Kết quả:** Trả về `true` khi và chỉ khi mọi thông tin hiển thị của cả Đồ án lẫn Tiến độ hoàn toàn trùng khớp. Nhờ vậy, ngay khi người dùng tick vào một CheckBox trong tab Nhiệm vụ và quay ra màn hình chính, `DiffUtil` phát hiện ngay `completedTasks` đã đổi và chỉ vẽ lại duy nhất card đồ án đó kèm hiệu ứng mượt mà.
