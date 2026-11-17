# TÀI LIỆU GIẢI THÍCH CHI TIẾT MÃ NGUỒN – NHÓM D: ADAPTERS
**Thư mục:** `app/src/main/java/com/example/app/adapter/`

---

## TỔNG QUAN NHÓM ADAPTERS
Tầng Adapter là cầu nối dữ liệu và hiển thị danh sách trên `RecyclerView`. Tất cả các Adapter trong dự án đều kế thừa `androidx.recyclerview.widget.ListAdapter` (một thành phần cao cấp của Android Jetpack RecyclerView) kết hợp cùng `DiffUtil.ItemCallback` và `ViewBinding`:
* Khác với `RecyclerView.Adapter` thông thường phải quản lý mảng `List` thủ công và gọi `notifyDataSetChanged()` gây vẽ lại toàn bộ màn hình, `ListAdapter` quản lý danh sách ngầm (Background Thread) và tự động tính toán hiệu số vi sai giữa 2 danh sách cũ - mới.
* Nhờ vậy, RecyclerView chỉ thực hiện diễn hoạt (animation) và cập nhật duy nhất các View thực sự bị biến đổi.

---

## 1. DoAnAdapter.java (`app/src/main/java/com/example/app/adapter/DoAnAdapter.java`)

### 1.1. Vai trò
`DoAnAdapter` chịu trách nhiệm hiển thị danh sách đồ án kèm thanh tiến độ hoàn thành trên `RecyclerView` (`rvDoAn`) tại màn hình chính `MainActivity`. Nó bắt các tương tác của người dùng trên từng card đồ án: nhấn vào card để mở màn hình chi tiết, nhấn biểu tượng Sửa, hoặc nhấn biểu tượng Xóa.

### 1.2. Mối quan hệ
* **Các lớp gọi `DoAnAdapter`:**
  * `MainActivity`: Khởi tạo adapter, gán cho `binding.rvDoAn`, truyền callback `OnDoAnActionListener`, và nạp dữ liệu qua `adapter.submitList(list)`.
* **Lớp mà `DoAnAdapter` gọi:**
  * `ItemDoAnBinding`: Lớp ViewBinding của layout `item_do_an.xml`.
  * `DoAnWithTienDo`, `DoAn`, `TienDoInfo`: Đối tượng dữ liệu để bind vào view.

### 1.3. Giao diện Callback `OnDoAnActionListener`
```java
public interface OnDoAnActionListener {
    void onItemClick(DoAnWithTienDo item);    // Khi chạm vào thẻ đồ án
    void onEditClick(DoAnWithTienDo item);    // Khi chạm nút Sửa (bút chì)
    void onDeleteClick(DoAnWithTienDo item);  // Khi chạm nút Xóa (thùng rác)
}
```

### 1.4. Bảng giải thích chi tiết từng hàm trong `DoAnAdapter.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động (các bước chính) | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `onItemClick(DoAnWithTienDo)` | `DoAnWithTienDo item` $\rightarrow$ `void` | (Interface) Báo sự kiện chạm thẻ đồ án | Định nghĩa hành động mở chi tiết đồ án. | `ViewHolder.bind()` |
| 2 | `onEditClick(DoAnWithTienDo)` | `DoAnWithTienDo item` $\rightarrow$ `void` | (Interface) Báo sự kiện chạm nút sửa | Định nghĩa hành động mở hộp thoại sửa đồ án. | `ViewHolder.bind()` |
| 3 | `onDeleteClick(DoAnWithTienDo)` | `DoAnWithTienDo item` $\rightarrow$ `void` | (Interface) Báo sự kiện chạm nút xóa | Định nghĩa hành động mở dialog xác nhận xóa đồ án. | `ViewHolder.bind()` |
| 4 | `areItemsTheSame(...)` | `DoAnWithTienDo oldItem, DoAnWithTienDo newItem` $\rightarrow$ `boolean` | (DiffUtil) Kiểm tra xem 2 item có cùng trỏ tới 1 bản ghi CSDL không | So sánh khóa chính: `oldItem.getDoAn().getId() == newItem.getDoAn().getId()`. | `DiffUtil` khi `submitList()` |
| 5 | `areContentsTheSame(...)` | `DoAnWithTienDo oldItem, DoAnWithTienDo newItem` $\rightarrow$ `boolean` | (DiffUtil) Kiểm tra nội dung hiển thị có thay đổi không | Gọi `Objects.equals(oldItem, newItem)`. Sử dụng hàm `equals` sâu đã cài đặt ở Model. | `DiffUtil` khi `areItemsTheSame` trả về `true` |
| 6 | `DoAnAdapter(OnDoAnActionListener)` | `OnDoAnActionListener listener` $\rightarrow$ `void` | Constructor khởi tạo Adapter | Gọi `super(DIFF_CALLBACK)` và lưu `this.listener = listener`. | `MainActivity.initViews()` |
| 7 | `onCreateViewHolder(...)` | `ViewGroup parent, int viewType` $\rightarrow$ `ViewHolder` | Tạo mới một ViewHolder khi RecyclerView cần thêm View | **Bước 1:** `ItemDoAnBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)`.<br>**Bước 2:** Trả về `new ViewHolder(binding)`. | `RecyclerView` khi thiếu View hiển thị |
| 8 | `onBindViewHolder(...)` | `ViewHolder holder, int position` $\rightarrow$ `void` | Gắn dữ liệu tại vị trí `position` vào ViewHolder | Lấy item bằng `getItem(position)` của ListAdapter và gọi `holder.bind(item)`. | `RecyclerView` khi hiển thị hoặc cuộn item vào màn hình |
| 9 | `ViewHolder(ItemDoAnBinding)` | `ItemDoAnBinding binding` $\rightarrow$ `void` | Constructor ViewHolder | Gọi `super(binding.getRoot())` và lưu `this.binding = binding`. | `onCreateViewHolder()` |
| 10 | `bind(DoAnWithTienDo)` | `DoAnWithTienDo item` $\rightarrow$ `void` | Điền dữ liệu đồ án và tiến độ lên các View | Xem phân tích chi tiết quy trình 5 bước ở mục 1.5 bên dưới. | `onBindViewHolder()` |

### 1.5. Phân tích chi tiết hàm `ViewHolder.bind(DoAnWithTienDo item)` (dòng 72–135)
1. **Gán tên đồ án (dòng 76):** `binding.tvTen.setText(doAn.getTen());`.
2. **Xử lý mô tả (dòng 78–84):** Nếu có mô tả thì hiện `tvMoTa` và gán nội dung; nếu null hoặc rỗng thì ẩn hoàn toàn (`View.GONE`) để thẻ CardView co giãn gọn gàng.
3. **Xử lý giảng viên hướng dẫn (dòng 86–90):** Nếu có GVHD thì hiển thị `"GVHD: Nguyễn Văn A"`, nếu null/rỗng thì hiển thị `"GVHD: (Chưa phân công)"` thông qua chuỗi định dạng trong `strings.xml`.
4. **Xử lý thời hạn (dòng 92–103):** Nếu có cả ngày bắt đầu và hạn nộp thì hiện `"dd/MM/yyyy → dd/MM/yyyy"`; nếu chỉ có hạn nộp thì hiện `"Hạn nộp: dd/MM/yyyy"`; nếu không có thì hiện `"Chưa đặt hạn nộp"`.
5. **Gán thanh tiến độ ProgressBar và nhãn tỷ lệ (dòng 105–113):** Nếu `tienDo != null`, lấy `percent` đặt cho `progressBar.setProgress(percent)` và đặt chuỗi `"X/Y (Z%)"` cho `tvTienDo`.
6. **Gắn sự kiện Click (dòng 115–134):** Gắn listener cho `itemView` (xem chi tiết), `btnEdit` (sửa) và `btnDelete` (xóa).

---

## 2. NhiemVuAdapter.java (`app/src/main/java/com/example/app/adapter/NhiemVuAdapter.java`)

### 2.1. Vai trò
`NhiemVuAdapter` hiển thị danh sách các đầu mục công việc trực thuộc đồ án trên `RecyclerView` tại `NhiemVuFragment`. Nó quản lý việc tick CheckBox hoàn thành nhiệm vụ, gạch ngang chữ (strikethrough) khi hoàn thành và kích hoạt sửa/xóa nhiệm vụ.

### 2.2. Mối quan hệ
* **Các lớp gọi `NhiemVuAdapter`:**
  * `NhiemVuFragment`: Khởi tạo adapter, gán cho `binding.rvNhiemVu`, nhận callback `OnNhiemVuActionListener` và truyền danh sách qua `submitList(list)`.
* **Lớp mà `NhiemVuAdapter` gọi:**
  * `ItemNhiemVuBinding`: Lớp ViewBinding của `item_nhiem_vu.xml`.
  * `NhiemVu`: Đối tượng thực thể nhiệm vụ.
  * `android.graphics.Paint`: Xử lý cờ gạch ngang chữ `STRIKE_THRU_TEXT_FLAG`.

### 2.3. Giao diện Callback `OnNhiemVuActionListener`
```java
public interface OnNhiemVuActionListener {
    void onStatusChanged(NhiemVu item, boolean isChecked); // Khi người dùng tick/untick CheckBox
    void onEditClick(NhiemVu item);                        // Khi bấm nút Sửa nhiệm vụ
    void onDeleteClick(NhiemVu item);                      // Khi bấm nút Xóa nhiệm vụ
}
```

### 2.4. Bảng giải thích chi tiết từng hàm trong `NhiemVuAdapter.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động (các bước chính) | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `onStatusChanged(...)` | `NhiemVu item, boolean isChecked` $\rightarrow$ `void` | (Interface) Báo trạng thái checkbox đổi | Nhận sự kiện đảo trạng thái hoàn thành. | `ViewHolder.bind()` |
| 2 | `onEditClick(NhiemVu)` | `NhiemVu item` $\rightarrow$ `void` | (Interface) Báo sự kiện sửa việc | Mở dialog sửa nhiệm vụ. | `ViewHolder.bind()` |
| 3 | `onDeleteClick(NhiemVu)` | `NhiemVu item` $\rightarrow$ `void` | (Interface) Báo sự kiện xóa việc | Mở dialog xác nhận xóa nhiệm vụ. | `ViewHolder.bind()` |
| 4 | `areItemsTheSame(...)` | `NhiemVu oldItem, NhiemVu newItem` $\rightarrow$ `boolean` | (DiffUtil) So sánh mã ID nhiệm vụ | `oldItem.getId() == newItem.getId()`. | `DiffUtil` |
| 5 | `areContentsTheSame(...)` | `NhiemVu oldItem, NhiemVu newItem` $\rightarrow$ `boolean` | (DiffUtil) So sánh nội dung chi tiết | `Objects.equals(oldItem, newItem)` (gọi `NhiemVu.equals`). | `DiffUtil` |
| 6 | `NhiemVuAdapter(OnNhiemVuActionListener)` | `OnNhiemVuActionListener listener` $\rightarrow$ `void` | Constructor khởi tạo Adapter | Gọi `super(DIFF_CALLBACK)` và lưu `listener`. | `NhiemVuFragment.initViews()` |
| 7 | `onCreateViewHolder(...)` | `ViewGroup parent, int viewType` $\rightarrow$ `ViewHolder` | Tạo mới ViewHolder cho nhiệm vụ | Inflate `item_nhiem_vu.xml` qua `ItemNhiemVuBinding` và trả về `ViewHolder`. | `RecyclerView` |
| 8 | `onBindViewHolder(...)` | `ViewHolder holder, int position` $\rightarrow$ `void` | Gắn dữ liệu nhiệm vụ vào ViewHolder | Lấy item bằng `getItem(position)` và gọi `holder.bind(item)`. | `RecyclerView` |
| 9 | `ViewHolder(ItemNhiemVuBinding)` | `ItemNhiemVuBinding binding` $\rightarrow$ `void` | Constructor ViewHolder | Gọi `super(binding.getRoot())` và lưu `binding`. | `onCreateViewHolder()` |
| 10 | `bind(NhiemVu)` | `NhiemVu item` $\rightarrow$ `void` | Gắn dữ liệu, xử lý CheckBox và hiệu ứng text | Xem phân tích chi tiết ở mục 2.5 bên dưới. | `onBindViewHolder()` |

### 2.5. Điểm kỹ thuật sống còn: Tháo Listener CheckBox chống lỗi tái sử dụng ViewHolder
Đoạn mã từ dòng 99 đến 106 trong `NhiemVuAdapter.java`:
```java
// CheckBox: tranh loi tai su dung ViewHolder
binding.cbHoanThanh.setOnCheckedChangeListener(null);
binding.cbHoanThanh.setChecked(item.isCompleted());
binding.cbHoanThanh.setOnCheckedChangeListener((buttonView, isChecked) -> {
    if (listener != null) {
        listener.onStatusChanged(item, isChecked);
    }
});
```
* **Bản chất lỗi kinh điển trên Android:** RecyclerView tái sử dụng đối tượng `ViewHolder` cũ khi người dùng cuộn màn hình. Nếu ViewHolder đó đang mang một listener cũ, khi `onBindViewHolder` gọi hàm `setChecked(item.isCompleted())`, lệnh này sẽ kích hoạt listener cũ của phần tử trước đó $\rightarrow$ Dẫn đến việc các CheckBox bị nhảy lung tung hoặc tự động cập nhật sai trạng thái vào CSDL khi người dùng cuộn danh sách.
* **Biện pháp xử lý triệt để:** Bắt buộc phải gán `setOnCheckedChangeListener(null)` trước để vô hiệu hóa listener cũ, sau đó mới gọi `setChecked()`, rồi cuối cùng mới gán lại listener mới cho phần tử hiện tại.
* **Hiệu ứng chữ gạch ngang (dòng 91–97):** Khi `item.isCompleted() == true`, thêm cờ `Paint.STRIKE_THRU_TEXT_FLAG` và đổi màu chữ sang màu xám phụ (`text_secondary`); khi chưa hoàn thành thì xóa cờ gạch ngang bằng phép toán bit `& ~Paint.STRIKE_THRU_TEXT_FLAG` và trả lại màu chữ đen/trắng chính (`text_primary`).

---

## 3. CuocHenAdapter.java (`app/src/main/java/com/example/app/adapter/CuocHenAdapter.java`)

### 3.1. Vai trò
`CuocHenAdapter` hiển thị danh sách lịch hẹn gặp Giảng viên hướng dẫn trên `RecyclerView` tại `CuocHenFragment`. Nhiệm vụ đặc thù của adapter này là so sánh thời gian hẹn với thời gian thực tế hiện tại để tự động gắn nhãn trạng thái trực quan: Badge màu xanh lá *"Sắp diễn ra"* hoặc Badge màu xám *"Đã qua"* (kèm hiệu ứng làm mờ thẻ 65%).

### 3.2. Mối quan hệ
* **Các lớp gọi `CuocHenAdapter`:**
  * `CuocHenFragment`: Khởi tạo adapter, gán cho `binding.rvCuocHen`, nhận callback `OnCuocHenActionListener` và cập nhật danh sách qua `submitList(list)`.
* **Lớp mà `CuocHenAdapter` gọi:**
  * `ItemCuocHenBinding`: Lớp ViewBinding của `item_cuoc_hen.xml`.
  * `CuocHen`: Đối tượng thực thể cuộc hẹn.
  * `DateUtils.isPastDateTime()`: Hàm tiện ích kiểm tra thời gian quá khứ.

### 3.3. Giao diện Callback `OnCuocHenActionListener`
```java
public interface OnCuocHenActionListener {
    void onEditClick(CuocHen item);    // Khi chạm nút Sửa cuộc hẹn
    void onDeleteClick(CuocHen item);  // Khi chạm nút Xóa cuộc hẹn
}
```

### 3.4. Bảng giải thích chi tiết từng hàm trong `CuocHenAdapter.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động (các bước chính) | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `onEditClick(CuocHen)` | `CuocHen item` $\rightarrow$ `void` | (Interface) Báo sự kiện sửa cuộc hẹn | Mở dialog sửa cuộc hẹn. | `ViewHolder.bind()` |
| 2 | `onDeleteClick(CuocHen)` | `CuocHen item` $\rightarrow$ `void` | (Interface) Báo sự kiện xóa cuộc hẹn | Mở dialog xác nhận xóa cuộc hẹn. | `ViewHolder.bind()` |
| 3 | `areItemsTheSame(...)` | `CuocHen oldItem, CuocHen newItem` $\rightarrow$ `boolean` | (DiffUtil) So sánh mã ID cuộc hẹn | `oldItem.getId() == newItem.getId()`. | `DiffUtil` |
| 4 | `areContentsTheSame(...)` | `CuocHen oldItem, CuocHen newItem` $\rightarrow$ `boolean` | (DiffUtil) So sánh nội dung cuộc hẹn | `Objects.equals(oldItem, newItem)` (gọi `CuocHen.equals`). | `DiffUtil` |
| 5 | `CuocHenAdapter(OnCuocHenActionListener)` | `OnCuocHenActionListener listener` $\rightarrow$ `void` | Constructor khởi tạo Adapter | Gọi `super(DIFF_CALLBACK)` và lưu `listener`. | `CuocHenFragment.initViews()` |
| 6 | `onCreateViewHolder(...)` | `ViewGroup parent, int viewType` $\rightarrow$ `ViewHolder` | Tạo mới ViewHolder cho cuộc hẹn | Inflate layout `item_cuoc_hen.xml` qua `ItemCuocHenBinding`. | `RecyclerView` |
| 7 | `onBindViewHolder(...)` | `ViewHolder holder, int position` $\rightarrow$ `void` | Gắn dữ liệu cuộc hẹn vào ViewHolder | Lấy item tại vị trí `position` và gọi `holder.bind(item)`. | `RecyclerView` |
| 8 | `ViewHolder(ItemCuocHenBinding)` | `ItemCuocHenBinding binding` $\rightarrow$ `void` | Constructor ViewHolder | Gọi `super(binding.getRoot())` và lưu `binding`. | `onCreateViewHolder()` |
| 9 | `bind(CuocHen)` | `CuocHen item` $\rightarrow$ `void` | Gắn dữ liệu, địa điểm, ghi chú và gắn Badge trạng thái | Xem phân tích chi tiết ở mục 3.5 bên dưới. | `onBindViewHolder()` |

### 3.5. Phân tích chi tiết hàm `ViewHolder.bind(CuocHen item)` (dòng 71–118)
1. **Tiêu đề và thời gian (dòng 72–73):** `binding.tvTieuDe.setText(item.getTieuDe())` và `binding.tvThoiGian.setText(item.getThoiGian())`.
2. **Xử lý địa điểm (dòng 75–81):** Kiểm tra `item.getDiaDiem()`, nếu có thì hiện cụm `layoutDiaDiem` (gồm icon vị trí và text địa điểm); nếu null/rỗng thì ẩn `View.GONE`.
3. **Xử lý ghi chú (dòng 83–89):** Kiểm tra `item.getGhiChu()`, ẩn/hiện tương tự địa điểm.
4. **Phân loại trạng thái và áp dụng phong cách thị giác (dòng 91–103):**
   * Gọi `boolean isPast = DateUtils.isPastDateTime(item.getThoiGian());`.
   * **Nếu cuộc hẹn đã qua (`isPast == true`):**
     * Text badge: `"Đã qua"`.
     * Nền badge: `@drawable/bg_badge_passed` (màu xám nhạt bo góc).
     * Màu chữ badge: `text_secondary`.
     * Độ trong suốt của toàn bộ card: `binding.cardCuocHen.setAlpha(0.65f);` (làm mờ nhẹ để người dùng tập trung vào các cuộc hẹn sắp tới).
   * **Nếu cuộc hẹn chưa diễn ra (`isPast == false`):**
     * Text badge: `"Sắp diễn ra"`.
     * Nền badge: `@drawable/bg_badge_upcoming` (màu xanh lá nhạt bo góc).
     * Màu chữ badge: `status_completed` (màu xanh lá đậm nổi bật).
     * Độ trong suốt card: `binding.cardCuocHen.setAlpha(1.0f);` (sáng rõ 100%).
5. **Gắn nút sửa và nút xóa (dòng 105–117):** Gửi sự kiện về cho Fragment xử lý.
