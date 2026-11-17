# TÀI LIỆU GIẢI THÍCH CHI TIẾT MÃ NGUỒN – NHÓM E: UI & ACTIVITIES
**Thư mục:** `app/src/main/java/com/example/app/` và `app/src/main/java/com/example/app/ui/`

---

## TỔNG QUAN NHÓM GIAO DIỆN (UI)
Tầng Giao diện người dùng chịu trách nhiệm điều khiển toàn bộ luồng tương tác, hiển thị và phản hồi người dùng. Kiến trúc UI tuân thủ mô hình phân rã hướng component của Android Jetpack:
* `MainActivity`: Màn hình khởi đầu điều phối danh sách đồ án.
* `DoAnDetailActivity`: Màn hình chi tiết chứa `ViewPager2` và `TabLayout`.
* `NhiemVuFragment`, `CuocHenFragment`: Hai fragment độc lập quản lý hai tab con.
* Các `DialogFragment` (`AddEditDoAnDialogFragment`, `AddEditNhiemVuDialogFragment`, `AddEditCuocHenDialogFragment`, `ConfirmDeleteDialogFragment`): Các hộp thoại giao tiếp qua `Fragment Result API`, xử lý chống mất dữ liệu khi xoay màn hình điện thoại.

---

## 1. MainActivity.java (`app/src/main/java/com/example/app/MainActivity.java`)

### 1.1. Vai trò
`MainActivity` là Activity chính của ứng dụng (`MAIN` và `LAUNCHER`), hiển thị toàn bộ danh sách đồ án, quản lý menu nạp dữ liệu mẫu trên Toolbar, điều hướng sang màn hình chi tiết đồ án và kích hoạt các dialog thêm/sửa/xóa đồ án.

### 1.2. Mối quan hệ
* **Kế thừa & Triển khai:** Kế thừa `AppCompatActivity`, triển khai interface `DoAnAdapter.OnDoAnActionListener`.
* **Lớp mà `MainActivity` gọi:** `DoAnDao`, `DoAnAdapter`, `DoAnDetailActivity`, `AddEditDoAnDialogFragment`, `ConfirmDeleteDialogFragment`, `SampleDataLoader`, `ActivityMainBinding`.

### 1.3. Bảng giải thích chi tiết từng hàm trong `MainActivity.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động (các bước chính) | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `onCreate(Bundle)` | `Bundle savedInstanceState` $\rightarrow$ `void` | Khởi tạo Activity, bật EdgeToEdge, ViewBinding và lắng nghe kết quả xóa | **Bước 1:** Bật `EdgeToEdge.enable(this)`.<br>**Bước 2:** `inflate` layout `ActivityMainBinding`.<br>**Bước 3:** Đăng ký WindowInsets đệm thanh hệ thống.<br>**Bước 4:** Khởi tạo `doAnDao`.<br>**Bước 5:** Đăng ký `setFragmentResultListener` cho `ConfirmDeleteDialogFragment`.<br>**Bước 6:** Gọi `initViews()`. | Hệ điều hành Android khi khởi chạy app |
| 2 | `initViews()` | Không có $\rightarrow$ `void` | Cấu hình Toolbar, RecyclerView và nút FAB | **Bước 1:** `setSupportActionBar(binding.toolbar)`.<br>**Bước 2:** Khởi tạo `DoAnAdapter(this)` và gán cho `rvDoAn`.<br>**Bước 3:** Bắt sự kiện click `fabAddDoAn` mở dialog thêm mới đồ án. | `onCreate()` |
| 3 | `onCreateOptionsMenu(Menu)` | `Menu menu` $\rightarrow$ `boolean` | Nạp menu overflow vào Toolbar | Gọi `getMenuInflater().inflate(R.menu.menu_main, menu)`. Trả về `true`. | Android khi tạo menu ActionBar |
| 4 | `onOptionsItemSelected(MenuItem)` | `MenuItem item` $\rightarrow$ `boolean` | Xử lý khi bấm mục menu | Kiểm tra `if (item.getItemId() == R.id.action_load_sample_data)`, gọi `onLoadSampleDataRequested()` và trả về `true`. | Android khi bấm menu |
| 5 | `onLoadSampleDataRequested()` | Không có $\rightarrow$ `void` | Hiển thị hộp thoại xác nhận nạp dữ liệu mẫu | **Bước 1:** Kiểm tra `doAnDao.hasData()`.<br>**Bước 2:** Nếu có dữ liệu: hiện dialog cảnh báo dữ liệu đã có, nút dương tính là "Thêm vào".<br>**Bước 3:** Nếu rỗng: hiện dialog xác nhận nạp dữ liệu mẫu bình thường. | `onOptionsItemSelected()` |
| 6 | `importSampleData()` | Không có $\rightarrow$ `void` | Gọi nạp dữ liệu mẫu và báo kết quả | **Bước 1:** Gọi `SampleDataLoader.importFromAssets(this)`.<br>**Bước 2:** Nếu thành công: gọi `loadData()` và hiện `Snackbar` thông báo số lượng nạp.<br>**Bước 3:** Nếu thất bại: hiện Snackbar báo lỗi. | Hộp thoại xác nhận nạp dữ liệu |
| 7 | `onResume()` | Không có $\rightarrow$ `void` | Làm mới dữ liệu khi quay lại màn hình | Gọi `super.onResume()` và gọi `loadData()`. | Vòng đời Android khi màn hình hiển thị lại |
| 8 | `loadData()` | Không có $\rightarrow$ `void` | Truy vấn CSDL và nạp dữ liệu vào RecyclerView | **Bước 1:** Gọi `doAnDao.getAllWithTienDo()`.<br>**Bước 2:** Gửi danh sách qua `adapter.submitList(list)`.<br>**Bước 3:** Nếu list rỗng: hiện `layoutEmpty`, ẩn `rvDoAn`; ngược lại hiện `rvDoAn`, ẩn `layoutEmpty`. | `onCreate()`, `onResume()`, `importSampleData()` |
| 9 | `onItemClick(DoAnWithTienDo)` | `DoAnWithTienDo item` $\rightarrow$ `void` | Mở màn hình chi tiết đồ án | **Bước 1:** Tạo `Intent intent = new Intent(this, DoAnDetailActivity.class)`.<br>**Bước 2:** `intent.putExtra(EXTRA_DO_AN_ID, item.getDoAn().getId())`.<br>**Bước 3:** `startActivity(intent)`. | `DoAnAdapter` khi chạm card |
| 10 | `onEditClick(DoAnWithTienDo)` | `DoAnWithTienDo item` $\rightarrow$ `void` | Mở hộp thoại sửa đồ án | Gọi `showAddEditDialog(item.getDoAn())`. | `DoAnAdapter` khi chạm nút bút chì |
| 11 | `onDeleteClick(DoAnWithTienDo)` | `DoAnWithTienDo item` $\rightarrow$ `void` | Mở hộp thoại xác nhận xóa đồ án | Khởi tạo `ConfirmDeleteDialogFragment` với loại `TYPE_DO_AN` và id đồ án, sau đó gọi `show()`. | `DoAnAdapter` khi chạm nút thùng rác |
| 12 | `showAddEditDialog(DoAn)` | `DoAn existing` $\rightarrow$ `void` | Mở DialogFragment thêm/sửa đồ án | Khởi tạo `AddEditDoAnDialogFragment.newInstance(existing)`, đăng ký listener `setOnDoAnSavedListener(this::loadData)` và hiển thị dialog. | `initViews()`, `onEditClick()` |

---

## 2. DoAnDetailActivity.java (`app/src/main/java/com/example/app/ui/DoAnDetailActivity.java`)

### 2.1. Vai trò
`DoAnDetailActivity` là màn hình chi tiết đồ án. Nó quản lý thông tin tổng quan của đồ án ở phần đầu (Header card), thanh ProgressBar tổng thể và điều khiển việc chuyển đổi giữa 2 tab con (Nhiệm vụ và Cuộc hẹn) thông qua `TabLayout` và `ViewPager2`.

### 2.2. Mối quan hệ
* **Kế thừa:** `AppCompatActivity`.
* **Lớp gọi:** `MainActivity.onItemClick()`.
* **Lớp mà nó gọi:** `DoAnDao`, `NhiemVuFragment`, `CuocHenFragment`, `FragmentStateAdapter`, `TabLayoutMediator`, `ActivityDoAnDetailBinding`.

### 2.3. Bảng giải thích chi tiết từng hàm trong `DoAnDetailActivity.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động (các bước chính) | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `onCreate(Bundle)` | `Bundle savedInstanceState` $\rightarrow$ `void` | Khởi tạo màn hình chi tiết, nhận `EXTRA_DO_AN_ID` | **Bước 1:** Nhận `doAnId = getIntent().getIntExtra(...)`. Nếu không tìm thấy hoặc đối tượng null thì Toast và đóng Activity (`finish()`).<br>**Bước 2:** Gọi `setupToolbar()`, `setupHeaderInfo()`, `setupViewPagerAndTabs(savedInstanceState)`, `updateHeaderProgress()`. | Hệ điều hành Android |
| 2 | `setupToolbar()` | Không có $\rightarrow$ `void` | Đặt tiêu đề Toolbar và nút Back | Gán tên đồ án cho Toolbar, gọi `binding.toolbarDetail.setNavigationOnClickListener(v -> finish())`. | `onCreate()` |
| 3 | `setupHeaderInfo()` | Không có $\rightarrow$ `void` | Điền thông tin GVHD và thời gian lên thẻ Header | Hiển thị tên đồ án, giảng viên hướng dẫn (hoặc "Chưa phân công"), khoảng thời gian bắt đầu $\rightarrow$ hạn nộp. | `onCreate()` |
| 4 | `setupViewPagerAndTabs(...)` | `Bundle savedInstanceState` $\rightarrow$ `void` | Cấu hình ViewPager2 và kết nối TabLayout | **Bước 1:** Gán `FragmentStateAdapter` cho `viewPagerDetail` (trả về `NhiemVuFragment` tại tab 0, `CuocHenFragment` tại tab 1).<br>**Bước 2:** Kết nối qua `TabLayoutMediator` đặt nhãn tab "Nhiệm vụ" và "Lịch hẹn GV".<br>**Bước 3:** Khôi phục tab đang chọn từ `savedInstanceState`. | `onCreate()` |
| 5 | `createFragment(int)` | `int position` $\rightarrow$ `Fragment` | (FragmentStateAdapter) Khởi tạo Fragment cho từng tab | `position == 0` trả về `NhiemVuFragment.newInstance(doAnId)`; `position == 1` trả về `CuocHenFragment.newInstance(doAnId)`. | ViewPager2 khi cần nạp tab |
| 6 | `getItemCount()` | Không có $\rightarrow$ `int` | (FragmentStateAdapter) Trả về số lượng tab | Trả về cố định là `2`. | ViewPager2 |
| 7 | `onSaveInstanceState(Bundle)` | `Bundle outState` $\rightarrow$ `void` | Lưu lại tab đang chọn khi xoay màn hình | Đưa vị trí tab hiện tại `binding.tabLayoutDetail.getSelectedTabPosition()` vào `outState` với key `KEY_SELECTED_TAB`. | Android khi xoay màn hình |
| 8 | `updateHeaderProgress()` | Không có $\rightarrow$ `void` | Cập nhật tiến độ trên Header theo thời gian thực | Gọi `doAnDao.getTienDo(doAnId)`, đặt `progressDetail.setProgress(percent)` và gán chuỗi `"X/Y (Z%)"`. | `onCreate()`, `NhiemVuFragment.notifyActivityProgressChanged()` |

---

## 3. NhiemVuFragment.java (`app/src/main/java/com/example/app/ui/NhiemVuFragment.java`)

### 3.1. Vai trò
`NhiemVuFragment` quản lý giao diện và nghiệp vụ của tab Danh sách Nhiệm vụ trong màn hình chi tiết đồ án. Nó điều khiển RecyclerView các công việc, xử lý sự kiện tick/untick CheckBox hoàn thành công việc và thông báo cho Activity cha cập nhật lại thanh tiến độ trên Header.

### 3.2. Mối quan hệ
* **Kế thừa & Triển khai:** Kế thừa `Fragment`, triển khai `NhiemVuAdapter.OnNhiemVuActionListener`.
* **Lớp gọi:** `DoAnDetailActivity` thông qua `FragmentStateAdapter`.
* **Lớp mà nó gọi:** `NhiemVuDao`, `DoAnDao`, `NhiemVuAdapter`, `AddEditNhiemVuDialogFragment`, `ConfirmDeleteDialogFragment`.

### 3.3. Bảng giải thích chi tiết từng hàm trong `NhiemVuFragment.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động (các bước chính) | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `newInstance(int)` | `int doAnId` $\rightarrow$ `NhiemVuFragment` | Factory method khởi tạo Fragment kèm tham số | Tạo `Bundle`, đặt `ARG_DO_AN_ID = doAnId`, gán `setArguments(args)` và trả về fragment. | `DoAnDetailActivity.createFragment()` |
| 2 | `onCreate(Bundle)` | `Bundle savedInstanceState` $\rightarrow$ `void` | Đọc tham số `doAnId` và khởi tạo DAO | Đọc `doAnId` từ `getArguments()`, khởi tạo `nhiemVuDao` và `doAnDao`. | Vòng đời Fragment |
| 3 | `onCreateView(...)` | `LayoutInflater, ViewGroup, Bundle` $\rightarrow$ `View` | Tạo giao diện Fragment | `FragmentNhiemVuBinding.inflate(inflater, container, false)`. Trả về `binding.getRoot()`. | Vòng đời Fragment |
| 4 | `onViewCreated(...)` | `View view, Bundle savedInstanceState` $\rightarrow$ `void` | Cấu hình RecyclerView, FAB và FragmentResultListener | **Bước 1:** Khởi tạo `NhiemVuAdapter(this)` và gán cho `rvNhiemVu`.<br>**Bước 2:** Bắt sự kiện click nút FAB thêm nhiệm vụ.<br>**Bước 3:** Đăng ký lắng nghe kết quả xóa từ `ConfirmDeleteDialogFragment` qua `getChildFragmentManager()`.<br>**Bước 4:** Gọi `loadTasks()`. | Vòng đời Fragment |
| 5 | `onResume()` | Không có $\rightarrow$ `void` | Làm mới dữ liệu khi quay lại tab | Gọi `loadTasks()`. | Vòng đời Fragment |
| 6 | `loadTasks()` | Không có $\rightarrow$ `void` | Tải danh sách nhiệm vụ từ CSDL | **Bước 1:** Gọi `nhiemVuDao.getByDoAnId(doAnId)`.<br>**Bước 2:** `adapter.submitList(list)`.<br>**Bước 3:** Ẩn/hiện Empty state.<br>**Bước 4:** Gọi `notifyActivityProgressChanged()`. | `onViewCreated()`, `onResume()`, `onStatusChanged()`, sau khi thêm/sửa/xóa |
| 7 | `notifyActivityProgressChanged()` | Không có $\rightarrow$ `void` | Báo cho Activity cha cập nhật Header | Kiểm tra `if (getActivity() instanceof DoAnDetailActivity)`, ép kiểu và gọi `((DoAnDetailActivity) getActivity()).updateHeaderProgress()`. | `loadTasks()` |
| 8 | `onStatusChanged(...)` | `NhiemVu item, boolean isChecked` $\rightarrow$ `void` | Xử lý khi tick CheckBox | Gọi `nhiemVuDao.updateStatus(item.getId(), isChecked)` rồi gọi `loadTasks()`. | `NhiemVuAdapter` |
| 9 | `onEditClick(NhiemVu)` | `NhiemVu item` $\rightarrow$ `void` | Mở dialog sửa nhiệm vụ | Gọi `showAddEditDialog(item)`. | `NhiemVuAdapter` |
| 10 | `onDeleteClick(NhiemVu)` | `NhiemVu item` $\rightarrow$ `void` | Mở dialog xác nhận xóa | Khởi tạo `ConfirmDeleteDialogFragment` với loại `TYPE_NHIEM_VU` và show bằng `getChildFragmentManager()`. | `NhiemVuAdapter` |
| 11 | `showAddEditDialog(NhiemVu)` | `NhiemVu existing` $\rightarrow$ `void` | Mở DialogFragment thêm/sửa nhiệm vụ | Khởi tạo `AddEditNhiemVuDialogFragment.newInstance(doAnId, existing)`, đăng ký callback `loadTasks()` và show. | `onViewCreated()`, `onEditClick()` |
| 12 | `onDestroyView()` | Không có $\rightarrow$ `void` | Dọn dẹp binding chống rò rỉ bộ nhớ | Gán `binding = null` và gọi `super.onDestroyView()`. | Vòng đời Fragment |

---

## 4. CuocHenFragment.java (`app/src/main/java/com/example/app/ui/CuocHenFragment.java`)

### 4.1. Vai trò
`CuocHenFragment` quản lý giao diện và nghiệp vụ của tab Lịch hẹn Giảng viên. Nó hiển thị danh sách các buổi hẹn sắp xếp theo thứ tự thời gian tăng dần, hỗ trợ thêm cuộc hẹn mới qua FAB và kích hoạt sửa/xóa cuộc hẹn.

### 4.2. Mối quan hệ
* **Kế thừa & Triển khai:** Kế thừa `Fragment`, triển khai `CuocHenAdapter.OnCuocHenActionListener`.
* **Lớp gọi:** `DoAnDetailActivity`.
* **Lớp mà nó gọi:** `CuocHenDao`, `CuocHenAdapter`, `AddEditCuocHenDialogFragment`, `ConfirmDeleteDialogFragment`.

### 4.3. Bảng giải thích chi tiết từng hàm trong `CuocHenFragment.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `newInstance(int)` | `int doAnId` $\rightarrow$ `CuocHenFragment` | Factory method khởi tạo kèm `doAnId` | Đóng gói `doAnId` vào `Bundle` đối số và trả về fragment mới. | `DoAnDetailActivity.createFragment()` |
| 2 | `onCreate(Bundle)` | `Bundle savedInstanceState` $\rightarrow$ `void` | Đọc `doAnId` và khởi tạo DAO | Đọc `ARG_DO_AN_ID`, khởi tạo `cuocHenDao`. | Vòng đời Fragment |
| 3 | `onCreateView(...)` | `LayoutInflater, ViewGroup, Bundle` $\rightarrow$ `View` | Tạo View cho fragment | Inflate `FragmentCuocHenBinding`. Trả về `binding.getRoot()`. | Vòng đời Fragment |
| 4 | `onViewCreated(...)` | `View, Bundle` $\rightarrow$ `void` | Cấu hình Adapter, FAB và listener xóa | Thiết lập `CuocHenAdapter(this)`, bắt click FAB, lắng nghe `ConfirmDeleteDialogFragment` qua `getChildFragmentManager()`. Gọi `loadMeetings()`. | Vòng đời Fragment |
| 5 | `onResume()` | Không có $\rightarrow$ `void` | Làm mới danh sách khi hiển thị lại | Gọi `loadMeetings()`. | Vòng đời Fragment |
| 6 | `loadMeetings()` | Không có $\rightarrow$ `void` | Nạp danh sách cuộc hẹn từ SQLite | Lấy danh sách từ `cuocHenDao.getByDoAnId(doAnId)`, gửi vào adapter và ẩn/hiện layoutEmpty. | `onViewCreated()`, `onResume()`, sau thêm/sửa/xóa |
| 7 | `onEditClick(CuocHen)` | `CuocHen item` $\rightarrow$ `void` | Mở dialog sửa cuộc hẹn | Gọi `showAddEditDialog(item)`. | `CuocHenAdapter` |
| 8 | `onDeleteClick(CuocHen)` | `CuocHen item` $\rightarrow$ `void` | Mở dialog xác nhận xóa cuộc hẹn | Khởi tạo `ConfirmDeleteDialogFragment` với loại `TYPE_CUOC_HEN` và hiển thị. | `CuocHenAdapter` |
| 9 | `showAddEditDialog(CuocHen)` | `CuocHen existing` $\rightarrow$ `void` | Mở DialogFragment thêm/sửa cuộc hẹn | Khởi tạo `AddEditCuocHenDialogFragment`, đăng ký `setOnCuocHenSavedListener(this::loadMeetings)` và show. | FAB click, `onEditClick()` |
| 10 | `onDestroyView()` | Không có $\rightarrow$ `void` | Giải phóng ViewBinding | Gán `binding = null`. | Vòng đời Fragment |

---

## 5. AddEditDoAnDialogFragment.java (`app/src/main/java/com/example/app/ui/AddEditDoAnDialogFragment.java`)

### 5.1. Vai trò
Hộp thoại thêm mới hoặc chỉnh sửa thông tin đồ án. Lớp này kế thừa `DialogFragment`, sử dụng `MaterialAlertDialogBuilder`, tích hợp `MaterialDatePicker` để chọn ngày bắt đầu và hạn nộp, đồng thời thực thi cơ chế tự lưu trạng thái nhập liệu chống mất dữ liệu khi xoay màn hình điện thoại.

### 5.2. Mối quan hệ
* **Các lớp gọi:** `MainActivity.showAddEditDialog()`.
* **Lớp mà nó gọi:** `DoAnDao`, `DateUtils`, `MaterialDatePicker`, `DialogAddEditDoAnBinding`.
* **Interface định nghĩa:** `OnDoAnSavedListener` có phương thức `void onDoAnSaved()`.

### 5.3. Bảng giải thích chi tiết từng hàm trong `AddEditDoAnDialogFragment.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động (các bước chính) | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `onDoAnSaved()` | Không có $\rightarrow$ `void` | (Interface) Báo lưu đồ án thành công | Callback để màn hình cha gọi `loadData()`. | `handleSave()` |
| 2 | `newInstance(DoAn)` | `DoAn existing` $\rightarrow$ `AddEditDoAnDialogFragment` | Factory method khởi tạo dialog | Nếu `existing != null` thì đưa vào arguments (`Serializable`), ngược lại tạo rỗng. | `MainActivity` |
| 3 | `setOnDoAnSavedListener(...)` | `OnDoAnSavedListener listener` $\rightarrow$ `void` | Gán callback lưu đồ án | Lưu tham chiếu `this.listener = listener`. | `MainActivity` |
| 4 | `onCreate(Bundle)` | `Bundle savedInstanceState` $\rightarrow$ `void` | Lấy đối tượng đồ án cũ nếu có | Đọc `existingDoAn` từ `getArguments()`. | Vòng đời DialogFragment |
| 5 | `onCreateDialog(Bundle)` | `Bundle savedInstanceState` $\rightarrow$ `Dialog` | Khởi tạo giao diện Dialog và khôi phục form | Xem giải thích chi tiết ở mục 5.4 bên dưới. | Vòng đời DialogFragment |
| 6 | `onStart()` | Không có $\rightarrow$ `void` | Gắn lại listener cho DatePicker khi xoay màn hình | Gọi `super.onStart()` và gọi `rebindPickerListeners()`. | Vòng đời DialogFragment |
| 7 | `rebindPickerListeners()` | Không có $\rightarrow$ `void` | Tìm lại các DatePicker đang mở dở dang và gắn lại callback | Tìm Fragment theo tag `TAG_PICKER_START_DATE` và `TAG_PICKER_DUE_DATE`. Nếu tồn tại thì gắn lại `addOnPositiveButtonClickListener`. | `onStart()` |
| 8 | `showStartDatePicker()` | Không có $\rightarrow$ `void` | Mở MaterialDatePicker cho Ngày bắt đầu | Lấy ngày hiện tại trong ô nhập liệu, chuyển sang UTC millis, khởi tạo `MaterialDatePicker`, gắn listener và `show()`. | Chạm vào ô Ngày bắt đầu |
| 9 | `showDueDatePicker()` | Không có $\rightarrow$ `void` | Mở MaterialDatePicker cho Hạn nộp | Khởi tạo `MaterialDatePicker` cho hạn nộp, gắn listener và `show()`. | Chạm vào ô Hạn nộp |
| 10 | `handleSave()` | Không có $\rightarrow$ `void` | Kiểm tra dữ liệu và lưu vào CSDL SQLite | **Bước 1:** Trích xuất chuỗi từ các EditText.<br>**Bước 2:** Kiểm tra tên không được rỗng (`tilTen.setError`).<br>**Bước 3:** Kiểm tra `isValidDateRange(ngayBatDau, hanNop)` (hạn nộp $\ge$ ngày bắt đầu).<br>**Bước 4:** Nếu lỗi: dừng lại, giữ nguyên dialog.<br>**Bước 5:** Nếu hợp lệ: gọi `dao.update()` hoặc `dao.insert()`, thông báo Toast, gọi `listener.onDoAnSaved()` và đóng dialog (`dismiss()`). | Khi nhấn nút "Lưu" |
| 11 | `onSaveInstanceState(Bundle)` | `Bundle outState` $\rightarrow$ `void` | Lưu tạm nội dung form khi xoay màn hình | Đưa 5 chuỗi từ các EditText vào `outState` với các key `KEY_TEN`, `KEY_MO_TA`, `KEY_GIANG_VIEN`, `KEY_NGAY_BAT_DAU`, `KEY_HAN_NOP`. | Android khi xoay màn hình |

### 5.4. Điểm kỹ thuật đáng chú ý: Chặn Dialog tự đóng khi dữ liệu nhập lỗi
Tại dòng 98–100 của `AddEditDoAnDialogFragment.java`:
```java
dialog.setOnShowListener(d -> {
    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> handleSave());
});
```
* **Lý do thiết kế:** Mặc định của `AlertDialog`, khi người dùng nhấn nút Positive (`R.string.save`), hệ thống sẽ **tự động đóng Dialog bất kể dữ liệu đúng hay sai**. Bằng cách ghi đè `OnClickListener` của nút sau khi Dialog đã hiển thị (`onShowListener`), ta có thể can thiệp: Nếu người dùng nhập thiếu tên hoặc chọn ngày hạn nộp nhỏ hơn ngày bắt đầu, `handleSave()` sẽ hiển thị lỗi đỏ trên `TextInputLayout` và `return` mà không gọi `dismiss()`, giúp người dùng tiếp tục sửa lỗi mà không bị mất dữ liệu đang gõ.

---

## 6. AddEditNhiemVuDialogFragment.java (`app/src/main/java/com/example/app/ui/AddEditNhiemVuDialogFragment.java`)

### 6.1. Vai trò
Hộp thoại thêm mới hoặc chỉnh sửa đầu mục nhiệm vụ. Lớp này kiểm tra tính hợp lệ của hạn hoàn thành nhiệm vụ so với hạn nộp của đồ án cha (hạn hoàn thành không được vượt quá hạn nộp đồ án).

### 6.2. Mối quan hệ
* **Các lớp gọi:** `NhiemVuFragment.showAddEditDialog()`.
* **Lớp mà nó gọi:** `NhiemVuDao`, `DoAnDao`, `DateUtils`, `MaterialDatePicker`, `DialogAddEditNhiemVuBinding`.
* **Interface định nghĩa:** `OnNhiemVuSavedListener` có phương thức `void onNhiemVuSaved()`.

### 6.3. Bảng giải thích chi tiết từng hàm trong `AddEditNhiemVuDialogFragment.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `onNhiemVuSaved()` | Không có $\rightarrow$ `void` | (Interface) Báo lưu nhiệm vụ thành công | Gọi `loadTasks()` ở Fragment. | `handleSave()` |
| 2 | `newInstance(int, NhiemVu)` | `int doAnId, NhiemVu existing` $\rightarrow$ `AddEditNhiemVuDialogFragment` | Factory method khởi tạo | Đóng gói `doAnId` và đối tượng `existing` (nếu sửa) vào arguments. | `NhiemVuFragment` |
| 3 | `setOnNhiemVuSavedListener(...)` | `OnNhiemVuSavedListener listener` $\rightarrow$ `void` | Đăng ký callback lưu | Gán `this.listener = listener`. | `NhiemVuFragment` |
| 4 | `onCreate(Bundle)` | `Bundle savedInstanceState` $\rightarrow$ `void` | Lấy dữ liệu arguments | Đọc `doAnId` và `existingNhiemVu`. | Vòng đời DialogFragment |
| 5 | `onCreateDialog(Bundle)` | `Bundle savedInstanceState` $\rightarrow$ `Dialog` | Khởi tạo dialog và khôi phục form | Nạp layout, khôi phục `KEY_TEN`, `KEY_GHI_CHU`, `KEY_HAN`, `KEY_DA_HOAN_THANH`, ghi đè nút Lưu để kiểm tra hợp lệ. | Vòng đời DialogFragment |
| 6 | `onStart()` | Không có $\rightarrow$ `void` | Gắn lại callback cho DatePicker | Gọi `rebindPickerListeners()`. | Vòng đời DialogFragment |
| 7 | `rebindPickerListeners()` | Không có $\rightarrow$ `void` | Tìm lại DatePicker theo tag khi xoay màn hình | Tìm `TAG_PICKER_TASK_DUE`, nếu có thì gắn lại callback gán text vào `etHanHoanThanh`. | `onStart()` |
| 8 | `showDueDatePicker()` | Không có $\rightarrow$ `void` | Mở MaterialDatePicker chọn hạn chót | Hiển thị bộ chọn ngày Material cho nhiệm vụ. | Chạm vào ô hạn hoàn thành |
| 9 | `handleSave()` | Không có $\rightarrow$ `void` | Kiểm tra ràng buộc và lưu CSDL | **Bước 1:** Kiểm tra tên nhiệm vụ không rỗng.<br>**Bước 2:** Truy vấn đồ án cha qua `doAnDao.getById(doAnId)`. Nếu đồ án có hạn nộp và nhiệm vụ có hạn chót, gọi `DateUtils.isValidDateRange(hanHoanThanh, doAn.getHanNop())`. Nếu vi phạm (hạn nhiệm vụ sau hạn đồ án), báo lỗi `R.string.error_task_due_after_project`.<br>**Bước 3:** Chèn mới (`insert`) hoặc cập nhật (`update`) vào CSDL.<br>**Bước 4:** Báo thành công và `dismiss()`. | Nhấn nút "Lưu" |
| 10 | `onSaveInstanceState(Bundle)` | `Bundle outState` $\rightarrow$ `void` | Lưu trạng thái form khi xoay màn hình | Lưu tên, ghi chú, hạn và trạng thái checkbox vào bundle. | Android khi xoay màn hình |

---

## 7. AddEditCuocHenDialogFragment.java (`app/src/main/java/com/example/app/ui/AddEditCuocHenDialogFragment.java`)

### 7.1. Vai trò
Hộp thoại thêm mới hoặc chỉnh sửa cuộc hẹn với giảng viên. Lớp này hỗ trợ luồng chọn thời gian 2 bước mượt mà: Chọn ngày qua `MaterialDatePicker`, ngay sau đó tự động mở tiếp `MaterialTimePicker` (chế độ 24 giờ) để chọn giờ/phút, sau đó ghép lại thành định dạng chuẩn `yyyy-MM-dd HH:mm`.

### 7.2. Mối quan hệ
* **Các lớp gọi:** `CuocHenFragment.showAddEditDialog()`.
* **Lớp mà nó gọi:** `CuocHenDao`, `DateUtils`, `MaterialDatePicker`, `MaterialTimePicker`, `DialogAddEditCuocHenBinding`.
* **Interface định nghĩa:** `OnCuocHenSavedListener` có phương thức `void onCuocHenSaved()`.

### 7.3. Bảng giải thích chi tiết từng hàm trong `AddEditCuocHenDialogFragment.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `onCuocHenSaved()` | Không có $\rightarrow$ `void` | (Interface) Báo lưu cuộc hẹn thành công | Callback gọi `loadMeetings()` ở Fragment. | `handleSave()` |
| 2 | `newInstance(int, CuocHen)` | `int doAnId, CuocHen existing` $\rightarrow$ `AddEditCuocHenDialogFragment` | Factory method khởi tạo | Đóng gói `doAnId` và đối tượng cuộc hẹn cũ vào arguments. | `CuocHenFragment` |
| 3 | `setOnCuocHenSavedListener(...)` | `OnCuocHenSavedListener listener` $\rightarrow$ `void` | Gán callback lưu | Gán `this.listener = listener`. | `CuocHenFragment` |
| 4 | `onCreate(Bundle)` | `Bundle savedInstanceState` $\rightarrow$ `void` | Lấy tham số arguments | Đọc `doAnId` và `existingCuocHen`. | Vòng đời DialogFragment |
| 5 | `onCreateDialog(Bundle)` | `Bundle savedInstanceState` $\rightarrow$ `Dialog` | Khởi tạo dialog, nạp hoặc khôi phục dữ liệu | Khôi phục `tempSelectedDate`, tiêu đề, thời gian, địa điểm, ghi chú. Ghi đè nút Positive để kiểm tra hợp lệ. | Vòng đời DialogFragment |
| 6 | `onStart()` | Không có $\rightarrow$ `void` | Gắn lại callback picker khi xoay màn hình | Gọi `rebindPickerListeners()`. | Vòng đời DialogFragment |
| 7 | `rebindPickerListeners()` | Không có $\rightarrow$ `void` | Tìm lại DatePicker và TimePicker dở dang | Tìm theo tag `TAG_PICKER_MEETING_DATE` và `TAG_PICKER_MEETING_TIME`, gắn lại callback để tiếp tục luồng chọn ngày giờ sau khi xoay màn hình. | `onStart()` |
| 8 | `showDateTimePicker()` | Không có $\rightarrow$ `void` | Bước 1: Mở MaterialDatePicker chọn ngày | Khởi tạo `MaterialDatePicker`. Khi người dùng bấm nút Chọn: lưu ngày vào `tempSelectedDate` và gọi ngay `showTimePicker()`. | Chạm vào ô Thời gian hẹn |
| 9 | `showTimePicker()` | Không có $\rightarrow$ `void` | Bước 2: Mở MaterialTimePicker chọn giờ | Khởi tạo `MaterialTimePicker` với định dạng `TimeFormat.CLOCK_24H`. Khi người dùng bấm Chọn: gọi `applySelectedDateTime(hour, minute)`. | Sau khi chọn xong ngày |
| 10 | `applySelectedDateTime(...)` | `int hour, int minute` $\rightarrow$ `void` | Ghép ngày và giờ thành chuỗi hoàn chỉnh | Định dạng giờ thành `%02d:%02d`, ghép với chuỗi ngày thành `"yyyy-MM-dd HH:mm"`, gán vào `etThoiGian` và xóa thông báo lỗi. | Khi chọn xong giờ |
| 11 | `handleSave()` | Không có $\rightarrow$ `void` | Kiểm tra hợp lệ và lưu vào CSDL | Kiểm tra tiêu đề và thời gian không được để trống. Gọi `dao.insert()` hoặc `dao.update()`, Toast thành công, báo callback và đóng dialog. | Khi nhấn nút "Lưu" |
| 12 | `onSaveInstanceState(Bundle)` | `Bundle outState` $\rightarrow$ `void` | Lưu trạng thái nhập liệu khi xoay màn hình | Lưu `tempSelectedDate`, tiêu đề, thời gian, địa điểm, ghi chú vào `outState`. | Android khi xoay màn hình |

---

## 8. ConfirmDeleteDialogFragment.java (`app/src/main/java/com/example/app/ui/ConfirmDeleteDialogFragment.java`)

### 8.1. Vai trò
`ConfirmDeleteDialogFragment` là hộp thoại xác nhận xóa dùng chung (Generic Confirmation Dialog) cho toàn bộ ứng dụng (dùng cho cả Đồ án, Nhiệm vụ và Cuộc hẹn). Hộp thoại này sử dụng cơ chế `Fragment Result API` của Android Jetpack để trả kết quả đồng ý hoặc hủy bỏ về cho màn hình gọi mà không tạo ra bất kỳ liên kết phụ thuộc cứng (tight coupling) nào.

### 8.2. Mối quan hệ
* **Các lớp gọi:**
  * `MainActivity`: Mở dialog xác nhận xóa đồ án (lắng nghe kết quả qua `getSupportFragmentManager().setFragmentResultListener(...)`).
  * `NhiemVuFragment`: Mở dialog xác nhận xóa nhiệm vụ (lắng nghe qua `getChildFragmentManager().setFragmentResultListener(...)`).
  * `CuocHenFragment`: Mở dialog xác nhận xóa cuộc hẹn (lắng nghe qua `getChildFragmentManager().setFragmentResultListener(...)`).
* **Lớp mà nó gọi:** `MaterialAlertDialogBuilder`, `DialogFragment`, `Bundle`.

### 8.3. Bảng giải thích chi tiết từng hàm trong `ConfirmDeleteDialogFragment.java`

| STT | Tên hàm | Tham số $\rightarrow$ Kiểu trả về | Công dụng | Cách hoạt động (các bước chính) | Được gọi từ đâu |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 1 | `newInstance(...)` | `String requestKey, String title, String message, String itemType, long itemId` $\rightarrow$ `ConfirmDeleteDialogFragment` | Factory method khởi tạo hộp thoại xác nhận | **Bước 1:** Khởi tạo fragment và `Bundle args`.<br>**Bước 2:** Đưa `requestKey`, `title`, `message`, `itemType` (`DO_AN` / `NHIEM_VU` / `CUOC_HEN`) và `itemId` vào bundle.<br>**Bước 3:** `fragment.setArguments(args)` và trả về. | `MainActivity`, `NhiemVuFragment`, `CuocHenFragment` |
| 2 | `onCreateDialog(Bundle)` | `Bundle savedInstanceState` $\rightarrow$ `Dialog` | Khởi tạo hộp thoại cảnh báo Material và gửi kết quả qua Fragment Result API | **Bước 1:** Trích xuất các đối số từ `getArguments()`.<br>**Bước 2:** Tạo `MaterialAlertDialogBuilder` với tiêu đề và thông điệp cảnh báo.<br>**Bước 3:** Thiết lập nút "Xóa" (Positive Button): đóng gói `RESULT_KEY_CONFIRMED = true`, `itemType`, `itemId` vào Bundle và gửi qua `getParentFragmentManager().setFragmentResult(requestKey, result)`.<br>**Bước 4:** Thiết lập nút "Hủy" (Negative Button): đóng gói `RESULT_KEY_CONFIRMED = false` và gửi qua `setFragmentResult`.<br>**Bước 5:** `create()` và trả về Dialog. | Vòng đời DialogFragment |

### 8.4. Điểm kỹ thuật cốt lõi: Vì sao Fragment Result API là chuẩn mực hiện đại?
* **Khắc phục lỗi văng ứng dụng khi xoay màn hình:** Với cách lập trình Android cũ sử dụng Callback Interface (ví dụ `setListener(new OnDeleteListener() {...})`), khi người dùng xoay màn hình trong lúc Dialog đang mở, Fragment/Activity cha bị hủy và tái tạo (recreate), biến tham chiếu `listener` trở thành `null` hoặc trỏ vào Activity cũ đã chết, gây lỗi rò rỉ bộ nhớ hoặc `NullPointerException`.
* **Cơ chế của Fragment Result API:** `ConfirmDeleteDialogFragment` gửi kết quả vào `FragmentManager` thông qua chuỗi định danh `requestKey`. Ngay cả khi Activity/Fragment cha bị tái sinh do xoay màn hình, `FragmentManager` vẫn lưu trữ kết quả an toàn và kích hoạt listener của Activity mới ngay khi vòng đời của nó đạt trạng thái `STARTED`, đảm bảo thao tác xóa được thực thi ổn định 100%.
