# BÁO CÁO BÀI TẬP LỚN LẬP TRÌNH THIẾT BỊ DI ĐỘNG
## ĐỀ TÀI: ỨNG DỤNG THEO DÕI TIẾN ĐỘ ĐỒ ÁN VÀ LỊCH HẸN GIẢNG VIÊN (ANDROID)

---

* **Học phần:** Lập trình Thiết bị Di động (LTTBDD)
* **Nền tảng:** Android (Java, SQLite thuần qua `SQLiteOpenHelper`, RecyclerView, Material Components)
* **Phiên bản hỗ trợ:** Android 7.0 (API 24) trở lên, `targetSdk 37`
* **Package:** `com.example.app`
* **Cơ sở dữ liệu:** `tiendo.db` (SQLite)

---

## MỤC LỤC
1. [Giới thiệu đề tài](#1-giới-thiệu-đề-tài)
2. [Phân tích yêu cầu](#2-phân-tích-yêu-cầu)
3. [Thiết kế Cơ sở Dữ liệu (CSDL)](#3-thiết-kế-cơ-sở-dữ-liệu-csdl)
4. [Thiết kế Giao diện Người dùng (UI/UX)](#4-thiết-kế-giao-diện-người-dùng-uiux)
5. [Thiết kế Kiến trúc và Kỹ thuật Lập trình](#5-thiết-kế-kiến-trúc-và-kỹ-thuật-lập-trình)
6. [Kiểm thử và Đánh giá](#6-kiểm-thử-và-đánh-giá)
7. [Kết quả đạt được, Hạn chế và Hướng phát triển](#7-kết-quả-đạt-được-hạn-chế-và-hướng-phát-triển)
8. [Phụ lục](#8-phụ-lục)

---

## 1. GIỚI THIỆU ĐỀ TÀI

### 1.1. Đặt vấn đề và bài toán thực tế
Trong quá trình học tập tại bậc đại học, sinh viên công nghệ thông tin và các ngành kỹ thuật thường xuyên phải thực hiện đồng thời nhiều loại đồ án khác nhau: Đồ án môn học, Thực tập chuyên ngành, Đề án khởi nghiệp, và Đồ án tốt nghiệp (Khóa luận tốt nghiệp). Mỗi đồ án kéo dài từ vài tuần đến hàng tháng, bao gồm khối lượng công việc lớn được chia thành nhiều giai đoạn, nhiều hạn chót (deadline) trung gian, cùng các buổi gặp gỡ, báo cáo định kỳ với Giảng viên hướng dẫn (GVHD).

Thực tế sinh viên hay gặp các khó khăn:
* Không nắm bắt được tiến độ tổng quát: Dễ bị "dồn ứ" khối lượng công việc vào sát ngày nộp.
* Bỏ quên các đầu việc nhỏ hoặc nhầm lẫn hạn hoàn thành giữa các môn học.
* Quên hoặc nhầm lẫn lịch hẹn, địa điểm gặp giảng viên (văn phòng bộ môn, phòng lab, phòng họp trực tuyến Google Meet/Zoom).
* Phụ thuộc vào các ứng dụng ghi chú rời rạc, không gắn liền trực tiếp giữa đồ án – nhiệm vụ – lịch hẹn.

### 1.2. Mục tiêu và đối tượng sử dụng
* **Đối tượng sử dụng chính:** Sinh viên đại học/cao đẳng cần công cụ cá nhân trực quan trên điện thoại để quản lý tiến độ học tập.
* **Mục tiêu của ứng dụng:** Xây dựng một ứng dụng Android hoàn chỉnh, hoạt động độc lập (offline-first), lưu trữ an toàn bằng SQLite thuần, cung cấp giao diện Material hiện đại giúp sinh viên dễ dàng theo dõi toàn bộ đồ án, nhiệm vụ và lịch hẹn một cách khoa học.

### 1.3. Phạm vi chức năng
* **Quản lý Đồ án (Projects):** Thêm, sửa, xóa thông tin đồ án, người hướng dẫn, thời gian bắt đầu và hạn nộp; hiển thị tiến độ hoàn thành tự động tính toán dạng tỷ lệ phần trăm (%).
* **Quản lý Nhiệm vụ (Tasks):** Chia nhỏ đồ án thành các đầu mục công việc, đặt hạn hoàn thành, đánh dấu nhanh trạng thái hoàn thành trực tiếp bằng checkbox.
* **Quản lý Cuộc hẹn (Appointments):** Đặt lịch trao đổi với giảng viên kèm mốc thời gian (ngày, giờ), địa điểm và nội dung cần trao đổi; tự động phân loại cuộc hẹn "Sắp diễn ra" và "Đã qua".
* **Bộ dữ liệu mẫu (Sample Data):** 25 bản ghi được nạp sẵn khi cài lần đầu và có thể nạp lại chỉ với một thao tác từ menu, hỗ trợ minh họa và nghiệm thu sản phẩm thuận tiện.

---

## 2. PHÂN TÍCH YÊU CẦU

### 2.1. Yêu cầu chức năng
1. **Quản trị Đồ án:**
   * Xem danh sách đồ án kèm thanh tiến độ trực quan, số đầu việc đã xong/tổng số; chuyển đổi chế độ xem danh sách / lưới 2 cột.
   * Xem chi tiết đồ án với 4 thẻ thống kê nhanh và dòng thời gian tổng hợp nhiệm vụ – lịch hẹn.
   * Thêm đồ án mới với ràng buộc: Hạn nộp phải sau hoặc bằng ngày bắt đầu; khi sửa, hạn nộp không được sớm hơn hạn của nhiệm vụ muộn nhất và ngày bắt đầu không được muộn hơn hạn của nhiệm vụ sớm nhất.
   * Chỉnh sửa thông tin đồ án đã có.
   * Xóa đồ án kèm cảnh báo xóa xếp tầng (cascade) toàn bộ nhiệm vụ và cuộc hẹn trực thuộc.
2. **Quản trị Nhiệm vụ:**
   * Xem danh sách nhiệm vụ của từng đồ án, tự động phân nhóm/sắp xếp: việc chưa hoàn thành lên trước, việc đã hoàn thành xuống dưới.
   * Thêm nhiệm vụ với ràng buộc: Hạn hoàn thành của nhiệm vụ phải nằm trong khoảng từ ngày bắt đầu đến hạn nộp của đồ án.
   * Chỉnh sửa và xóa từng nhiệm vụ.
   * Chạm vào Checkbox để đảo trạng thái (hoàn thành / chưa xong); thanh tiến độ đồ án cập nhật tức thì.
3. **Quản trị Cuộc hẹn:**
   * Xem danh sách cuộc hẹn sắp xếp theo thứ tự thời gian tăng dần.
   * Hiển thị trực quan nhãn trạng thái (Badge): màu xanh lá *"Sắp diễn ra"* đối với cuộc hẹn trong tương lai, màu xám *"Đã qua"* đối với cuộc hẹn trong quá khứ so với thời điểm hiện tại.
   * Thêm, sửa, xóa cuộc hẹn với bộ chọn ngày (DatePicker) và bộ chọn giờ (TimePicker); khi sửa, bộ chọn mở sẵn đúng ngày giờ đã lưu.
4. **Nạp dữ liệu mẫu:**
   * Dữ liệu mẫu (4 đồ án, 14 nhiệm vụ, 7 cuộc hẹn) được nạp tự động khi cài đặt lần đầu.
   * Mục **"Nạp dữ liệu mẫu"** trong menu ⋮ của màn hình chính cho phép khôi phục lại bộ dữ liệu này bất cứ lúc nào.
   * Vì thao tác thay thế toàn bộ dữ liệu hiện có, ứng dụng luôn hỏi xác nhận bằng hộp thoại trước khi thực hiện, và chạy trong Transaction để không bao giờ bị xóa dở dang.

### 2.2. Yêu cầu phi chức năng
* **Độ tương thích:** Hỗ trợ từ Android 7.0 (API 24 - Nougat) đến Android 15/16 (API 35-37).
* **Hiệu năng & Bộ nhớ:** Cuộn danh sách mượt nhờ cơ chế tái sử dụng ViewHolder của RecyclerView; tiến độ của mọi đồ án được tính bằng một truy vấn duy nhất; cập nhật giao diện bằng `notifyItemInserted/Removed/Moved` thay vì vẽ lại toàn bộ.
* **Độ tin cậy & Toàn vẹn:** Dữ liệu đang nhập không bị mất khi xoay màn hình; khóa ngoại `ON DELETE CASCADE` không để lại dữ liệu mồ côi; thao tác nạp lại dữ liệu mẫu chạy trong Transaction để đảm bảo tính nguyên tử.
* **Trải nghiệm người dùng (UX):** Thiết kế theo hướng dẫn Material Design 3, màu sắc tương phản rõ ràng, hỗ trợ đầy đủ Chế độ Tối (Dark Theme).

### 2.3. Sơ đồ Use Case tổng quát

```mermaid
flowchart TD
    SinhVien([Sinh viên / Người dùng])

    subgraph "Ứng dụng Quản lý Tiến độ Đồ án"
        UC1[Xem danh sách Đồ án & Tiến độ]
        UC2[Thêm / Sửa / Xóa Đồ án]
        UC3[Xem chi tiết Đồ án]
        UC4[Quản lý Nhiệm vụ: Thêm/Sửa/Xóa]
        UC5[Đánh dấu hoàn thành Nhiệm vụ]
        UC6[Quản lý Cuộc hẹn: Thêm/Sửa/Xóa]
        UC7[Xem trạng thái Lịch hẹn Sắp đến/Đã qua]
        UC8[Nạp dữ liệu mẫu minh họa]
    end

    SinhVien --> UC1
    SinhVien --> UC2
    SinhVien --> UC3
    SinhVien --> UC8

    UC3 -.->|Bao gồm| UC4
    UC3 -.->|Bao gồm| UC5
    UC3 -.->|Bao gồm| UC6
    UC3 -.->|Bao gồm| UC7
```

---

## 3. THIẾT KẾ CƠ SỞ DỮ LIỆU (CSDL)

### 3.1. Sơ đồ Thực thể Mối quan hệ (ERD)

```mermaid
erDiagram
    DoAn ||--o{ NhiemVu : "chứa (1 - N)"
    DoAn ||--o{ CuocHen : "có (1 - N)"

    DoAn {
        INTEGER id PK "Tự tăng (AUTOINCREMENT)"
        TEXT ten "Tên đồ án (NOT NULL)"
        TEXT mo_ta "Mô tả chi tiết"
        TEXT giang_vien "Giảng viên hướng dẫn"
        TEXT ngay_bat_dau "Ngày bắt đầu (yyyy-MM-dd)"
        TEXT han_nop "Hạn nộp đồ án (yyyy-MM-dd)"
    }

    NhiemVu {
        INTEGER id PK "Tự tăng (AUTOINCREMENT)"
        INTEGER do_an_id FK "Mã đồ án (NOT NULL, CASCADE)"
        TEXT ten "Tên nhiệm vụ (NOT NULL)"
        TEXT ghi_chu "Ghi chú công việc"
        TEXT han_hoan_thanh "Hạn chót (yyyy-MM-dd)"
        INTEGER da_hoan_thanh "0: Chưa xong, 1: Đã xong"
    }

    CuocHen {
        INTEGER id PK "Tự tăng (AUTOINCREMENT)"
        INTEGER do_an_id FK "Mã đồ án (NOT NULL, CASCADE)"
        TEXT tieu_de "Tiêu đề cuộc hẹn (NOT NULL)"
        TEXT thoi_gian "Thời gian (yyyy-MM-dd HH:mm, NOT NULL)"
        TEXT dia_diem "Địa điểm / Link họp"
        TEXT ghi_chu "Nội dung trao đổi"
    }
```

### 3.2. Mô tả chi tiết các bảng CSDL

#### Bảng 1: `DoAn` (Lưu thông tin đồ án)
* `id` (INTEGER, Primary Key, Auto Increment): Mã định danh duy nhất của đồ án.
* `ten` (TEXT, NOT NULL): Tên đồ án hoặc đề tài nghiên cứu.
* `mo_ta` (TEXT, Nullable): Tóm tắt mục tiêu, nội dung đồ án.
* `giang_vien` (TEXT, Nullable): Họ và tên, học hàm học vị của GVHD.
* `ngay_bat_dau` (TEXT, Nullable): Ngày khởi động làm đồ án theo định dạng chuẩn ISO-8601 (`yyyy-MM-dd`).
* `han_nop` (TEXT, Nullable): Ngày phải nộp đồ án (`yyyy-MM-dd`).

#### Bảng 2: `NhiemVu` (Lưu các đầu việc thuộc đồ án)
* `id` (INTEGER, Primary Key, Auto Increment): Mã nhiệm vụ.
* `do_an_id` (INTEGER, NOT NULL, Foreign Key): Khóa ngoại liên kết tới `DoAn(id)` với thuộc tính `ON DELETE CASCADE`.
* `ten` (TEXT, NOT NULL): Tên đầu việc cần làm.
* `ghi_chu` (TEXT, Nullable): Hướng dẫn, lưu ý khi thực hiện.
* `han_hoan_thanh` (TEXT, Nullable): Hạn chót của việc (`yyyy-MM-dd`).
* `da_hoan_thanh` (INTEGER, NOT NULL, DEFAULT 0): Cờ trạng thái (0: Chưa xong, 1: Đã hoàn thành).

#### Bảng 3: `CuocHen` (Lưu lịch hẹn gặp GVHD)
* `id` (INTEGER, Primary Key, Auto Increment): Mã cuộc hẹn.
* `do_an_id` (INTEGER, NOT NULL, Foreign Key): Khóa ngoại liên kết tới `DoAn(id)` với thuộc tính `ON DELETE CASCADE`.
* `tieu_de` (TEXT, NOT NULL): Nội dung hoặc chủ đề buổi hẹn.
* `thoi_gian` (TEXT, NOT NULL): Thời điểm hẹn đầy đủ ngày và giờ (`yyyy-MM-dd HH:mm`).
* `dia_diem` (TEXT, Nullable): Phòng học, văn phòng khoa, phòng lab hoặc đường dẫn phòng họp trực tuyến.
* `ghi_chu` (TEXT, Nullable): Nội dung cần chuẩn bị hoặc kết quả trao đổi.

---

### 3.3. Giải thích các quyết định thiết kế CSDL

#### 1. Lý do tách thành 3 bảng riêng biệt
Theo nguyên lý chuẩn hóa cơ sở dữ liệu (đạt dạng chuẩn 3NF):
* Một đồ án có thể có không giới hạn số lượng nhiệm vụ và cuộc hẹn.
* Các thuộc tính của `NhiemVu` (như `han_hoan_thanh`, `da_hoan_thanh`) chỉ phụ thuộc vào nhiệm vụ cụ thể, không phụ thuộc trực tiếp vào toàn bộ đồ án.
* Việc tách riêng giúp tránh dư thừa dữ liệu (data redundancy), loại bỏ hoàn toàn các bất thường khi cập nhật (update anomalies), chèn (insert anomalies) hoặc xóa (delete anomalies).

#### 2. Khóa ngoại và ràng buộc `ON DELETE CASCADE`
* **Vấn đề thực tế:** Khi một sinh viên hủy bỏ hoặc xóa một đồ án, tất cả nhiệm vụ và cuộc hẹn của đồ án đó sẽ trở thành "dữ liệu mồ côi" (orphaned records) nếu không được dọn dẹp, gây lãng phí dung lượng lưu trữ và sai lệch thống kê.
* **Giải pháp:** Thiết lập khóa ngoại `REFERENCES DoAn(id) ON DELETE CASCADE` và bật ràng buộc này trong `DBHelper.onConfigure(SQLiteDatabase db)` bằng `db.setForeignKeyConstraintsEnabled(true);`. Khi xóa một dòng trong bảng `DoAn`, hệ thống SQLite tự động xóa sạch toàn bộ các dòng liên quan trong `NhiemVu` và `CuocHen` ở cấp độ CSDL, không cần phải viết thủ công nhiều câu lệnh xóa phức tạp ở tầng ứng dụng.

#### 3. Vì sao lưu trữ ngày giờ dạng `TEXT` (`yyyy-MM-dd` và `yyyy-MM-dd HH:mm`)?
SQLite không có kiểu dữ liệu `DATE` hay `DATETIME` riêng mà sử dụng cơ chế Dynamic Typing với 5 kiểu cơ bản (NULL, INTEGER, REAL, TEXT, BLOB).
Định dạng chuỗi chuẩn ISO-8601 được lựa chọn vì 3 lý do kỹ thuật:
1. **Khả năng so sánh và sắp xếp tự nhiên:** Định dạng chuỗi `yyyy-MM-dd` có thứ tự sắp xếp từ lớn đến nhỏ (năm $\rightarrow$ tháng $\rightarrow$ ngày). Do đó, phép so sánh từ vựng (lexicographical comparison) thông thường trùng khớp hoàn toàn với thứ tự thời gian (`"2026-03-01" < "2026-06-30"`). Ta có thể dùng trực tiếp toán tử `<`, `>`, `=` và `ORDER BY thoi_gian ASC` trong SQL mà không cần chuyển đổi.
2. **Dễ đọc, gỡ lỗi và đồng bộ:** Dữ liệu khi xem bằng DB Browser for SQLite hoặc xuất ra JSON hoàn toàn trực quan, không bị phụ thuộc vào định dạng epoch millisecond khó đọc đối với con người.
3. **Tương thích hoàn hảo với các hàm thời gian của SQLite:** SQLite hỗ trợ sẵn các hàm `date()`, `time()`, `datetime()`, `strftime()` thao tác trực tiếp trên chuỗi định dạng ISO này.

#### 4. Vì sao tính tiến độ bằng truy vấn thay vì lưu cứng cột `tien_do` trong bảng `DoAn`?
Nếu thêm cột `tien_do` (hoặc `phan_tram`) vào bảng `DoAn`:
* Mỗi khi thêm một nhiệm vụ, xóa một nhiệm vụ, hoặc người dùng chạm Checkbox để đổi trạng thái một nhiệm vụ, ta bắt buộc phải nhớ cập nhật lại bảng `DoAn`.
* Nếu có bất kỳ lỗi logic nào xảy ra khiến việc cập nhật bảng `DoAn` thất bại, dữ liệu giữa bảng `DoAn` và bảng `NhiemVu` sẽ bị **mất đồng bộ (inconsistent state)**.
* **Giải pháp tối ưu:** Tiến độ là **dữ liệu phái sinh (derived data)**. Ứng dụng sử dụng câu truy vấn `LEFT JOIN` kết hợp hàm gom nhóm `COUNT(n.id)` và `IFNULL(SUM(n.da_hoan_thanh), 0)` để tính toán động tại thời điểm truy vấn. Bằng cách này, tiến độ luôn phản ánh đúng 100% sự thật trạng thái của các nhiệm vụ trong CSDL mà không có nguy cơ lệch pha dữ liệu.

#### 5. Vì sao chọn SQLite thuần (`SQLiteOpenHelper`) thay vì thư viện Room?
Đồ án lựa chọn sử dụng SQLite thuần theo đúng định hướng môn học:
1. **Nắm vững bản chất cốt lõi của Android OS:** Giúp sinh viên hiểu sâu sắc về kiến trúc CSDL nhúng, vòng đời của `SQLiteOpenHelper` (`onCreate`, `onUpgrade`, `onConfigure`), cách quản lý con trỏ `Cursor` và cú pháp SQL tiêu chuẩn mà không bị che khuất bởi các lớp trừu tượng (abstraction layer).
2. **Không làm phình to dung lượng ứng dụng:** Thư viện Room yêu cầu Annotation Processor (KSP/kapt) và nhiều thư viện bổ trợ runtime, làm tăng thời gian build và kích thước file APK. Sử dụng SQLite thuần giữ cho dự án nhẹ, độc lập và dễ bảo trì trên mọi môi trường biên dịch.

---

## 4. THIẾT KẾ GIAO DIỆN NGƯỜI DÙNG (UI/UX)

### 4.1. Danh sách màn hình và các thành phần giao diện
Ứng dụng gồm **8 Activity**, mỗi Activity đảm nhận đúng một nhiệm vụ (thêm/sửa luôn mở sang một màn hình Form riêng).

| STT | Activity | Layout | Chức năng chính |
| :---: | :--- | :--- | :--- |
| 1 | `MainActivity` | `activity_main.xml`, `row_do_an.xml` | Danh sách đồ án kèm thanh tiến độ; chuyển chế độ **Danh sách / Lưới 2 cột**; menu ⋮ **Nạp dữ liệu mẫu**; FAB thêm đồ án |
| 2 | `DoAnFormActivity` | `activity_do_an_form.xml` | Thêm / sửa đồ án; chọn ngày bằng `DatePickerDialog` |
| 3 | `DoAnDetailActivity` | `activity_do_an_detail.xml`, `row_thong_ke.xml` | Thông tin đồ án, thanh tiến độ tổng, **RecyclerView cuộn ngang** 4 thẻ thống kê; nút sang Nhiệm vụ / Cuộc hẹn / Dòng thời gian; sửa, xóa đồ án |
| 4 | `NhiemVuListActivity` | `activity_nhiem_vu_list.xml`, `row_nhiem_vu.xml` | Danh sách nhiệm vụ (chưa xong lên trước), CheckBox đánh dấu hoàn thành, sửa / xóa |
| 5 | `NhiemVuFormActivity` | `activity_nhiem_vu_form.xml` | Thêm / sửa nhiệm vụ, kiểm tra hạn nằm trong khoảng ngày bắt đầu – hạn nộp của đồ án |
| 6 | `CuocHenListActivity` | `activity_cuoc_hen_list.xml`, `row_cuoc_hen.xml` | Danh sách cuộc hẹn theo thời gian tăng dần, nhãn *Sắp diễn ra* / *Đã qua* |
| 7 | `CuocHenFormActivity` | `activity_cuoc_hen_form.xml` | Thêm / sửa cuộc hẹn; chọn liên tiếp `DatePickerDialog` → `TimePickerDialog` |
| 8 | `TimelineActivity` | `activity_timeline.xml`, `row_header.xml`, `row_timeline_task.xml`, `row_timeline_meeting.xml` | Dòng thời gian gộp Nhiệm vụ và Lịch hẹn trong **một** RecyclerView nhiều kiểu dòng |

Các thành phần dùng chung:
* `Toolbar` có nút quay lại trên mọi màn hình con.
* `layoutEmpty*`: giao diện trạng thái rỗng (Empty State) khi danh sách chưa có dữ liệu.
* `AlertDialog.Builder`: hộp thoại xác nhận xóa và xác nhận nạp dữ liệu mẫu.
* `Toast`: thông báo nhanh kết quả thao tác và lỗi kiểm tra dữ liệu.
* Các Form được bọc trong `ScrollView` để không bị che khi xoay ngang hoặc khi bàn phím hiện lên.

### 4.2. Luồng điều hướng (Navigation Flow)

```mermaid
flowchart TD
    Start([Khởi động ứng dụng]) --> Main[MainActivity: Danh sách Đồ án]

    Main -->|Menu ⋮ → Nạp dữ liệu mẫu| Confirm[AlertDialog xác nhận]
    Confirm -->|Đồng ý| Reload[DBHelper.napLaiDuLieuMau trong Transaction]
    Reload --> Main

    Main -->|Nút lưới / danh sách| Main
    Main -->|FAB + hoặc nút Sửa| DoAnForm[DoAnFormActivity]
    DoAnForm -->|Lưu → finish| Main
    Main -->|Nút Xóa| DelDoAn[AlertDialog → Xóa + CASCADE]
    DelDoAn --> Main

    Main -->|Chạm vào đồ án| Detail[DoAnDetailActivity]
    Detail --> NVList[NhiemVuListActivity]
    Detail --> CHList[CuocHenListActivity]
    Detail --> Timeline[TimelineActivity]
    Detail -->|Sửa| DoAnForm

    NVList -->|Tick CheckBox| Toggle[Cập nhật SQLite + dòng trượt về đúng nhóm]
    NVList -->|FAB + / Sửa| NVForm[NhiemVuFormActivity]
    CHList -->|FAB + / Sửa| CHForm[CuocHenFormActivity]
    Timeline -->|Chạm vào dòng| NVForm
    Timeline -->|Chạm vào dòng| CHForm
```

### 4.3. Lý do lựa chọn các thành phần giao diện

#### 1. Mỗi chức năng một Activity, truyền dữ liệu bằng `Intent`
* Luồng màn hình tuyến tính, dễ hiểu và bám sát bài giảng (tương tự bài mẫu "Quản lý sinh viên").
* ID đối tượng được truyền qua `intent.putExtra(EXTRA_DO_AN_ID, id)`; tên khóa được khai báo bằng hằng số `public static final String` trong Activity nhận để phía gửi và phía nhận không bao giờ gõ lệch chuỗi.
* Khi Form đóng (`finish()`), màn hình danh sách tự nạp lại dữ liệu mới trong `onResume()`.

#### 2. `RecyclerView` thay cho `ListView`
* Bắt buộc dùng **ViewHolder Pattern**: tái sử dụng View đã cuộn khỏi màn hình, giảm số lần gọi `findViewById`.
* `LayoutManager` linh hoạt: dự án dùng cả `LinearLayoutManager` dọc (danh sách), `GridLayoutManager` 2 cột (chế độ lưới ở màn hình chính, lựa chọn được lưu bằng `SharedPreferences`) và `LinearLayoutManager.HORIZONTAL` (thẻ thống kê).
* `ItemAnimator` có sẵn hiệu ứng thêm / xóa / di chuyển dòng.

#### 3. Nhiều kiểu dòng (Multiple View Types) cho Dòng thời gian
* `TimelineAdapter` nhận `List<Object>` gồm `String` (tiêu đề nhóm), `NhiemVu` và `CuocHen`; `getItemViewType()` dùng `instanceof` để chọn layout tương ứng.
* Hiển thị hai loại dữ liệu khác nhau trên một danh sách liền mạch mà không cần lồng nhiều RecyclerView.

#### 4. `DatePickerDialog` / `TimePickerDialog`
* Người dùng không phải tự gõ ngày, tránh sai định dạng. Giá trị trả về được định dạng ngay thành `yyyy-MM-dd` hoặc `yyyy-MM-dd HH:mm` bằng `String.format(Locale.US, ...)` (dùng `Locale.US` để chữ số luôn là 0–9 trên mọi ngôn ngữ máy, giữ đúng phép so sánh chuỗi).
* Khi sửa, hàm dùng chung `DBHelper.chuoiSangLich()` đổi chuỗi ngày giờ đang lưu thành `Calendar`, nên bộ chọn mở sẵn đúng ngày giờ cũ thay vì ngày hôm nay; người dùng chỉ cần chỉnh phần muốn đổi.

#### 5. `findViewById` + `setOnClickListener`
* Đúng kỹ thuật được dạy trên lớp, không cần bật thêm tính năng build, dễ giải thích từng dòng khi vấn đáp.

#### 6. Material Components (Theme `Material3.DayNight`)
* `MaterialCardView`, `FloatingActionButton`, `Toolbar` cho giao diện hiện đại, tự hỗ trợ chế độ tối (`values-night/themes.xml`).

---

## 5. THIẾT KẾ KIẾN TRÚC VÀ KỸ THUẬT LẬP TRÌNH

### 5.1. Cấu trúc mã nguồn

```
app/src/main/java/com/example/app/      (package phẳng, không chia tầng)
├── DBHelper.java              // SQLiteOpenHelper: tạo bảng, dữ liệu mẫu, CRUD 3 bảng, thống kê
├── DoAn.java                  // Model Đồ án (+ tongNhiemVu, soNhiemVuXong để vẽ tiến độ)
├── NhiemVu.java               // Model Nhiệm vụ
├── CuocHen.java               // Model Cuộc hẹn
├── ThongKeItem.java           // Model thẻ thống kê (tiêu đề, giá trị, icon)
├── MainActivity.java          // Danh sách đồ án, chuyển list/grid, menu nạp dữ liệu mẫu
├── DoAnAdapter.java
├── DoAnFormActivity.java
├── DoAnDetailActivity.java    // Chi tiết + RecyclerView ngang thống kê
├── ThongKeAdapter.java
├── NhiemVuListActivity.java
├── NhiemVuAdapter.java        // CheckBox hoàn thành + gạch ngang
├── NhiemVuFormActivity.java
├── CuocHenListActivity.java
├── CuocHenAdapter.java        // Nhãn Sắp diễn ra / Đã qua
├── CuocHenFormActivity.java
├── TimelineActivity.java
└── TimelineAdapter.java       // 3 kiểu dòng: HEADER / TASK / MEETING

app/src/main/res/
├── layout/   8 layout màn hình + 7 layout dòng (row_*.xml)
├── drawable/ icon vector, nền badge, progress drawable
├── menu/menu_main.xml         // Nút chuyển list/grid + mục "Nạp dữ liệu mẫu"
└── values/, values-night/     // colors, strings, themes (sáng / tối)
```

**Lý do chọn cấu trúc phẳng:** dự án có quy mô vừa (18 lớp), một package duy nhất giúp người đọc thấy toàn bộ mã nguồn ngay lập tức, bám sát bài mẫu trên lớp và dễ trình bày khi bảo vệ. Toàn bộ truy cập CSDL tập trung trong một lớp `DBHelper`, nên các Activity không chứa câu SQL nào.

### 5.2. Các kỹ thuật lập trình then chốt (trích mã nguồn thực tế)

#### 1. Bật khóa ngoại để `ON DELETE CASCADE` hoạt động
```java
// DBHelper.java
@Override
public void onConfigure(SQLiteDatabase db) {
    super.onConfigure(db);
    db.setForeignKeyConstraintsEnabled(true);
}
```
SQLite mặc định **tắt** kiểm tra khóa ngoại; nếu không bật, xóa đồ án sẽ để lại nhiệm vụ và cuộc hẹn "mồ côi".

#### 2. Tính tiến độ của tất cả đồ án bằng một truy vấn `LEFT JOIN`
```java
// DBHelper.getAllDoAn()
String sql = "SELECT d.id, d.ten, d.mo_ta, d.giang_vien, d.ngay_bat_dau, d.han_nop, " +
        "COUNT(n.id) AS tong, " +
        "IFNULL(SUM(n.da_hoan_thanh), 0) AS xong " +
        "FROM DoAn d " +
        "LEFT JOIN NhiemVu n ON d.id = n.do_an_id " +
        "GROUP BY d.id " +
        "ORDER BY d.id DESC";
```
* Một lần gọi CSDL cho toàn bộ danh sách (không lặp `N + 1` truy vấn).
* `LEFT JOIN` giữ lại đồ án chưa có nhiệm vụ; `IFNULL(..., 0)` tránh giá trị `NULL` khi `SUM` trên tập rỗng.

#### 3. Sắp xếp nhiệm vụ: chưa xong lên trước, đã xong xuống dưới
```java
// DBHelper.getNhiemVuByDoAnId()
"ORDER BY da_hoan_thanh ASC, " +
"CASE WHEN han_hoan_thanh IS NULL OR han_hoan_thanh = '' THEN 1 ELSE 0 END, " +
"han_hoan_thanh ASC, id ASC"
```
* Trong mỗi nhóm, việc có hạn gần nhất đứng đầu; việc chưa đặt hạn xếp cuối nhóm.
* Sắp xếp ngay trong SQL nên cả `NhiemVuListActivity` và `TimelineActivity` cùng dùng chung thứ tự này.
* Khi người dùng tick CheckBox, `NhiemVuListActivity.onToggleStatus()` ghi CSDL, đọc lại danh sách đã sắp xếp, tìm vị trí mới của nhiệm vụ và gọi `notifyItemMoved(viTriCu, viTriMoi)`. Dòng trượt về đúng nhóm có hiệu ứng mà không phải vẽ lại toàn bộ danh sách.

#### 4. Ràng buộc ngày bắt đầu ≤ hạn nhiệm vụ ≤ hạn nộp đồ án (kiểm tra hai chiều)
```java
// NhiemVuFormActivity.luuNhiemVu()
if (!hanHoanThanh.isEmpty()) {
    DoAn doAn = dbHelper.getDoAnById(doAnId);
    String ngayBatDauDoAn = ...; // doAn.getNgayBatDau()
    String hanNopDoAn = ...;     // doAn.getHanNop()
    if (!ngayBatDauDoAn.isEmpty() && hanHoanThanh.compareTo(ngayBatDauDoAn) < 0) {
        Toast.makeText(this, "Hạn hoàn thành không được trước ngày bắt đầu đồ án (" + ngayBatDauDoAn + ")!", Toast.LENGTH_LONG).show();
        return;
    }
    if (!hanNopDoAn.isEmpty() && hanHoanThanh.compareTo(hanNopDoAn) > 0) {
        Toast.makeText(this, "Hạn hoàn thành không được sau hạn nộp đồ án (" + hanNopDoAn + ")!", Toast.LENGTH_LONG).show();
        return;
    }
}
```
```java
// DoAnFormActivity.luuDoAn() – khi sửa đồ án, không cho dời hai đầu mốc vượt qua các nhiệm vụ đã có
String hanNhiemVuMuonNhat = dbHelper.getHanNhiemVuMuonNhat(doAnId); // SELECT MAX(han_hoan_thanh) ...
if (hanNhiemVuMuonNhat != null && hanNop.compareTo(hanNhiemVuMuonNhat) < 0) { ... return; }
String hanNhiemVuSomNhat = dbHelper.getHanNhiemVuSomNhat(doAnId);   // SELECT MIN(han_hoan_thanh) ...
if (hanNhiemVuSomNhat != null && ngayBatDau.compareTo(hanNhiemVuSomNhat) > 0) { ... return; }
```
Nếu chỉ kiểm tra ở Form nhiệm vụ, người dùng vẫn có thể phá vỡ ràng buộc bằng cách sửa ngày bắt đầu hoặc hạn nộp của đồ án. Vì vậy ràng buộc được kiểm tra ở cả hai phía. Phép so sánh chuỗi `compareTo` hợp lệ nhờ định dạng `yyyy-MM-dd` (mục 3.3.3).

#### 5. Nạp lại dữ liệu mẫu an toàn bằng Transaction
```java
// DBHelper.napLaiDuLieuMau()
SQLiteDatabase db = this.getWritableDatabase();
db.beginTransaction();
try {
    db.execSQL("DELETE FROM CuocHen");
    db.execSQL("DELETE FROM NhiemVu");
    db.execSQL("DELETE FROM DoAn");
    db.execSQL("DELETE FROM sqlite_sequence WHERE name IN ('DoAn', 'NhiemVu', 'CuocHen')");
    chenDuLieuMau(db);
    db.setTransactionSuccessful();
} finally {
    db.endTransaction();
}
```
* Hàm `chenDuLieuMau(db)` được dùng chung cho `onCreate` (lần cài đầu tiên) và cho menu, nên chỉ có **một** nguồn dữ liệu mẫu trong mã.
* Nếu có lỗi giữa chừng, `setTransactionSuccessful()` không được gọi và CSDL quay về trạng thái cũ, không bao giờ bị "xóa một nửa".
* Vì thao tác này xóa dữ liệu người dùng, `MainActivity` luôn hỏi xác nhận bằng `AlertDialog` trước khi thực hiện.
* Xóa `sqlite_sequence` để bộ đếm `AUTOINCREMENT` bắt đầu lại sau id của dữ liệu mẫu.

#### 6. Truyền tham số bằng dấu `?` (chống SQL Injection)
```java
db.execSQL("UPDATE NhiemVu SET da_hoan_thanh = ? WHERE id = ?", new Object[]{daHoanThanh, id});
db.rawQuery("SELECT ... FROM NhiemVu WHERE do_an_id = ?", new String[]{String.valueOf(doAnId)});
```
Dữ liệu người dùng nhập có dấu nháy `'` (ví dụ "Báo cáo 'giữa kỳ'") vẫn được lưu đúng, không gây lỗi cú pháp SQL.

#### 7. Hàm dùng chung xác định cuộc hẹn đã qua
```java
private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

public static boolean laDaQua(String thoiGian) {
    if (thoiGian == null || thoiGian.trim().isEmpty()) return false;
    String currentDateTime = DATE_TIME_FORMAT.format(new Date());
    return thoiGian.trim().compareTo(currentDateTime) < 0;
}
```
`CuocHenAdapter` và `TimelineAdapter` cùng gọi hàm này nên nhãn *Sắp diễn ra* / *Đã qua* luôn thống nhất giữa các màn hình.

#### 8. Lấy vị trí an toàn trong ViewHolder
```java
int position = getAdapterPosition();
if (position == RecyclerView.NO_POSITION) return;
```
Khi dòng đang chạy hiệu ứng xóa, vị trí có thể là `-1`; việc kiểm tra giúp tránh `IndexOutOfBoundsException`.

---

## 6. GIẢI THÍCH LÝ DO LỰA CHỌN CÁCH PHÂN TÍCH, THIẾT KẾ VÀ LẬP TRÌNH

Chương này tổng hợp lại toàn bộ các quyết định quan trọng của đồ án theo ba giai đoạn: phân tích, thiết kế và lập trình. Với mỗi quyết định, báo cáo nêu phương án đã chọn, các phương án khác đã cân nhắc và lý do lựa chọn, để người đọc thấy mỗi chi tiết trong sản phẩm đều có chủ đích chứ không phải ngẫu nhiên.

### 6.1. Lý do chọn cách phân tích bài toán

#### 1. Xuất phát từ nhu cầu thực tế của chính sinh viên
Đề tài được chọn từ khó khăn mà sinh viên gặp hằng ngày: làm nhiều đồ án song song, nhiều hạn chót trung gian và nhiều buổi gặp giảng viên hướng dẫn. Vì người viết cũng là người dùng, các yêu cầu được rút ra trực tiếp từ trải nghiệm thật (quên hạn, nhầm lịch hẹn, không biết mình đã làm được bao nhiêu phần trăm), nhờ đó phạm vi chức năng vừa đủ, không thừa, không thiếu.

#### 2. Chỉ có một tác nhân (Actor) và không cần đăng nhập
* Ứng dụng là công cụ **cá nhân**, dữ liệu nằm trên chính điện thoại của sinh viên nên không cần phân quyền hay tài khoản.
* Bỏ đăng nhập giúp người dùng mở ứng dụng là dùng được ngay, đồng thời giữ cho đồ án tập trung vào trọng tâm môn học: SQLite, Activity, Intent và RecyclerView.
* Phương án có máy chủ và đồng bộ đám mây đã được cân nhắc nhưng bị loại vì vượt phạm vi học phần và cần kết nối mạng, trái với mục tiêu hoạt động ngoại tuyến (offline-first).

#### 3. Phân rã bài toán thành ba thực thể Đồ án – Nhiệm vụ – Cuộc hẹn
* Khi mô tả bài toán bằng lời, ba danh từ xuất hiện lặp lại là **đồ án**, **việc cần làm** và **buổi hẹn với giảng viên**. Mỗi danh từ có thuộc tính riêng nên trở thành một thực thể.
* Nhiệm vụ và Cuộc hẹn đều "thuộc về" một Đồ án, do đó quan hệ tự nhiên là 1 – N. Không tách Cuộc hẹn thành một loại Nhiệm vụ vì cuộc hẹn có thuộc tính khác hẳn (giờ hẹn, địa điểm) và không có trạng thái "hoàn thành".

#### 4. Các ràng buộc nghiệp vụ được rút ra từ logic thời gian
* Hạn nộp đồ án phải sau hoặc bằng ngày bắt đầu (một đồ án không thể kết thúc trước khi bắt đầu).
* Hạn của từng nhiệm vụ phải nằm trong khoảng từ ngày bắt đầu đến hạn nộp của đồ án (việc con không thể đến hạn trước khi đồ án bắt đầu, và phải xong trước khi nộp). Ràng buộc này được kiểm tra **hai chiều**, vì người dùng có thể phá vỡ nó từ cả phía nhiệm vụ lẫn phía đồ án.
* Cuộc hẹn được phân loại "Sắp diễn ra" / "Đã qua" bằng cách so sánh với thời điểm hiện tại, vì đó là thông tin sinh viên cần nhất khi mở danh sách lịch hẹn.

#### 5. Tiến độ là dữ liệu phái sinh, không phải dữ liệu nhập
Trong bước phân tích, tiến độ được xác định là kết quả tính ra từ trạng thái các nhiệm vụ chứ không phải thông tin người dùng tự nhập. Quyết định này dẫn thẳng tới thiết kế "tính bằng truy vấn" ở mục 3.3.

#### 6. Có bộ dữ liệu mẫu ngay từ đầu
Một ứng dụng quản lý mà mở ra trống trơn thì rất khó đánh giá. Vì vậy yêu cầu "nạp sẵn dữ liệu mẫu và nạp lại được từ menu" được đưa vào ngay từ giai đoạn phân tích để phục vụ việc minh họa, kiểm thử và chấm bài.

### 6.2. Lý do chọn cách thiết kế cơ sở dữ liệu

| Quyết định | Phương án khác đã cân nhắc | Lý do chọn |
| :--- | :--- | :--- |
| 3 bảng `DoAn`, `NhiemVu`, `CuocHen` | Một bảng phẳng gộp tất cả | Đạt chuẩn 3NF, tránh lặp thông tin đồ án trên từng dòng, tránh bất thường khi thêm/sửa/xóa |
| Khóa ngoại `ON DELETE CASCADE` + `setForeignKeyConstraintsEnabled(true)` | Tự viết 3 lệnh `DELETE` trong Java | CSDL tự đảm bảo toàn vẹn, không để lại dữ liệu mồ côi, ít mã hơn, không thể quên |
| Ngày giờ lưu `TEXT` dạng `yyyy-MM-dd` / `yyyy-MM-dd HH:mm` | Lưu `INTEGER` epoch mili-giây | So sánh chuỗi trùng thứ tự thời gian, dùng được `ORDER BY`, `MAX`, `<`, `>`; dễ đọc khi gỡ lỗi |
| Tiến độ tính bằng `LEFT JOIN` + `COUNT` + `IFNULL(SUM)` | Thêm cột `tien_do` vào bảng `DoAn` | Không bao giờ lệch dữ liệu; một truy vấn cho cả danh sách, tránh N + 1 truy vấn |
| Sắp xếp nhiệm vụ ngay trong SQL (`ORDER BY da_hoan_thanh, ...`) | Sắp xếp bằng `Comparator` trong Java | Một nguồn sắp xếp duy nhất dùng chung cho 2 màn hình, mã Java gọn hơn |
| SQLite thuần qua `SQLiteOpenHelper` | Thư viện Room | Đúng nội dung môn học, hiểu rõ `onCreate`/`onUpgrade`/`Cursor`, không cần thư viện sinh mã |
| Tham số `?` trong mọi câu SQL | Cộng chuỗi trực tiếp | Chống SQL Injection, không lỗi cú pháp khi dữ liệu có dấu nháy `'` |
| Nạp dữ liệu mẫu trong Transaction | Xóa rồi chèn từng lệnh rời | Tính nguyên tử: hoặc thành công hết, hoặc giữ nguyên dữ liệu cũ |

Chi tiết lập luận cho từng quyết định đã được trình bày ở mục 3.3; bảng trên giúp người đọc thấy rõ phương án bị loại và lý do loại.

### 6.3. Lý do chọn cách thiết kế giao diện

| Quyết định | Phương án khác đã cân nhắc | Lý do chọn |
| :--- | :--- | :--- |
| Mỗi chức năng một Activity, thêm/sửa mở Form riêng | Fragment, hoặc hộp thoại nhập liệu | Luồng tuyến tính dễ hiểu, đúng bài giảng; Form có đủ chỗ cho DatePicker và nhiều trường nhập |
| Dùng chung một Form cho Thêm và Sửa | Hai màn hình riêng | Không lặp layout và mã kiểm tra; phân biệt chế độ bằng ID truyền qua `Intent` (−1 là thêm mới) |
| `RecyclerView` cho mọi danh sách | `ListView` | Bắt buộc ViewHolder, đổi bố cục bằng `LayoutManager`, có sẵn hiệu ứng thêm/xóa/di chuyển |
| Chế độ Danh sách / Lưới 2 cột, nhớ bằng `SharedPreferences` | Chỉ một kiểu danh sách | Minh họa `GridLayoutManager`; người dùng chọn kiểu xem hợp với mình và không phải chọn lại |
| Thẻ thống kê cuộn ngang | 4 ô xếp dọc | Tiết kiệm chiều cao màn hình chi tiết, minh họa `LinearLayoutManager.HORIZONTAL` |
| Dòng thời gian nhiều kiểu dòng | Lồng 2 RecyclerView | Một danh sách liền mạch, cuộn mượt, đúng kỹ thuật Multiple View Type trong bài giảng |
| `DatePickerDialog` / `TimePickerDialog` | Ô nhập ngày bằng bàn phím | Người dùng không thể gõ sai định dạng, dữ liệu luôn so sánh được |
| Nhãn màu "Sắp diễn ra" / "Đã qua" | Chỉ hiển thị giờ hẹn | Nhận biết trạng thái bằng mắt ngay lập tức, không phải tự so sánh với ngày hôm nay |
| Hộp thoại xác nhận trước khi xóa và nạp dữ liệu mẫu | Xóa ngay | Thao tác không hoàn tác được nên phải hỏi lại, tránh mất dữ liệu do chạm nhầm |
| Giao diện trạng thái rỗng (Empty State) | Để màn hình trắng | Người dùng hiểu là "chưa có dữ liệu" chứ không phải ứng dụng bị lỗi |
| Material 3 `DayNight` | Theme mặc định | Giao diện hiện đại, tự hỗ trợ chế độ tối |

### 6.4. Lý do chọn kiến trúc và cách tổ chức mã nguồn

#### 1. Cấu trúc package phẳng
* Toàn bộ 18 lớp nằm trong `com.example.app`. Với quy mô này, chia nhiều tầng (MVVM, Repository, DAO) chỉ làm tăng số tệp và lớp trung gian mà không đem lại lợi ích tương xứng.
* Cấu trúc phẳng bám sát bài mẫu trên lớp, người chấm mở thư mục là thấy toàn bộ mã nguồn, sinh viên giải thích được từng tệp.

#### 2. Tập trung mọi câu SQL trong `DBHelper`
* Các Activity chỉ gọi hàm như `getAllDoAn()`, `insertNhiemVu(...)`, không chứa câu SQL nào. Khi cần sửa truy vấn chỉ sửa ở một nơi.
* Đây là cách tách lớp dữ liệu đơn giản nhất mà vẫn giữ được ưu điểm chính của kiến trúc nhiều tầng.

#### 3. Model `implements Serializable` và trường phụ trợ hiển thị
* Các lớp `DoAn`, `NhiemVu`, `CuocHen`, `ThongKeItem` cài đặt `Serializable` theo đúng bước 1 của bài giảng RecyclerView, có thể đưa vào `Intent` hoặc `Bundle` khi cần.
* `DoAn` có thêm `tongNhiemVu` và `soNhiemVuXong` lấy sẵn từ truy vấn `LEFT JOIN`, nên `DoAnAdapter` vẽ thanh tiến độ mà không phải truy vấn CSDL trong lúc cuộn.

#### 4. Truyền ID qua `Intent` thay vì truyền cả đối tượng
* Màn hình nhận luôn đọc lại bản ghi mới nhất từ CSDL theo ID, tránh hiển thị dữ liệu cũ khi đối tượng đã bị sửa ở màn hình khác.
* Tên khóa là hằng số `public static final String EXTRA_...` nên phía gửi và phía nhận không thể gõ lệch chuỗi.

#### 5. Làm mới dữ liệu trong `onResume()`
Khi Form gọi `finish()`, màn hình danh sách không chạy lại `onCreate()` mà chạy `onResume()`. Nạp lại dữ liệu ở đây giúp danh sách luôn đúng sau khi thêm/sửa mà không cần cơ chế trả kết quả phức tạp như `startActivityForResult`.

#### 6. Giao tiếp Adapter → Activity bằng interface
Adapter chỉ lo hiển thị; khi người dùng bấm, Adapter gọi interface (ví dụ `OnDoAnClickListener`) để Activity xử lý (mở màn hình, xóa CSDL). Nhờ vậy Adapter không phụ thuộc vào `DBHelper` hay `Intent`, đúng nguyên tắc mỗi lớp một trách nhiệm.

### 6.5. Lý do chọn các kỹ thuật lập trình chi tiết

| Vấn đề | Cách xử lý trong dự án | Lý do |
| :--- | :--- | :--- |
| Lấy vị trí dòng khi bấm | `getAdapterPosition()` kèm kiểm tra `NO_POSITION` | Khi dòng đang chạy hiệu ứng xóa, vị trí có thể là −1; kiểm tra để không bị `IndexOutOfBoundsException` |
| Nhấn giữ một dòng | `onLongClick` trả về `true` | Báo hệ thống sự kiện đã được xử lý, tránh vừa hiện hộp thoại xóa vừa mở màn hình chi tiết |
| Kiểu dòng không hợp lệ | Ném `IllegalArgumentException` thay vì `return null` | RecyclerView không chấp nhận ViewHolder `null`; lỗi rõ ràng giúp phát hiện sai sót sớm |
| Tick hoàn thành nhiệm vụ | Ghi CSDL, đọc lại danh sách đã sắp xếp, gọi `notifyItemMoved` | Dòng trượt mượt về đúng nhóm, không vẽ lại toàn bộ; tắt `setSupportsChangeAnimations` để không nhấp nháy |
| Xóa một dòng | Xóa CSDL rồi `removeItem(position)` → `notifyItemRemoved` | Có hiệu ứng xóa của `DefaultItemAnimator`, chỉ vẽ lại phần thay đổi |
| Xác định cuộc hẹn đã qua | Hàm tĩnh dùng chung `DBHelper.laDaQua()`, một `SimpleDateFormat` dùng lại | Hai Adapter cho kết quả thống nhất; không tạo đối tượng định dạng mới ở mỗi dòng |
| Mở bộ chọn ngày khi sửa | Hàm dùng chung `DBHelper.chuoiSangLich()` đổi chuỗi đang lưu thành `Calendar` | Bộ chọn mở đúng ngày giờ cũ; tránh việc người dùng lỡ bấm OK làm ngày bị đổi thành hôm nay |
| Định dạng ngày | `String.format(Locale.US, "%04d-%02d-%02d", ...)` | Luôn ra chữ số 0–9 với số 0 đệm đầu, giữ đúng phép so sánh và sắp xếp chuỗi trên mọi ngôn ngữ máy |
| Làm tròn phần trăm tiến độ | `Math.round(xong * 100.0 / tong)` ở cả `DoAnAdapter` và `DBHelper.getTienDoDoAn()` | Danh sách và màn hình chi tiết luôn hiện cùng một con số (2/3 là 67% ở cả hai nơi) |
| Tiến độ khi chưa có nhiệm vụ | `IFNULL(SUM(...), 0)` và trả về `0/0 (0%)` | `SUM` trên tập rỗng trả `NULL`; xử lý để không lỗi và không chia cho 0 |
| Chuỗi giao diện | Đặt trong `res/values/strings.xml` | Sửa tập trung, sẵn sàng đa ngôn ngữ |
| Xoay màn hình | `android:configChanges` + bọc Form trong `ScrollView` | Không mất dữ liệu đang nhập, giao diện không bị che khi màn hình thấp |
| Nạp lại dữ liệu mẫu | Transaction + dùng chung `chenDuLieuMau(db)` với `onCreate` | Không bao giờ "xóa một nửa"; chỉ có một nguồn dữ liệu mẫu trong mã |

### 6.6. Hai điểm cố ý làm khác so với mã mẫu trong bài giảng
1. **`onCreateViewHolder` của `TimelineAdapter`:** mã mẫu kết thúc bằng `return null;`. Nếu chạy tới nhánh này, RecyclerView sẽ văng lỗi khó truy vết. Dự án thay bằng `throw new IllegalArgumentException("Kiểu dòng không hợp lệ: " + viewType);` để báo lỗi rõ ràng.
2. **Giá trị trả về của `onLongClick`:** mã mẫu trả về `false`, khiến sự kiện `onClick` vẫn chạy sau khi nhấc tay. Dự án trả về `true` để tiêu thụ hẳn sự kiện nhấn giữ.

Cả hai thay đổi đều nhỏ nhưng thể hiện việc hiểu cơ chế bên trong của RecyclerView và hệ thống sự kiện chạm của Android, thay vì chép nguyên mã mẫu.

---

## 7. KIỂM THỬ VÀ ĐÁNH GIÁ

### 7.1. Kiểm tra truy vấn trên bộ dữ liệu mẫu
Các truy vấn mới được chạy thử trên SQLite với đúng bộ dữ liệu mẫu (4 đồ án, 14 nhiệm vụ, 7 cuộc hẹn):

| Kiểm tra | Kết quả |
| :--- | :--- |
| Số bản ghi nạp được | 4 / 14 / 7, khớp tệp `du_lieu_mau.sql` |
| Nhiệm vụ mẫu có hạn nằm ngoài khoảng ngày bắt đầu – hạn nộp của đồ án | Không có |
| Thứ tự nhiệm vụ Đồ án 1 (id) | `3, 4, 1, 2` (2 việc chưa xong theo hạn, rồi 2 việc đã xong) |
| Thứ tự nhiệm vụ Đồ án 2 (id) | `6, 7, 5` |
| Tiến độ 4 đồ án | 50%, 33%, 100%, 0% |

### 7.2. Bảng kiểm thử thao tác (Manual Testing Checklist)

| STT | Kịch bản kiểm thử | Thao tác thực hiện | Kết quả mong đợi | Đánh giá |
| :---: | :--- | :--- | :--- | :---: |
| 1 | Khởi động lần đầu | Cài mới và mở ứng dụng | Hiển thị sẵn 4 đồ án mẫu với tiến độ 50%, 33%, 100%, 0% | `[ ]` |
| 2 | Nạp lại dữ liệu mẫu | Menu ⋮ → "Nạp dữ liệu mẫu" → "Nạp dữ liệu" | Hỏi xác nhận; dữ liệu cũ bị thay bằng 4 đồ án mẫu | `[ ]` |
| 3 | Hủy nạp dữ liệu mẫu | Menu ⋮ → "Nạp dữ liệu mẫu" → "Hủy" | Dữ liệu hiện tại giữ nguyên | `[ ]` |
| 4 | Ràng buộc ngày đồ án | Thêm đồ án có hạn nộp trước ngày bắt đầu | Báo lỗi, không cho lưu | `[ ]` |
| 5 | Ràng buộc hạn nhiệm vụ | Đồ án 2 (hạn 2026-11-20) → thêm nhiệm vụ hạn 2026-12-01 | Báo lỗi "Hạn hoàn thành không được sau hạn nộp đồ án", không lưu | `[ ]` |
| 6 | Ràng buộc chiều ngược (hạn nộp) | Sửa Đồ án 1, đổi hạn nộp thành 2026-12-01 (nhiệm vụ muộn nhất 2026-12-25) | Báo lỗi, không cho lưu | `[ ]` |
| 7 | Hạn nhiệm vụ trước ngày bắt đầu | Đồ án 2 (bắt đầu 2026-08-15) → thêm nhiệm vụ hạn 2026-08-01 | Báo lỗi "Hạn hoàn thành không được trước ngày bắt đầu đồ án", không lưu | `[ ]` |
| 8 | Ràng buộc chiều ngược (ngày bắt đầu) | Sửa Đồ án 1, đổi ngày bắt đầu thành 2026-10-01 (nhiệm vụ sớm nhất 2026-09-20) | Báo lỗi, không cho lưu | `[ ]` |
| 9 | Sắp xếp nhiệm vụ | Mở nhiệm vụ Đồ án 1 | Hai việc chưa xong ở trên, hai việc đã xong (gạch ngang) ở dưới | `[ ]` |
| 10 | Tick hoàn thành | Tick một việc chưa xong | Dòng gạch ngang và trượt xuống nhóm đã xong; quay lại chi tiết thấy % tăng, cùng con số với danh sách | `[ ]` |
| 11 | Phân loại cuộc hẹn | Mở danh sách cuộc hẹn Đồ án 1 | Cuộc hẹn quá khứ nhãn "Đã qua", tương lai nhãn "Sắp diễn ra" | `[ ]` |
| 12 | Bộ chọn ngày khi sửa | Sửa một cuộc hẹn đã có → bấm chọn ngày giờ | Hộp chọn mở sẵn đúng ngày giờ đã lưu, không phải hôm nay | `[ ]` |
| 13 | Xóa xếp tầng | Xóa một đồ án | Nhiệm vụ và cuộc hẹn của đồ án đó biến mất | `[ ]` |
| 14 | Chuyển lưới / danh sách | Bấm nút trên Toolbar, thoát và mở lại app | Chế độ xem được giữ nguyên | `[ ]` |
| 15 | Xoay màn hình | Đang nhập dở Form → xoay ngang | Dữ liệu đang nhập không mất | `[ ]` |

*(Đánh dấu kết quả sau khi chạy APK trên máy ảo hoặc điện thoại thật.)*

---

## 8. KẾT QUẢ ĐẠT ĐƯỢC, HẠN CHẾ VÀ HƯỚNG PHÁT TRIỂN

### 8.1. Kết quả đạt được
* Ứng dụng Android hoàn chỉnh viết bằng Java, dùng SQLite thuần qua `SQLiteOpenHelper`, không dùng thư viện ngoài ngoài AndroidX/Material.
* Đủ CRUD cho 3 bảng có quan hệ 1–N, xóa xếp tầng, tiến độ tính động, ràng buộc ngày tháng hai chiều.
* Ứng dụng nhiều kỹ thuật RecyclerView: danh sách dọc, lưới, cuộn ngang, nhiều kiểu dòng, `notifyItemInserted/Removed/Moved`.
* Bộ dữ liệu mẫu 25 bản ghi có sẵn khi cài và nạp lại được bất cứ lúc nào để minh họa.

### 8.2. Các hạn chế đã biết
1. **`onUpgrade` xóa bảng rồi tạo lại:** tiện cho giai đoạn phát triển nhưng sẽ mất dữ liệu nếu tăng `DATABASE_VERSION`. Sản phẩm thực tế cần migration bằng `ALTER TABLE`.
2. **Xoay màn hình bằng `android:configChanges`:** Activity không bị tạo lại nên dữ liệu đang nhập được giữ, nhưng đây là cách làm tắt. Cách chuẩn hơn là lưu trạng thái bằng `onSaveInstanceState` hoặc `ViewModel`.
3. **Truy vấn CSDL chạy trên luồng giao diện:** chấp nhận được với dữ liệu nhỏ; nếu dữ liệu lớn cần chuyển sang luồng nền (`ExecutorService`).
4. **Nhãn trạng thái cuộc hẹn** chỉ được tính lại khi danh sách được nạp lại hoặc cuộn, không tự đổi khi để màn hình mở qua giờ hẹn.
5. **Chưa có kiểm thử tự động** cho CSDL và giao diện; các ca kiểm thử ở mục 7.2 được thực hiện thủ công.

### 8.3. Hướng phát triển
* Nhắc lịch hẹn bằng thông báo (`AlarmManager` / `WorkManager`).
* Tìm kiếm đồ án (`SearchView`) và lọc nhiệm vụ theo trạng thái / hạn chót.
* Sao lưu / phục hồi dữ liệu ra tệp JSON.

---

## 9. PHỤ LỤC

### 9.1. Hướng dẫn cài đặt APK
1. Tệp nộp bài: `deliverables/ung_dung_theo_doi_do_an.apk` (bản debug, cài được ngay).
2. **Cách 1 (ADB):** `adb install -r deliverables/ung_dung_theo_doi_do_an.apk`
3. **Cách 2 (trực tiếp):** chép tệp APK vào điện thoại, mở bằng Trình quản lý tệp, cho phép "Cài đặt từ nguồn không xác định" rồi bấm *Cài đặt*.
4. Nếu máy đã cài bản cũ, chọn menu ⋮ → **Nạp dữ liệu mẫu** để có đúng bộ dữ liệu minh họa mới nhất.

### 9.2. Cấu trúc thư mục bàn giao (`deliverables/`)
```
deliverables/
├── ung_dung_theo_doi_do_an.apk      // Tệp cài đặt
├── du_lieu_mau/
│   ├── du_lieu_mau.sql             // 25 bản ghi, trùng khớp DBHelper.chenDuLieuMau()
│   └── du_lieu_mau.json            // Cùng dữ liệu ở dạng JSON lồng nhau
├── thiet_ke/                        // Tệp thiết kế
│   ├── AndroidManifest.xml
│   ├── java/com/example/app/       // 18 tệp .java
│   └── res/                        // layout, drawable, menu, values (.xml)
└── bao_cao/
    ├── bao_cao.docx                // Báo cáo này (Word)
    ├── bao_cao.md                  // Báo cáo dạng Markdown
    ├── cau_hoi_van_dap.docx        // Bộ câu hỏi vấn đáp và gợi ý trả lời
    ├── GIAI_THICH_THIET_KE.md      // Giải thích lý do chọn từng quyết định thiết kế
    └── giai_thich_don_gian.md      // Giải thích mã nguồn bằng ngôn ngữ đơn giản
```

### 9.3. Vị trí ảnh chụp màn hình minh họa
* [Chèn ảnh: Màn hình chính chế độ danh sách và chế độ lưới]
* [Chèn ảnh: Menu ⋮ "Nạp dữ liệu mẫu" và hộp thoại xác nhận]
* [Chèn ảnh: Chi tiết đồ án với thẻ thống kê cuộn ngang]
* [Chèn ảnh: Danh sách nhiệm vụ – việc chưa xong ở trên, đã xong ở dưới]
* [Chèn ảnh: Thông báo lỗi khi hạn nhiệm vụ nằm ngoài khoảng ngày bắt đầu – hạn nộp đồ án]
* [Chèn ảnh: Danh sách cuộc hẹn với nhãn Sắp diễn ra / Đã qua]
* [Chèn ảnh: Dòng thời gian (Timeline)]
