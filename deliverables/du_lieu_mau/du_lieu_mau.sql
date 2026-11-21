-- ===================================================================
-- TỆP DỮ LIỆU MẪU: du_lieu_mau.sql
-- Dự án: Quản lý Tiến độ Đồ án (tiendo.db)
-- Nội dung TRÙNG KHỚP với dữ liệu ứng dụng nạp trong DBHelper.chenDuLieuMau()
-- (khi cài lần đầu hoặc khi chọn menu "Nạp dữ liệu mẫu").
-- Chạy được trực tiếp trong DB Browser for SQLite hoặc sqlite3 CLI
-- sau khi đã tạo 3 bảng DoAn, NhiemVu, CuocHen (xem DBHelper.onCreate).
-- Tổng cộng: 4 Đồ án, 14 Nhiệm vụ, 7 Cuộc hẹn = 25 bản ghi
-- ===================================================================

PRAGMA foreign_keys = ON;

BEGIN TRANSACTION;


-- 1. BẢNG DoAn (4 bản ghi)
INSERT INTO DoAn (id, ten, mo_ta, giang_vien, ngay_bat_dau, han_nop) VALUES
(1, 'Xây dựng ứng dụng quản lý tiến độ đồ án trên Android', 'Hệ thống hỗ trợ sinh viên lập kế hoạch và theo dõi lịch hẹn trao đổi với GVHD.', 'TS. Nguyễn Văn A', '2026-09-01', '2026-12-30'),
(2, 'Nghiên cứu và triển khai hệ thống nhận diện khuôn mặt', 'Mô hình AI nhận diện khuôn mặt phục vụ điểm danh sinh viên trong lớp học.', 'ThS. Trần Thị Mai', '2026-08-15', '2026-11-20'),
(3, 'Xây dựng website bán sách trực tuyến BookStore', 'Trang web thương mại điện tử với tính năng thanh toán trực tuyến và gợi ý sách.', 'TS. Lê Hoàng Nam', '2026-08-01', '2026-10-15'),
(4, 'Ứng dụng kết nối gia sư và học sinh TutorNow', 'Nền tảng tìm kiếm và đặt lịch học kèm tại nhà cho học sinh các cấp.', 'ThS. Phạm Thanh Tùng', '2026-10-01', '2027-01-15');

-- 2. BẢNG NhiemVu (14 bản ghi)
-- Nhiệm vụ cho Đồ án 1 (Tiến độ: 2/4 hoàn thành = 50%)
INSERT INTO NhiemVu (id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh) VALUES
(1, 1, 'Khảo sát yêu cầu và viết đề cương chi tiết', 'Lấy ý kiến định hướng từ GVHD', '2026-09-20', 1),
(2, 1, 'Thiết kế kiến trúc hệ thống và CSDL SQLite', 'Thiết kế 3 bảng DoAn, NhiemVu, CuocHen', '2026-10-15', 1),
(3, 1, 'Lập trình giao diện và hoàn thiện RecyclerView CRUD', 'Hoàn thiện các màn hình Activity', '2026-11-25', 0),
(4, 1, 'Viết báo cáo kỹ thuật và chuẩn bị slide thuyết minh', 'Chuẩn bị nội dung báo cáo nghiệm thu', '2026-12-25', 0);

-- Nhiệm vụ cho Đồ án 2 (Tiến độ: 1/3 hoàn thành = 33%)
INSERT INTO NhiemVu (id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh) VALUES
(5, 2, 'Tìm hiểu thuật toán RetinaFace và ArcFace', 'Đọc bài báo khoa học và thử nghiệm mô hình', '2026-09-10', 1),
(6, 2, 'Thu thập và tiền xử lý tập dữ liệu ảnh', 'Thu thập tối thiểu 500 ảnh mẫu', '2026-10-25', 0),
(7, 2, 'Đánh giá độ chính xác mô hình trên tập kiểm thử', 'Đo đạc độ chính xác và độ trễ nhận diện', '2026-11-15', 0);

-- Nhiệm vụ cho Đồ án 3 (Tiến độ: 4/4 hoàn thành = 100%)
INSERT INTO NhiemVu (id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh) VALUES
(8, 3, 'Phân tích yêu cầu và thiết kế lược đồ CSDL MySQL', 'Hoàn thiện 8 bảng dữ liệu chuẩn hóa 3NF', '2026-08-20', 1),
(9, 3, 'Xây dựng RESTful API cho quản lý sách và giỏ hàng', 'Viết backend Spring Boot', '2026-09-15', 1),
(10, 3, 'Thiết kế giao diện người dùng Responsive bằng React', 'Giao diện thân thiện người dùng', '2026-10-01', 1),
(11, 3, 'Kiểm thử chức năng thanh toán trực tuyến và nộp bài', 'Tích hợp cổng giả lập VnPay', '2026-10-12', 1);

-- Nhiệm vụ cho Đồ án 4 (Tiến độ: 0/3 hoàn thành = 0%)
INSERT INTO NhiemVu (id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh) VALUES
(12, 4, 'Nghiên cứu thị trường và khảo sát nhu cầu người dùng', 'Khảo sát 200 phụ huynh học sinh', '2026-11-05', 0),
(13, 4, 'Xây dựng Business Model Canvas (BMC)', 'Hoàn thiện mô hình kinh doanh', '2026-12-01', 0),
(14, 4, 'Thiết kế bản mẫu Wireframe trên Figma', 'Thiết kế luồng đặt lịch học kèm', '2027-01-05', 0);

-- 3. BẢNG CuocHen (7 bản ghi)
-- Cuộc hẹn cho Đồ án 1
INSERT INTO CuocHen (id, do_an_id, tieu_de, thoi_gian, dia_diem, ghi_chu) VALUES
(1, 1, 'Báo cáo đề cương chi tiết', '2026-09-15 09:00', 'Văn phòng Bộ môn KHMT - Phòng 402-A1', 'Thầy duyệt đề cương và phân công lịch báo cáo'),
(2, 1, 'Góp ý thiết kế CSDL và giao diện', '2026-10-20 14:30', 'Phòng nghiên cứu Lab 501', 'Chuẩn bị bản vẽ kiến trúc ERD trước khi gặp'),
(3, 1, 'Kiểm tra tiến độ hoàn thiện ứng dụng', '2026-11-28 08:30', 'Phòng 305-B1', 'Demo chạy thử tính năng SQLite và RecyclerView');

-- Cuộc hẹn cho Đồ án 2
INSERT INTO CuocHen (id, do_an_id, tieu_de, thoi_gian, dia_diem, ghi_chu) VALUES
(4, 2, 'Thống nhất yêu cầu thực tập và mô hình AI', '2026-08-25 10:00', 'Văn phòng Khoa CNTT - Bàn số 3', 'Gặp mặt cô hướng dẫn lần đầu'),
(5, 2, 'Báo cáo kết quả huấn luyện mô hình', '2026-11-19 15:00', 'Google Meet (meet.google.com/xyz-abcd-efg)', 'Trình bày biểu đồ Loss/Accuracy và độ trễ');

-- Cuộc hẹn cho Đồ án 3
INSERT INTO CuocHen (id, do_an_id, tieu_de, thoi_gian, dia_diem, ghi_chu) VALUES
(6, 3, 'Báo cáo nghiệm thu sản phẩm website', '2026-10-14 16:00', 'Phòng máy 204-C4', 'Thầy đánh giá đạt điểm tối đa phần API và giao diện');

-- Cuộc hẹn cho Đồ án 4
INSERT INTO CuocHen (id, do_an_id, tieu_de, thoi_gian, dia_diem, ghi_chu) VALUES
(7, 4, 'Tư vấn cố vấn đề án khởi nghiệp', '2026-12-05 09:30', 'Phòng họp Tòa A', 'Gặp ban cố vấn để xin ý kiến định hướng');

COMMIT;
