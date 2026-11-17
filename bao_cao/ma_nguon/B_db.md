# TÀI LIỆU GIẢI THÍCH CHI TIẾT MÃ NGUỒN – NHÓM B: DATABASE & DAO
**Thư mục:** `app/src/main/java/com/example/app/db/`

---

## TỔNG QUAN NHÓM DATABASE & DAO
Tầng CSDL và Truy xuất Dữ liệu (Database Access Objects - DAO) chịu trách nhiệm toàn bộ về việc lưu trữ, truy vấn, biến đổi và duy trì tính toàn vẹn dữ liệu cho ứng dụng.
* `DatabaseHelper`: Kế thừa `SQLiteOpenHelper`, quản lý vòng đời tệp CSDL nhúng `tiendo.db`, cấu hình ràng buộc khóa ngoại (Foreign Keys) và khởi tạo bảng.
* `DoAnDao`, `NhiemVuDao`, `CuocHenDao`: Đóng gói toàn bộ các câu lệnh SQL (CRUD, gom nhóm, sắp xếp) riêng cho từng thực thể. Các tầng UI (Activity, Fragment, Dialog) không bao giờ gọi trực tiếp SQLiteDatabase mà luôn giao tiếp thông qua DAO, giúp phân tách độc lập giữa tầng nghiệp vụ và tầng lưu trữ.

---

## 1. DatabaseHelper.java (`app/src/main/java/com/example/app/db/DatabaseHelper.java`)

### 1.1. Vai trò
`DatabaseHelper` là lớp trung tâm quản lý việc tạo mới, mở kết nối và nâng cấp cơ sở dữ liệu SQLite `tiendo.db`. Lớp này đảm bảo cơ chế thực thi khóa ngoại tầng CSDL và cung cấp kết nối CSDL an toàn thông qua mẫu thiết kế Singleton.

### 1.2. Mối quan hệ
* **Các lớp gọi `DatabaseHelper`:**
  * `DoAnDao`, `NhiemVuDao`, `CuocHenDao`: Gọi `DatabaseHelper.getInstance(context)` để lấy thể hiện duy nhất và mở kết nối đọc/ghi (`getReadableDatabase()`, `getWritableDatabase()`).
  * `SampleDataLoader`: Lấy writable database để thực hiện transaction nạp dữ liệu mẫu.
  * `DatabaseTest`: Gọi `getInstance()` và `close()` trong vòng đời kiểm thử.
* **Lớp mà `DatabaseHelper` gọi:** Kế thừa trực tiếp từ `android.database.sqlite.SQLiteOpenHelper`, sử dụng `android.database.sqlite.SQLiteDatabase` và `android.content.Context`.

### 1.3. Thuộc tính và hằng số quan trọng
* `DATABASE_NAME` (`"tiendo.db"`): Tên tệp tin cơ sở dữ liệu SQLite lưu cục bộ trong bộ nhớ trong của ứng dụng (`/data/data/com.example.app/databases/tiendo.db`).
* `DATABASE_VERSION` (`1`): Phiên bản lược đồ CSDL hiện tại.
* **Tên các bảng (Table Names):**
  * `TABLE_DO_AN` (`"DoAn"`): Bảng lưu trữ đồ án.
  * `TABLE_NHIEM_VU` (`"NhiemVu"`): Bảng lưu trữ nhiệm vụ.
  * `TABLE_CUOC_HEN` (`"CuocHen"`): Bảng lưu trữ cuộc hẹn.
* **Tên các cột (Column Names):**
  * `COLUMN_ID` (`"id"`): Khóa chính chung kiểu INTEGER AUTOINCREMENT.
  * Bảng `DoAn`: `COLUMN_DO_AN_TEN` (`"ten"`), `COLUMN_DO_AN_MO_TA` (`"mo_ta"`), `COLUMN_DO_AN_GIANG_VIEN` (`"giang_vien"`), `COLUMN_DO_AN_NGAY_BAT_DAU` (`"ngay_bat_dau"`), `COLUMN_DO_AN_HAN_NOP` (`"han_nop"`).
  * Bảng `NhiemVu`: `COLUMN_NHIEM_VU_DO_AN_ID` (`"do_an_id"`), `COLUMN_NHIEM_VU_TEN` (`"ten"`), `COLUMN_NHIEM_VU_GHI_CHU` (`"ghi_chu"`), `COLUMN_NHIEM_VU_HAN_HOAN_THANH` (`"han_hoan_thanh"`), `COLUMN_NHIEM_VU_DA_HOAN_THANH` (`"da_hoan_thanh"`).
  * Bảng `CuocHen`: `COLUMN_CUOC_HEN_DO_AN_ID` (`"do_an_id"`), `COLUMN_CUOC_HEN_TIEU_DE` (`"tieu_de"`), `COLUMN_CUOC_HEN_THOI_GIAN` (`"thoi_gian"`), `COLUMN_CUOC_HEN_DIA_DIEM` (`"dia_diem"`), `COLUMN_CUOC_HEN_GHI_CHU` (`"ghi_chu"`).
* `instance` (`DatabaseHelper`): Biến tĩnh (static) lưu thể hiện duy nhất của lớp Singleton.

### 1.4. Bảng giải thích chi tiết từng hàm trong `DatabaseHelper.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `getInstance(Context)` | `Context context` $\rightarrow$ `DatabaseHelper` | Lấy thể hiện Singleton duy nhất của DatabaseHelper | **Bước 1:** Kiểm tra nếu `instance == null`.<br>**Bước 2:** Khởi tạo `instance = new DatabaseHelper(context.getApplicationContext())`.<br>**Bước 3:** Trả về `instance`. Từ khóa `synchronized` đảm bảo an toàn đa luồng. | Tất cả các DAO (`DoAnDao`, `NhiemVuDao`, `CuocHenDao`), `SampleDataLoader`, `DatabaseTest` |
| 2 | `DatabaseHelper(Context)` | `Context context` $\rightarrow$ `void` | Constructor khởi tạo helper | Gọi `super(context, DATABASE_NAME, null, DATABASE_VERSION)` để đăng ký tên file CSDL và phiên bản với hệ điều hành Android. | `getInstance(Context)` |
| 3 | `onConfigure(SQLiteDatabase)` | `SQLiteDatabase db` $\rightarrow$ `void` | Cấu hình tham số kết nối CSDL trước khi mở | **Bước 1:** Gọi `super.onConfigure(db)`.<br>**Bước 2:** Gọi `db.setForeignKeyConstraintsEnabled(true);` để kích hoạt cờ kiểm tra khóa ngoại (PRAGMA foreign_keys = ON). | Hệ điều hành Android gọi tự động đầu tiên khi mở kết nối CSDL |
| 4 | `onCreate(SQLiteDatabase)` | `SQLiteDatabase db` $\rightarrow$ `void` | Khởi tạo cấu trúc các bảng khi CSDL được tạo lần đầu | **Bước 1:** Xây dựng câu lệnh DDL `CREATE TABLE DoAn (...)`.<br>**Bước 2:** Xây dựng câu lệnh DDL `CREATE TABLE NhiemVu (...)` kèm `REFERENCES DoAn(id) ON DELETE CASCADE`.<br>**Bước 3:** Xây dựng DDL `CREATE TABLE CuocHen (...)` kèm `REFERENCES DoAn(id) ON DELETE CASCADE`.<br>**Bước 4:** Thực thi 3 câu lệnh qua `db.execSQL(...)`. | Hệ điều hành Android gọi tự động một lần duy nhất khi tệp `tiendo.db` chưa tồn tại |
| 5 | `onUpgrade(SQLiteDatabase, int, int)` | `SQLiteDatabase db, int oldVersion, int newVersion` $\rightarrow$ `void` | Xử lý nâng cấp lược đồ CSDL khi tăng số `DATABASE_VERSION` | **Bước 1:** Xóa bảng phụ `CuocHen` bằng `DROP TABLE IF EXISTS`.<br>**Bước 2:** Xóa bảng phụ `NhiemVu`.<br>**Bước 3:** Xóa bảng chính `DoAn`.<br>**Bước 4:** Gọi lại `onCreate(db)` để tái tạo bảng mới. | Hệ điều hành Android gọi khi số version trong code lớn hơn số version lưu trong file DB |

### 1.5. Điểm kỹ thuật đáng chú ý
* **Vì sao bắt buộc phải dùng `onConfigure` để bật Khóa ngoại?** Mặc định trong SQLite và Android OS, tính năng kiểm tra khóa ngoại bị tắt vì lý do tương thích ngược (legacy compatibility). Nếu chỉ viết `REFERENCES DoAn(id) ON DELETE CASCADE` trong câu lệnh `CREATE TABLE` mà không gọi `db.setForeignKeyConstraintsEnabled(true)` trong `onConfigure`, SQLite sẽ bỏ qua kiểm tra khóa ngoại và tính năng xóa tầng (Cascade) sẽ **hoàn toàn vô hiệu hóa**.
* **Thứ tự xóa trong `onUpgrade` (dòng 99–101):** Phải xóa `CuocHen` và `NhiemVu` trước, sau đó mới xóa `DoAn` để tuân thủ tính toàn vẹn tham chiếu.

---

## 2. DoAnDao.java (`app/src/main/java/com/example/app/db/DoAnDao.java`)

### 2.1. Vai trò
`DoAnDao` đóng vai trò là lớp truy xuất dữ liệu độc quyền cho bảng `DoAn`. Ngoài các thao tác thêm, sửa, xóa, lớp này sở hữu truy vấn quan trọng nhất của ứng dụng: gom nhóm và tính toán tiến độ hoàn thành đồ án tự động ngay trong SQL thông qua câu lệnh `LEFT JOIN`.

### 2.2. Mối quan hệ
* **Các lớp gọi `DoAnDao`:**
  * `MainActivity`: Lấy danh sách đồ án kèm tiến độ (`getAllWithTienDo()`), xóa đồ án (`delete()`), kiểm tra dữ liệu tồn tại (`hasData()`).
  * `AddEditDoAnDialogFragment`: Thêm đồ án mới (`insert()`) hoặc cập nhật đồ án cũ (`update()`).
  * `DoAnDetailActivity`: Lấy thông tin chi tiết một đồ án (`getById()`) và lấy tiến độ cập nhật (`getTienDo()`).
  * `DatabaseTest`: Kiểm thử các thao tác CSDL.
* **Lớp mà `DoAnDao` gọi:** `DatabaseHelper`, `DoAn`, `DoAnWithTienDo`, `TienDoInfo`, `SQLiteDatabase`, `ContentValues`, `Cursor`.

### 2.3. Bảng giải thích chi tiết từng hàm trong `DoAnDao.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `DoAnDao(Context)` | `Context context` $\rightarrow$ `void` | Constructor khởi tạo DAO | Nhận context và gán `this.dbHelper = DatabaseHelper.getInstance(context);`. | Các Activity, Fragment, Test |
| 2 | `insert(DoAn)` | `DoAn doAn` $\rightarrow$ `long` | Chèn một đồ án mới vào CSDL | **Bước 1:** Lấy `SQLiteDatabase db = dbHelper.getWritableDatabase()`.<br>**Bước 2:** Đưa các trường tên, mô tả, GVHD, ngày bắt đầu, hạn nộp vào `ContentValues`.<br>**Bước 3:** Gọi `db.insert(TABLE_DO_AN, null, values)`.<br>**Bước 4:** Nếu id trả về khác -1, gán `doAn.setId((int) id)` và trả về id vừa chèn. | `AddEditDoAnDialogFragment.saveDoAn()`, `DatabaseTest` |
| 3 | `update(DoAn)` | `DoAn doAn` $\rightarrow$ `int` | Cập nhật thông tin đồ án theo ID | **Bước 1:** Đưa các thông tin mới vào `ContentValues`.<br>**Bước 2:** Gọi `db.update(...)` với điều kiện `id = ?`, tham số là `String.valueOf(doAn.getId())`.<br>**Bước 3:** Trả về số dòng bị ảnh hưởng (1 nếu thành công). | `AddEditDoAnDialogFragment.saveDoAn()` |
| 4 | `delete(int)` | `int id` $\rightarrow$ `int` | Xóa một đồ án theo mã ID | **Bước 1:** Lấy writable database.<br>**Bước 2:** Gọi `db.delete(TABLE_DO_AN, "id = ?", new String[]{String.valueOf(id)})`.<br>**Bước 3:** Trả về số dòng bị xóa (nhờ CASCADE, SQLite tự động dọn sạch các bản ghi trong NhiemVu và CuocHen liên quan). | `MainActivity` (xử lý FragmentResult từ ConfirmDeleteDialog), `DatabaseTest` |
| 5 | `getById(int)` | `int id` $\rightarrow$ `DoAn` | Lấy chi tiết một đồ án theo ID | **Bước 1:** Dùng `db.query(...)` trên bảng `DoAn` với điều kiện `id = ?`.<br>**Bước 2:** Sử dụng cú pháp `try-with-resources` bọc Cursor.<br>**Bước 3:** Nếu `cursor.moveToFirst() == true`, gọi `cursorToDoAn(cursor)` và trả về đối tượng; ngược lại trả về `null`. | `DoAnDetailActivity.loadData()`, `DatabaseTest` |
| 6 | `getAll()` | Không có $\rightarrow$ `List<DoAn>` | Lấy toàn bộ danh sách đồ án thuần | **Bước 1:** Dùng `db.query(...)` sắp xếp `id DESC`.<br>**Bước 2:** Lặp `while (cursor.moveToNext())` chuyển đổi từng dòng thành `DoAn` và thêm vào danh sách. | Khi chỉ cần danh sách đồ án mà không cần tính tiến độ |
| 7 | `getAllWithTienDo()` | Không có $\rightarrow$ `List<DoAnWithTienDo>` | Lấy danh sách đồ án kèm tiến độ hoàn thành | Xem phân tích chi tiết thuật toán ở mục 2.4 bên dưới. | `MainActivity.loadData()`, `DatabaseTest` |
| 8 | `getTienDo(int)` | `int doAnId` $\rightarrow$ `TienDoInfo` | Tính toán tiến độ của một đồ án cụ thể | **Bước 1:** Thực thi truy vấn: `SELECT COUNT(*) AS total, COALESCE(SUM(CASE WHEN da_hoan_thanh = 1 THEN 1 ELSE 0 END), 0) AS completed FROM NhiemVu WHERE do_an_id = ?`.<br>**Bước 2:** Đọc `total` và `completed` từ Cursor.<br>**Bước 3:** Trả về `new TienDoInfo(completed, total)`. | `DoAnDetailActivity.loadData()`, `DatabaseTest` |
| 9 | `getCount()` | Không có $\rightarrow$ `int` | Đếm tổng số đồ án hiện có | Thực thi truy vấn `SELECT COUNT(*) FROM DoAn`. Đọc giá trị tại cột 0 và trả về. | `hasData()`, `DatabaseTest` |
| 10 | `hasData()` | Không có $\rightarrow$ `boolean` | Kiểm tra CSDL đã có đồ án nào chưa | Gọi `return getCount() > 0;`. | `MainActivity.onLoadSampleDataRequested()` để quyết định nội dung hộp thoại hỏi nạp dữ liệu mẫu |
| 11 | `cursorToDoAn(Cursor)` | `Cursor cursor` $\rightarrow$ `DoAn` | (Private) Chuyển dữ liệu con trỏ Cursor thành `DoAn` | **Bước 1:** Khởi tạo `DoAn doAn = new DoAn()`.<br>**Bước 2:** Dùng `cursor.getColumnIndexOrThrow(...)` để lấy giá trị từng cột (`id`, `ten`, `mo_ta`, `giang_vien`, `ngay_bat_dau`, `han_nop`) gán vào `doAn`.<br>**Bước 3:** Trả về đối tượng `doAn`. | `getById()`, `getAll()` |

### 2.4. Phân tích thuật toán hàm `getAllWithTienDo()` (dòng 104–143)
Đây là câu truy vấn SQL phức tạp nhất và có hiệu năng tối ưu nhất của toàn bộ dự án:
```sql
SELECT 
    d.id AS d_id, d.ten AS d_ten, d.mo_ta AS d_mo_ta, 
    d.giang_vien AS d_giang_vien, d.ngay_bat_dau AS d_ngay_bat_dau, d.han_nop AS d_han_nop,
    COUNT(n.id) AS total_tasks,
    COALESCE(SUM(CASE WHEN n.da_hoan_thanh = 1 THEN 1 ELSE 0 END), 0) AS completed_tasks
FROM DoAn d
LEFT JOIN NhiemVu n ON d.id = n.do_an_id
GROUP BY d.id
ORDER BY d.id DESC;
```
* **Tại sao dùng `LEFT JOIN` thay vì `INNER JOIN`?** Nếu dùng `INNER JOIN`, những đồ án mới tạo mà chưa có bất kỳ nhiệm vụ nào sẽ bị **loại bỏ hoàn toàn khỏi kết quả truy vấn**. Sử dụng `LEFT JOIN` đảm bảo tất cả các đồ án đều xuất hiện trên màn hình chính, ngay cả khi đồ án đó có 0 nhiệm vụ.
* **Hàm `COALESCE(...)`:** Khi một đồ án có 0 nhiệm vụ, lệnh `SUM(...)` sẽ trả về `NULL`. `COALESCE(..., 0)` ép giá trị `NULL` về `0`, ngăn ngừa lỗi đọc dữ liệu.
* **Cơ chế đọc dòng 122–140:** Duyệt Cursor qua vòng lặp `do-while`, trích xuất thông tin đồ án đưa vào `DoAn`, lấy `total_tasks` và `completed_tasks` đưa vào `TienDoInfo`, đóng gói vào `DoAnWithTienDo` và nạp vào danh sách trả về.

---

## 3. NhiemVuDao.java (`app/src/main/java/com/example/app/db/NhiemVuDao.java`)

### 3.1. Vai trò
`NhiemVuDao` quản lý dữ liệu cho bảng `NhiemVu`. Ngoài CRUD cơ bản, lớp này cung cấp phương thức cập nhật siêu tốc `updateStatus()` phục vụ thao tác tick/untick CheckBox của người dùng trên giao diện danh sách, và sắp xếp nhiệm vụ theo độ ưu tiên nghiệp vụ.

### 3.2. Mối quan hệ
* **Các lớp gọi `NhiemVuDao`:**
  * `NhiemVuFragment`: Lấy danh sách nhiệm vụ của đồ án (`getByDoAnId()`), xử lý đảo trạng thái CheckBox (`updateStatus()`), xóa nhiệm vụ (`delete()`).
  * `AddEditNhiemVuDialogFragment`: Thêm (`insert()`) hoặc sửa nhiệm vụ (`update()`).
  * `DatabaseTest`: Kiểm thử tính toàn vẹn CSDL.
* **Lớp mà `NhiemVuDao` gọi:** `DatabaseHelper`, `NhiemVu`, `SQLiteDatabase`, `ContentValues`, `Cursor`.

### 3.3. Bảng giải thích chi tiết từng hàm trong `NhiemVuDao.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `NhiemVuDao(Context)` | `Context context` $\rightarrow$ `void` | Constructor khởi tạo DAO | Gán `this.dbHelper = DatabaseHelper.getInstance(context);`. | Các Fragment, Dialog, Test |
| 2 | `insert(NhiemVu)` | `NhiemVu nhiemVu` $\rightarrow$ `long` | Chèn nhiệm vụ mới vào CSDL | Đưa `do_an_id`, `ten`, `ghi_chu`, `han_hoan_thanh`, `da_hoan_thanh` vào `ContentValues`, gọi `db.insert()`, gán ID trả về cho đối tượng. | `AddEditNhiemVuDialogFragment`, `DatabaseTest` |
| 3 | `update(NhiemVu)` | `NhiemVu nhiemVu` $\rightarrow$ `int` | Cập nhật đầy đủ các trường của nhiệm vụ | Cập nhật tên, ghi chú, hạn chót và trạng thái của nhiệm vụ dựa trên `id = ?`. | `AddEditNhiemVuDialogFragment` (khi sửa) |
| 4 | `updateStatus(int, boolean)` | `int id, boolean isCompleted` $\rightarrow$ `int` | Cập nhật nhanh trạng thái hoàn thành | **Bước 1:** Tạo `ContentValues values = new ContentValues()`.<br>**Bước 2:** `values.put(COLUMN_NHIEM_VU_DA_HOAN_THANH, isCompleted ? 1 : 0)`.<br>**Bước 3:** Gọi `db.update(TABLE_NHIEM_VU, values, "id = ?", new String[]{String.valueOf(id)})`. | `NhiemVuFragment.onTaskCheckChanged()`, `DatabaseTest` |
| 5 | `delete(int)` | `int id` $\rightarrow$ `int` | Xóa nhiệm vụ theo ID | Gọi `db.delete(TABLE_NHIEM_VU, "id = ?", new String[]{String.valueOf(id)})`. Trả về số dòng xóa (1 nếu thành công). | `NhiemVuFragment` (xử lý kết quả từ ConfirmDeleteDialog) |
| 6 | `deleteByDoAnId(int)` | `int doAnId` $\rightarrow$ `int` | Xóa tất cả nhiệm vụ thuộc một đồ án | Gọi `db.delete(TABLE_NHIEM_VU, "do_an_id = ?", new String[]{String.valueOf(doAnId)})`. | Hàm dự phòng khi cần xóa thủ công theo đồ án |
| 7 | `getById(int)` | `int id` $\rightarrow$ `NhiemVu` | Lấy chi tiết một nhiệm vụ theo ID | Query bảng `NhiemVu` với điều kiện `id = ?`. Chuyển Cursor thành `NhiemVu` và trả về. | `AddEditNhiemVuDialogFragment`, `DatabaseTest` |
| 8 | `getAll()` | Không có $\rightarrow$ `List<NhiemVu>` | Lấy tất cả nhiệm vụ trong toàn hệ thống | Query toàn bộ bảng sắp xếp `id DESC`. | Quản trị chung |
| 9 | `getByDoAnId(int)` | `int doAnId` $\rightarrow$ `List<NhiemVu>` | Lấy danh sách nhiệm vụ của 1 đồ án kèm sắp xếp thông minh | Query bảng `NhiemVu` với điều kiện `do_an_id = ?`, sắp xếp theo mệnh đề: `da_hoan_thanh ASC, han_hoan_thanh ASC`. Trả về danh sách. | `NhiemVuFragment.loadData()`, `DatabaseTest` |
| 10 | `getCount()` | Không có $\rightarrow$ `int` | Đếm tổng số nhiệm vụ trong CSDL | Thực thi `SELECT COUNT(*) FROM NhiemVu`. | `DatabaseTest.testSampleDataLoaderImport()` |
| 11 | `cursorToNhiemVu(Cursor)` | `Cursor cursor` $\rightarrow$ `NhiemVu` | (Private) Đọc Cursor thành `NhiemVu` | Lấy các giá trị: `id`, `do_an_id`, `ten`, `ghi_chu`, `han_hoan_thanh`, `da_hoan_thanh` từ Cursor và gán vào đối tượng mới. | `getById()`, `getAll()`, `getByDoAnId()` |

### 3.4. Điểm kỹ thuật đáng chú ý
* **Hàm `updateStatus()` (dòng 53–64):** Được tối ưu hóa cho thao tác chạm CheckBox của người dùng. Thay vì phải đọc lại toàn bộ đối tượng `NhiemVu` rồi gọi hàm `update()` cập nhật tất cả 6 cột, hàm này chỉ gửi đúng 1 cột `da_hoan_thanh` xuống CSDL SQLite, giúp giảm thiểu tải I/O đĩa và phản hồi tức thì trên giao diện.
* **Chiến lược sắp xếp danh sách (dòng 134):** `ORDER BY da_hoan_thanh ASC, han_hoan_thanh ASC`.
  * `da_hoan_thanh ASC`: Các công việc chưa hoàn thành (`0`) luôn được ưu tiên nổi lên trên đầu danh sách để sinh viên nhìn thấy ngay; các công việc đã xong (`1`) tự động tụt xuống dưới.
  * `han_hoan_thanh ASC`: Trong các công việc chưa xong, việc nào có hạn chót gấp nhất (ngày nhỏ hơn) sẽ được xếp lên trước.

---

## 4. CuocHenDao.java (`app/src/main/java/com/example/app/db/CuocHenDao.java`)

### 4.1. Vai trò
`CuocHenDao` quản lý việc lưu trữ và truy vấn dữ liệu cho bảng `CuocHen`. Lớp này đảm bảo các buổi hẹn gặp Giảng viên hướng dẫn luôn được sắp xếp theo trình tự thời gian tăng dần (`thoi_gian ASC`), phục vụ hiển thị lịch trình tuần tự cho sinh viên.

### 4.2. Mối quan hệ
* **Các lớp gọi `CuocHenDao`:**
  * `CuocHenFragment`: Lấy danh sách cuộc hẹn của đồ án (`getByDoAnId()`), xóa cuộc hẹn (`delete()`).
  * `AddEditCuocHenDialogFragment`: Thêm (`insert()`) hoặc sửa cuộc hẹn (`update()`).
  * `DatabaseTest`: Kiểm thử tính toàn vẹn và thứ tự sắp xếp thời gian.
* **Lớp mà `CuocHenDao` gọi:** `DatabaseHelper`, `CuocHen`, `SQLiteDatabase`, `ContentValues`, `Cursor`.

### 4.3. Bảng giải thích chi tiết từng hàm trong `CuocHenDao.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `CuocHenDao(Context)` | `Context context` $\rightarrow$ `void` | Constructor khởi tạo DAO | Gán `this.dbHelper = DatabaseHelper.getInstance(context);`. | Các Fragment, Dialog, Test |
| 2 | `insert(CuocHen)` | `CuocHen cuocHen` $\rightarrow$ `long` | Chèn cuộc hẹn mới vào CSDL | Đưa `do_an_id`, `tieu_de`, `thoi_gian`, `dia_diem`, `ghi_chu` vào `ContentValues`, gọi `db.insert()`, cập nhật ID cho đối tượng. | `AddEditCuocHenDialogFragment`, `DatabaseTest` |
| 3 | `update(CuocHen)` | `CuocHen cuocHen` $\rightarrow$ `int` | Cập nhật thông tin cuộc hẹn | Cập nhật tiêu đề, thời gian, địa điểm, ghi chú theo điều kiện `id = ?`. Trả về số dòng cập nhật. | `AddEditCuocHenDialogFragment`, `DatabaseTest` |
| 4 | `delete(int)` | `int id` $\rightarrow$ `int` | Xóa cuộc hẹn theo ID | Gọi `db.delete(TABLE_CUOC_HEN, "id = ?", new String[]{String.valueOf(id)})`. Trả về số dòng xóa. | `CuocHenFragment` (khi xác nhận xóa), `DatabaseTest` |
| 5 | `deleteByDoAnId(int)` | `int doAnId` $\rightarrow$ `int` | Xóa toàn bộ cuộc hẹn thuộc một đồ án | Gọi `db.delete(TABLE_CUOC_HEN, "do_an_id = ?", new String[]{String.valueOf(doAnId)})`. | Hàm dự phòng khi cần xóa thủ công theo đồ án |
| 6 | `getById(int)` | `int id` $\rightarrow$ `CuocHen` | Lấy chi tiết cuộc hẹn theo ID | Query bảng `CuocHen` theo `id = ?`. Chuyển Cursor thành `CuocHen` và trả về. | `AddEditCuocHenDialogFragment`, `DatabaseTest` |
| 7 | `getAll()` | Không có $\rightarrow$ `List<CuocHen>` | Lấy tất cả cuộc hẹn trong hệ thống | Query toàn bộ bảng sắp xếp `thoi_gian ASC`. | Xem lịch trình tổng quát |
| 8 | `getByDoAnId(int)` | `int doAnId` $\rightarrow$ `List<CuocHen>` | Lấy danh sách cuộc hẹn của đồ án theo trình tự thời gian | Query bảng `CuocHen` với điều kiện `do_an_id = ?`, sắp xếp `thoi_gian ASC`. Trả về danh sách. | `CuocHenFragment.loadData()`, `DatabaseTest` |
| 9 | `getCount()` | Không có $\rightarrow$ `int` | Đếm tổng số cuộc hẹn trong CSDL | Thực thi truy vấn `SELECT COUNT(*) FROM CuocHen`. Đọc cột 0 và trả về. | `DatabaseTest.testSampleDataLoaderImport()` |
| 10 | `cursorToCuocHen(Cursor)` | `Cursor cursor` $\rightarrow$ `CuocHen` | (Private) Đọc dữ liệu Cursor thành `CuocHen` | Lấy các cột: `id`, `do_an_id`, `tieu_de`, `thoi_gian`, `dia_diem`, `ghi_chu` từ Cursor và gán vào đối tượng mới. | `getById()`, `getAll()`, `getByDoAnId()` |

### 4.4. Điểm kỹ thuật đáng chú ý
* **Sắp xếp thời gian tự nhiên trong SQLite (dòng 121):** Mệnh đề `ORDER BY thoi_gian ASC` hoạt động hoàn hảo do chuỗi thời gian được chuẩn hóa định dạng `yyyy-MM-dd HH:mm`. SQLite thực hiện so sánh mã ký tự (ASCII comparison) từ trái sang phải, đảm bảo cuộc hẹn sớm hơn luôn đứng trước cuộc hẹn muộn hơn mà không cần hàm chuyển đổi thời gian phụ trợ.
* **Sử dụng `try-with-resources` cho Cursor:** Trong tất cả các hàm truy vấn của cả 3 DAO (`DoAnDao`, `NhiemVuDao`, `CuocHenDao`), khối `try (Cursor cursor = ...)` đảm bảo Cursor luôn được tự động đóng (`cursor.close()`) ngay cả khi có ngoại lệ xảy ra, ngăn chặn hoàn toàn lỗi rò rỉ con trỏ (Cursor leak / Window leak) thường gặp trên Android.
