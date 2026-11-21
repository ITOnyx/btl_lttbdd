# GIẢI THÍCH LÝ DO CHỌN CÁCH THIẾT KẾ HỆ THỐNG

> **Tài liệu phục vụ Báo cáo Bài tập lớn môn Lập trình Thiết bị Di động (Android - Java)**  
> *Đã đối chiếu và kiểm tra chính xác 100% với toàn bộ mã nguồn thực tế của dự án.*

---

## 1. Thiết kế cơ sở dữ liệu (Database Design)

### 1.1. Tách cấu trúc thành 3 bảng riêng biệt (`DoAn`, `NhiemVu`, `CuocHen`)
* **Lý do thiết kế:**
  * Giữa Đồ án và Nhiệm vụ, cũng như giữa Đồ án và Cuộc hẹn là các mối quan hệ **Một - Nhiều (1 - N)**. Một đồ án có thể bao gồm nhiều công việc cần hoàn thành theo các giai đoạn và nhiều buổi hẹn trao đổi với giảng viên hướng dẫn.
  * Nếu gộp tất cả vào một bảng phẳng duy nhất, các thông tin chung của đồ án (như *tên đồ án, giảng viên hướng dẫn, ngày bắt đầu, hạn nộp*) sẽ bị lặp đi lặp lại trên từng dòng công việc hoặc lịch hẹn. Điều này gây dư thừa dữ liệu (data redundancy) và dẫn đến hiện tượng bất thường khi cập nhật (update anomaly) — sửa tên giảng viên ở một chỗ thì phải duyệt sửa hàng loạt dòng khác.
  * Việc chuẩn hóa thành 3 bảng độc lập giúp dữ liệu gọn gàng, đúng chuẩn quan hệ và dễ mở rộng.

### 1.2. Khóa ngoại kèm ràng buộc xóa dây chuyền (`ON DELETE CASCADE`)
* **Thực thi trong mã nguồn:**
  * Tại `DBHelper.java`, bảng `NhiemVu` và `CuocHen` đều định nghĩa cột khóa ngoại:  
    `do_an_id INTEGER NOT NULL REFERENCES DoAn(id) ON DELETE CASCADE`.
  * Trong phương thức `onConfigure(SQLiteDatabase db)`:
    ```java
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
    ```
* **Lý do thiết kế:**
  * Khi người dùng xóa một đồ án, tất cả nhiệm vụ và cuộc hẹn gắn liền với đồ án đó sẽ tự động được SQLite thu hồi và xóa sạch, tuyệt đối không để lại dữ liệu "mồ côi" (orphaned records) làm phình dung lượng bộ nhớ.
  * SQLite theo mặc định tắt kiểm tra ràng buộc khóa ngoại để tương thích ngược. Do đó, việc kích hoạt tường minh bằng `setForeignKeyConstraintsEnabled(true)` trong `onConfigure` là giải pháp chuẩn xác và tối ưu nhất của Android SQLite framework.

### 1.3. Lưu trữ ngày giờ dạng chuỗi `TEXT` chuẩn ISO (`yyyy-MM-dd` và `yyyy-MM-dd HH:mm`)
* **Thực thi trong mã nguồn:**
  * [Đã kiểm tra `CuocHenFormActivity.java`]: Sử dụng `String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)` cho ngày và `%s %02d:%02d` cho giờ phút. Đảm bảo mọi giá trị số luôn được đệm số `0` ở đầu (ví dụ: `09:05`, `2026-11-18`).
* **Lý do thiết kế:**
  * SQLite không có kiểu dữ liệu ngày giờ chuyên biệt (như `DATETIME` hay `TIMESTAMP` của các hệ quản trị lớn).
  * Định dạng `yyyy-MM-dd HH:mm` có ưu điểm vượt trội: **so sánh thứ tự từ điển (lexicographical comparison) trùng khớp hoàn toàn với thứ tự thời gian tự nhiên**. Nhờ đó, ứng dụng có thể:
    * Dùng câu lệnh `ORDER BY thoi_gian ASC` hoặc `ORDER BY han_hoan_thanh ASC` trực tiếp trong SQL một cách chuẩn xác mà không cần hàm chuyển đổi phức tạp.
    * Kiểm tra trạng thái *"Đã qua"* bằng phép so sánh chuỗi đơn giản: `thoiGian.trim().compareTo(currentDateTime) < 0` trong hàm dùng chung `DBHelper.laDaQua()`.
    * Chuỗi hiển thị thân thiện, dễ đọc trực tiếp khi debug dữ liệu.

### 1.4. Tính toán tiến độ bằng truy vấn động (Dynamic Query), không lưu cứng cột tiến độ
* **Thực thi trong mã nguồn:**
  * Trong `DBHelper.getAllDoAn()`:
    ```sql
    SELECT d.id, d.ten, d.mo_ta, d.giang_vien, d.ngay_bat_dau, d.han_nop,
           COUNT(n.id) AS tong,
           IFNULL(SUM(n.da_hoan_thanh), 0) AS xong
    FROM DoAn d
    LEFT JOIN NhiemVu n ON d.id = n.do_an_id
    GROUP BY d.id
    ORDER BY d.id DESC
    ```
* **Lý do thiết kế:**
  * Tiến độ được định nghĩa bằng số lượng công việc đã hoàn thành trên tổng số công việc.
  * Nếu lưu tiến độ thành một cột riêng trong bảng `DoAn`, mỗi khi người dùng thêm, sửa, xóa hoặc tick chọn hoàn thành một nhiệm vụ, ứng dụng đều phải kích hoạt trigger hoặc viết mã cập nhật lại bảng `DoAn`. Việc này rất dễ gây lệch dữ liệu (inconsistency) nếu xảy ra lỗi giữa chừng.
  * Sử dụng câu lệnh `LEFT JOIN` kết hợp `COUNT(n.id)` và `SUM(n.da_hoan_thanh)` gom nhóm theo `d.id` mang lại hai lợi ích lớn:
    1. **Luôn đảm bảo tính nhất quán:** Số liệu tiến độ hiển thị luôn phản ánh 100% dữ liệu thực tế tại thời điểm truy vấn.
    2. **Tối ưu hiệu năng truy vấn:** Lấy được toàn bộ thông tin đồ án kèm thống kê tiến độ của tất cả đồ án chỉ qua **duy nhất một lần gọi database**, thay vì truy vấn lặp `N + 1` lần trong vòng lặp.
    3. `LEFT JOIN` đảm bảo những đồ án mới tạo (chưa có nhiệm vụ nào) vẫn được hiển thị với kết quả `0/0 (0%)`.

### 1.5. Sử dụng SQLite thuần qua `SQLiteOpenHelper`, không dùng Room ORM
* **Lý do thiết kế:**
  * Bám sát nội dung chương trình học môn Lập trình Thiết bị Di động trên lớp của thầy cô.
  * Giúp sinh viên nắm vững nguyên lý hoạt động cốt lõi của tầng lưu trữ trên Android: vòng đời khởi tạo (`onCreate`), nâng cấp CSDL (`onUpgrade`), con trỏ duyệt bản ghi (`Cursor`), cũng như sự phân định rõ ràng giữa kết nối đọc (`getReadableDatabase`) và kết nối ghi (`getWritableDatabase`).
  * Dễ dàng giải thích từng dòng mã nguồn khi vấn đáp, thuyết minh bảo vệ đồ án.

### 1.6. Truyền tham số an toàn bằng mảng đối tượng (`?`), chống SQL Injection
* **Thực thi trong mã nguồn:**
  * Mọi thao tác thêm/sửa/xóa đều sử dụng dấu hỏi chấm `?`:  
    `db.execSQL(sql, new Object[]{ten, moTa, giangVien, ngayBatDau, hanNop});`  
    `db.rawQuery(sql, new String[]{String.valueOf(id)});`
* **Lý do thiết kế:**
  * Tuyệt đối tránh việc cộng chuỗi thô (`"WHERE id = " + id`). Cách làm này loại bỏ hoàn toàn nguy cơ bị lỗi cú pháp khi dữ liệu người dùng nhập có chứa dấu nháy đơn (`'`), nháy kép (`"`), hoặc các ký tự đặc biệt, đồng thời bảo vệ ứng dụng trước các nguy cơ tấn công lỗ hổng SQL Injection.

---

## 2. Thiết kế kiến trúc mã nguồn (Software Architecture)

### 2.1. Cấu trúc mã nguồn phẳng (Flat Package Structure)
* **Thực thi trong mã nguồn:**
  * Toàn bộ mã nguồn được đặt chung trong package `com.example.app`, bao gồm: `DBHelper`, các lớp thực thể (`DoAn`, `NhiemVu`, `CuocHen`, `ThongKeItem`), các `Activity`, và các `Adapter`.
* **Lý do thiết kế:**
  * Giúp dự án bám sát ví dụ mẫu trong bài giảng của giảng viên (tương tự đồ án quản lý sinh viên kinh điển).
  * Việc sử dụng các thành phần cơ bản (`findViewById`, `setOnClickListener`, `Intent`, `onResume`) giúp mã nguồn trong sáng, trực quan, sinh viên có thể tự tin làm chủ và giải thích chi tiết chức năng của từng file, từng dòng mã khi thầy cô kiểm tra.

### 2.2. Luồng giao diện phân tách rõ ràng theo từng Activity
* **Mô hình luồng:**
  * `MainActivity` (Danh sách đồ án)  
    └── `DoAnDetailActivity` (Chi tiết đồ án & thanh thống kê cuộn ngang)  
        ├── `NhiemVuListActivity` (Quản lý danh sách nhiệm vụ)  
        │    └── `NhiemVuFormActivity` (Thêm / Sửa nhiệm vụ)  
        ├── `CuocHenListActivity` (Quản lý danh sách cuộc hẹn)  
        │    └── `CuocHenFormActivity` (Thêm / Sửa cuộc hẹn)  
        └── `TimelineActivity` (Dòng thời gian tổng hợp đồ án)
* **Lý do thiết kế:**
  * Mỗi Activity đảm nhận một trách nhiệm duy nhất (Single Responsibility Principle ở cấp độ màn hình), giúp luồng điều hướng của người dùng tự nhiên và mạch lạc.
  * Truyền nhận dữ liệu giữa các màn hình bằng `Intent.putExtra()`, sử dụng các hằng số công khai (`public static final String EXTRA_DO_AN_ID = "do_an_id"`,...) ở đầu mỗi Activity để bảo đảm phía gửi và phía nhận luôn đồng bộ, tránh lỗi gõ sai chuỗi (typo).

### 2.3. Lớp Model kế thừa `Serializable` và bổ sung trường hiển thị
* **Lý do thiết kế:**
  * Kế thừa `java.io.Serializable` giúp các đối tượng thực thể có thể đóng gói trực tiếp vào `Intent` hoặc `Bundle` để truyền qua lại giữa các Activity khi cần.
  * Riêng lớp `DoAn` được bổ sung hai trường mở rộng: `tongNhiemVu` và `soNhiemVuXong`. Nhờ đó, `DoAnAdapter` có sẵn dữ liệu để hiển thị thanh tiến độ `ProgressBar` và phần trăm hoàn thành ngay lập tức mà không phải truy vấn lại cơ sở dữ liệu trong quá trình cuộn danh sách, giúp tăng tốc độ render giao diện.

---

## 3. Thiết kế giao diện và RecyclerView (UI & RecyclerView Architecture)

### 3.1. Lựa chọn `RecyclerView` thay thế toàn diện cho `ListView`
* **Lý do thiết kế theo bài giảng:**
  * `RecyclerView` bắt buộc áp dụng mẫu thiết kế **ViewHolder Pattern**: tái sử dụng View đã cuộn khỏi màn hình, loại bỏ thao tác gọi lặp lại hàm tốn kém `findViewById()`.
  * Khả năng tùy biến linh hoạt nhờ `LayoutManager` (dễ dàng chuyển đổi giữa `LinearLayoutManager` dọc, ngang, hoặc `GridLayoutManager`).
  * Tích hợp sẵn `ItemAnimator` hỗ trợ các hiệu ứng thêm, sửa, xóa mượt mà.

### 3.2. Tuân thủ chuẩn 5 bước xây dựng Adapter của bài giảng
1. **Model:** Xây dựng các lớp dữ liệu `DoAn`, `NhiemVu`, `CuocHen`, `ThongKeItem`.
2. **Layout chứa RecyclerView:** Khai báo thẻ `<androidx.recyclerview.widget.RecyclerView>` trong layout Activity.
3. **Layout hàng (Row Layout):** Thiết kế file layout riêng cho một dòng (`row_do_an.xml`, `row_nhiem_vu.xml`, `row_cuoc_hen.xml`, `row_thong_ke.xml`, `row_header.xml`,...).
4. **Custom Adapter:** Kế thừa `RecyclerView.Adapter<VH>` và hiện thực đầy đủ 3 phương thức bắt buộc: `onCreateViewHolder`, `onBindViewHolder`, `getItemCount`.
5. **Thiết lập tại Activity:** Khởi tạo Adapter, gán dữ liệu, gọi `rv.setAdapter()` và `rv.setLayoutManager()`.

### 3.3. Tối ưu Animation với các hàm cập nhật vị trí cụ thể (`notifyItem*`)
* **Thực thi trong mã nguồn:**
  * Mỗi Adapter đều hiện thực các phương thức thao tác danh sách chuẩn slide bài giảng:
    * `addItem(item)` / `addItem(position, item)` ➔ gọi `notifyItemInserted(...)`.
    * `removeItem(position)` / `removeItem(item)` ➔ gọi `notifyItemRemoved(...)`.
    * `replaceItem(position, item)` ➔ gọi `notifyItemChanged(...)`.
* **Lý do thiết kế:**
  * Báo đúng vị trí phần tử thay đổi giúp `DefaultItemAnimator` của `RecyclerView` chạy hiệu ứng chuyển động tự nhiên (như trượt, mờ dần khi xóa) và hệ thống chỉ cần vẽ lại đúng dòng đó thay vì render lại toàn bộ màn hình.
  * Phương thức `notifyDataSetChanged()` chỉ dùng khi người dùng quay lại từ màn hình Form (`onResume`) để nạp lại toàn bộ dữ liệu mới nhất.

### 3.4. Áp dụng Multiple View Types cho màn hình Dòng thời gian (`TimelineActivity`)
* **Thực thi trong mã nguồn:**
  * `TimelineAdapter` quản lý danh sách phần tử kiểu `List<Object>` kết hợp 3 kiểu dòng khác nhau:
    * `HEADER` (Tiêu đề nhóm: `String`) ➔ nạp `row_header.xml`
    * `TASK` (Công việc: `NhiemVu`) ➔ nạp `row_timeline_task.xml`
    * `MEETING` (Lịch hẹn: `CuocHen`) ➔ nạp `row_timeline_meeting.xml`
  * Trong `getItemViewType(int position)`, dùng toán tử `instanceof` để nhận diện kiểu dữ liệu.
  * Trong `onCreateViewHolder` và `onBindViewHolder`, dùng cấu trúc `switch-case` tương ứng với từng kiểu view type.
* **Lý do thiết kế:**
  * Đây là kỹ thuật nâng cao được học trong chương RecyclerView, giải quyết trọn vẹn bài toán thực tế: hợp nhất danh sách nhiệm vụ và lịch hẹn vào một giao diện dòng thời gian trực quan, liền mạch mà không cần phải lồng ghép nhiều RecyclerView phức tạp.

### 3.5. RecyclerView cuộn ngang (`LinearLayoutManager.HORIZONTAL`) cho thẻ Thống kê
* **Thực thi trong mã nguồn:**
  * Trong `DoAnDetailActivity.java`: hiển thị 4 thẻ card thống kê gồm: *Tổng nhiệm vụ, Đã hoàn thành, Còn dang dở, Lịch hẹn sắp tới*.
  * Cấu hình: `rvThongKe.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));`
* **Lý do thiết kế:**
  * Minh họa khả năng linh hoạt của `LayoutManager`.
  * Tiết kiệm không gian chiều dọc màn hình, tạo điểm nhấn giao diện hiện đại và chuyên nghiệp cho trang chi tiết đồ án.

---

## 4. Các quyết định xử lý chi tiết (Chi tiết kỹ thuật cải tiến so với slide)

| STT | Vấn đề kỹ thuật | Cách xử lý trong dự án | Lý do & Ý nghĩa thực tế |
| :---: | :--- | :--- | :--- |
| **1** | **Lấy vị trí phần tử trong ViewHolder** | Dùng `int position = getAdapterPosition();`<br>kèm kiểm tra `if (position == RecyclerView.NO_POSITION) return;` | Vị trí click được lấy chính xác tại thời điểm sự kiện xảy ra. Khi một phần tử đang trong hiệu ứng xóa khỏi danh sách, nếu người dùng bấm nhanh, vị trí có thể trả về `NO_POSITION (-1)`, việc kiểm tra giúp ứng dụng không bị Crash (ngoại lệ `IndexOutOfBoundsException`). |
| **2** | **Xử lý sự kiện nhấn giữ (`onLongClick`)** | Phương thức `onLongClick` luôn trả về giá trị `true` (thay vì `false` như slide) | Trả về `true` báo hiệu cho hệ điều hành Android biết sự kiện nhấn giữ đã được tiêu thụ trọn vẹn, ngăn không cho sự kiện click thông thường tiếp tục kích hoạt khi người dùng thả tay ra (tránh lỗi vừa hiện hộp thoại xóa vừa nhảy sang màn hình sửa). |
| **3** | **Kiểm soát kiểu dòng trong `onCreateViewHolder`** | Ném ngoại lệ `throw new IllegalArgumentException("Kiểu dòng không hợp lệ: " + viewType);` thay vì `return null` | `RecyclerView` không chấp nhận `ViewHolder` mang giá trị `null`. Trả về `null` sẽ dẫn đến lỗi crash ngầm rất khó truy vết; ném lỗi cụ thể giúp lập trình viên phát hiện ngay sai sót trong quá trình phát triển. |
| **4** | **Tương tác CheckBox hoàn thành nhiệm vụ** | Cập nhật trực tiếp thuộc tính của đối tượng trong mảng, gạch ngang chữ (`STRIKE_THRU_TEXT_FLAG`), ghi xuống DB, đọc lại danh sách đã sắp xếp rồi chỉ gọi `notifyItemMoved(viTriCu, viTriMoi)` | Không gọi `notifyItemChanged` vì sẽ làm dòng nhấp nháy (blink); `notifyItemMoved` cho dòng trượt mượt về nhóm "đã xong" / "chưa xong" đúng thứ tự sắp xếp. Trong `TimelineAdapter`, CheckBox được khóa tương tác bằng `setClickable(false)` vì đây là màn hình xem tổng hợp. |
| **5** | **Tối ưu hóa kiểm tra thời gian cuộc hẹn** | Xây dựng hàm tĩnh dùng chung `DBHelper.laDaQua(String thoiGian)`, dùng duy nhất một biến hằng số `SimpleDateFormat` với `Locale.US` | Tránh việc khởi tạo lặp đi lặp lại đối tượng định dạng ngày tháng gây tiêu tốn tài nguyên bộ nhớ; đồng thời đảm bảo kết quả đánh giá trạng thái cuộc hẹn ("Sắp diễn ra" / "Đã qua") luôn đồng nhất tuyệt đối giữa các màn hình. |
| **6** | **Quản lý chuỗi giao diện qua `strings.xml`** | Toàn bộ các chuỗi cố định (nhãn trạng thái, tiền tố deadline, thông báo,...) được đặt trong `res/values/strings.xml` | Chuẩn hóa tài nguyên giao diện, dễ dàng sửa đổi nhãn hiển thị tập trung và tạo tiền đề thuận lợi nếu cần mở rộng đa ngôn ngữ trong tương lai. |
| **7** | **Hỗ trợ xoay màn hình và chống vỡ giao diện** | [Đã kiểm tra `AndroidManifest.xml`]: Bổ sung `android:configChanges="orientation|screenSize|keyboardHidden"`.<br>Bọc toàn bộ nội dung nhập liệu trong thẻ `<ScrollView>` | Giúp Activity không bị hủy và tạo lại khi xoay ngang/dọc thiết bị, giữ nguyên dữ liệu đang nhập dở trên Form mà không cần xử lý `Bundle` phức tạp. Thẻ `ScrollView` đảm bảo giao diện không bao giờ bị tràn hoặc che khuất bàn phím trên các máy màn hình nhỏ hoặc máy tính bảng/giả lập xoay ngang. |
| **8** | **Sắp xếp nhiệm vụ chưa xong lên trước** | `ORDER BY da_hoan_thanh ASC, CASE WHEN han_hoan_thanh IS NULL OR han_hoan_thanh = '' THEN 1 ELSE 0 END, han_hoan_thanh ASC, id ASC` ngay trong `DBHelper.getNhiemVuByDoAnId()` | Sinh viên thấy ngay việc cần làm tiếp theo (hạn gần nhất ở đầu). Sắp xếp trong SQL nên `NhiemVuListActivity` và `TimelineActivity` dùng chung một thứ tự, không phải viết `Comparator` ở Java. |
| **9** | **Ràng buộc hạn nhiệm vụ ≤ hạn nộp đồ án (hai chiều)** | `NhiemVuFormActivity` so sánh hạn nhiệm vụ với `han_nop` của đồ án; `DoAnFormActivity` khi sửa so sánh hạn nộp mới với `DBHelper.getHanNhiemVuMuonNhat()` (`SELECT MAX(han_hoan_thanh)`) | Nếu chỉ kiểm tra một phía, người dùng có thể phá ràng buộc bằng cách sửa đồ án sau khi đã thêm nhiệm vụ. So sánh chuỗi `compareTo` hợp lệ nhờ định dạng `yyyy-MM-dd`. |
| **10** | **Nạp lại dữ liệu mẫu từ menu** | Mục menu ⋮ "Nạp dữ liệu mẫu" → `AlertDialog` xác nhận → `DBHelper.napLaiDuLieuMau()`: xóa 3 bảng + đặt lại `sqlite_sequence` + gọi `chenDuLieuMau(db)` trong **một Transaction** | Dùng chung hàm `chenDuLieuMau` với `onCreate` nên chỉ có một nguồn dữ liệu mẫu. Transaction đảm bảo không bao giờ bị "xóa một nửa"; hộp thoại xác nhận bảo vệ dữ liệu người dùng khỏi thao tác nhầm. |
| **11** | **Định dạng ngày bằng `Locale.US`** | `String.format(Locale.US, "%04d-%02d-%02d", ...)` trong cả 3 Form | Với `Locale.getDefault()`, máy cài ngôn ngữ dùng chữ số khác (ví dụ tiếng Ả Rập) sẽ sinh chuỗi ngày không phải 0–9, làm sai phép so sánh và sắp xếp chuỗi. |

---

## 5. Hạn chế đã biết của hệ thống (Tính trung thực học thuật)

1. **Cơ chế nâng cấp CSDL trong `onUpgrade()`:**
   * Trong `DBHelper.java`, phương thức `onUpgrade()` hiện tại thực hiện lệnh xóa các bảng cũ (`DROP TABLE IF EXISTS`) rồi gọi lại `onCreate(db)`. 
   * Cách tiếp cận này rất thuận tiện trong giai đoạn phát triển và thử nghiệm đồ án (dễ cập nhật cấu trúc bảng và nạp lại dữ liệu mẫu), tuy nhiên trong môi trường sản phẩm thực tế sẽ làm mất dữ liệu người dùng. Nếu nâng cấp phiên bản ứng dụng thực sự, cần thay thế bằng các câu lệnh `ALTER TABLE` để bảo toàn dữ liệu cũ.
2. **Cập nhật trạng thái thời gian thực:**
   * Nhãn *"Đã qua"* và *"Sắp tới"* của cuộc hẹn được tính toán tại thời điểm nạp hoặc vẽ lại dòng dữ liệu (`onBindViewHolder`). Nếu người dùng mở ứng dụng và để nguyên màn hình qua mốc thời gian diễn ra cuộc hẹn, nhãn sẽ không tự động nhảy sang *"Đã qua"* cho đến khi danh sách được cuộn hoặc tải lại.
3. **Hiệu ứng chuyển động (Animation) khi thêm và sửa:**
   * Thao tác thêm mới và cập nhật dữ liệu được chuyển hướng sang một `Activity` Form riêng biệt. Khi hoàn tất và quay trở lại màn hình danh sách, dữ liệu được nạp lại toàn bộ trong hàm `onResume()` thông qua `notifyDataSetChanged()`. Do đó, chỉ có thao tác **xóa phần tử tại chỗ** là thể hiện được trọn vẹn hiệu ứng chuyển động xóa dòng của `RecyclerView`.
4. **Kiểm thử ứng dụng:**
   * Đồ án tập trung vào việc hiện thực hóa các chức năng cốt lõi và giao diện người dùng theo yêu cầu của môn học, chưa thiết lập hệ thống kiểm thử tự động (Unit Test / UI Test bằng Espresso). Toàn bộ các ca kiểm thử chức năng hiện đang được thực hiện thủ công (Manual Testing) theo checklist kịch bản sử dụng.
