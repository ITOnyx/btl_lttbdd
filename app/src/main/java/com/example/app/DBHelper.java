package com.example.app;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

// Lớp quản lý cơ sở dữ liệu SQLite cho toàn bộ ứng dụng
public class DBHelper extends SQLiteOpenHelper {

    // Tên tệp CSDL và phiên bản
    public static final String DATABASE_NAME = "tiendo.db";
    public static final int DATABASE_VERSION = 1;

    // Khởi tạo DBHelper với context của ứng dụng
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Cấu hình trước khi mở kết nối CSDL
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Bật hỗ trợ ràng buộc khóa ngoại trong SQLite.
        // Dòng này đảm bảo khi xóa 1 Đồ án, các Nhiệm vụ và Cuộc hẹn liên quan sẽ tự động bị xóa (ON DELETE CASCADE).
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Tạo các bảng và nạp dữ liệu mẫu ban đầu khi ứng dụng chạy lần đầu
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tạo bảng DoAn
        String sqlCreateDoAn = "CREATE TABLE DoAn (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ten TEXT NOT NULL, " +
                "mo_ta TEXT, " +
                "giang_vien TEXT, " +
                "ngay_bat_dau TEXT, " +
                "han_nop TEXT" +
                ");";
        db.execSQL(sqlCreateDoAn);

        // 2. Tạo bảng NhiemVu (khóa ngoại tham chiếu DoAn(id) và tự xóa theo dây chuyền)
        String sqlCreateNhiemVu = "CREATE TABLE NhiemVu (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "do_an_id INTEGER NOT NULL REFERENCES DoAn(id) ON DELETE CASCADE, " +
                "ten TEXT NOT NULL, " +
                "ghi_chu TEXT, " +
                "han_hoan_thanh TEXT, " +
                "da_hoan_thanh INTEGER NOT NULL DEFAULT 0" +
                ");";
        db.execSQL(sqlCreateNhiemVu);

        // 3. Tạo bảng CuocHen (khóa ngoại tham chiếu DoAn(id) và tự xóa theo dây chuyền)
        String sqlCreateCuocHen = "CREATE TABLE CuocHen (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "do_an_id INTEGER NOT NULL REFERENCES DoAn(id) ON DELETE CASCADE, " +
                "tieu_de TEXT NOT NULL, " +
                "thoi_gian TEXT NOT NULL, " +
                "dia_diem TEXT, " +
                "ghi_chu TEXT" +
                ");";
        db.execSQL(sqlCreateCuocHen);

        // 4. Chèn dữ liệu mẫu cho bảng DoAn (4 đồ án)
        db.execSQL("INSERT INTO DoAn (id, ten, mo_ta, giang_vien, ngay_bat_dau, han_nop) VALUES " +
                "(1, 'Xây dựng ứng dụng quản lý tiến độ đồ án trên Android', 'Hệ thống hỗ trợ sinh viên lập kế hoạch và theo dõi lịch hẹn trao đổi với GVHD.', 'TS. Nguyễn Văn A', '2026-09-01', '2026-12-30');");
        db.execSQL("INSERT INTO DoAn (id, ten, mo_ta, giang_vien, ngay_bat_dau, han_nop) VALUES " +
                "(2, 'Nghiên cứu và triển khai hệ thống nhận diện khuôn mặt', 'Mô hình AI nhận diện khuôn mặt phục vụ điểm danh sinh viên trong lớp học.', 'ThS. Trần Thị Mai', '2026-08-15', '2026-11-20');");
        db.execSQL("INSERT INTO DoAn (id, ten, mo_ta, giang_vien, ngay_bat_dau, han_nop) VALUES " +
                "(3, 'Xây dựng website bán sách trực tuyến BookStore', 'Trang web thương mại điện tử với tính năng thanh toán trực tuyến và gợi ý sách.', 'TS. Lê Hoàng Nam', '2026-08-01', '2026-10-15');");
        db.execSQL("INSERT INTO DoAn (id, ten, mo_ta, giang_vien, ngay_bat_dau, han_nop) VALUES " +
                "(4, 'Ứng dụng kết nối gia sư và học sinh TutorNow', 'Nền tảng tìm kiếm và đặt lịch học kèm tại nhà cho học sinh các cấp.', 'ThS. Phạm Thanh Tùng', '2026-10-01', '2027-01-15');");

        // 5. Chèn dữ liệu mẫu cho bảng NhiemVu (14 nhiệm vụ)
        // Nhiệm vụ Đồ án 1
        db.execSQL("INSERT INTO NhiemVu (id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh) VALUES (1, 1, 'Khảo sát yêu cầu và viết đề cương chi tiết', 'Lấy ý kiến định hướng từ GVHD', '2026-09-20', 1);");
        db.execSQL("INSERT INTO NhiemVu (id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh) VALUES (2, 1, 'Thiết kế kiến trúc hệ thống và CSDL SQLite', 'Thiết kế 3 bảng DoAn, NhiemVu, CuocHen', '2026-10-15', 1);");
        db.execSQL("INSERT INTO NhiemVu (id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh) VALUES (3, 1, 'Lập trình giao diện và hoàn thiện RecyclerView CRUD', 'Hoàn thiện các màn hình Activity', '2026-11-25', 0);");
        db.execSQL("INSERT INTO NhiemVu (id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh) VALUES (4, 1, 'Viết báo cáo kỹ thuật và chuẩn bị slide thuyết minh', 'Chuẩn bị nội dung báo cáo nghiệm thu', '2026-12-25', 0);");

        // Nhiệm vụ Đồ án 2
        db.execSQL("INSERT INTO NhiemVu (id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh) VALUES (5, 2, 'Tìm hiểu thuật toán RetinaFace và ArcFace', 'Đọc bài báo khoa học và thử nghiệm mô hình', '2026-09-10', 1);");
        db.execSQL("INSERT INTO NhiemVu (id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh) VALUES (6, 2, 'Thu thập và tiền xử lý tập dữ liệu ảnh', 'Thu thập tối thiểu 500 ảnh mẫu', '2026-10-25', 0);");
        db.execSQL("INSERT INTO NhiemVu (id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh) VALUES (7, 2, 'Đánh giá độ chính xác mô hình trên tập kiểm thử', 'Đo đạc độ chính xác và độ trễ nhận diện', '2026-11-15', 0);");

        // Nhiệm vụ Đồ án 3
        db.execSQL("INSERT INTO NhiemVu (id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh) VALUES (8, 3, 'Phân tích yêu cầu và thiết kế lược đồ CSDL MySQL', 'Hoàn thiện 8 bảng dữ liệu chuẩn hóa 3NF', '2026-08-20', 1);");
        db.execSQL("INSERT INTO NhiemVu (id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh) VALUES (9, 3, 'Xây dựng RESTful API cho quản lý sách và giỏ hàng', 'Viết backend Spring Boot', '2026-09-15', 1);");
        db.execSQL("INSERT INTO NhiemVu (id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh) VALUES (10, 3, 'Thiết kế giao diện người dùng Responsive bằng React', 'Giao diện thân thiện người dùng', '2026-10-01', 1);");
        db.execSQL("INSERT INTO NhiemVu (id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh) VALUES (11, 3, 'Kiểm thử chức năng thanh toán trực tuyến và nộp bài', 'Tích hợp cổng giả lập VnPay', '2026-10-12', 1);");

        // Nhiệm vụ Đồ án 4
        db.execSQL("INSERT INTO NhiemVu (id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh) VALUES (12, 4, 'Nghiên cứu thị trường và khảo sát nhu cầu người dùng', 'Khảo sát 200 phụ huynh học sinh', '2026-11-05', 0);");
        db.execSQL("INSERT INTO NhiemVu (id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh) VALUES (13, 4, 'Xây dựng Business Model Canvas (BMC)', 'Hoàn thiện mô hình kinh doanh', '2026-12-01', 0);");
        db.execSQL("INSERT INTO NhiemVu (id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh) VALUES (14, 4, 'Thiết kế bản mẫu Wireframe trên Figma', 'Thiết kế luồng đặt lịch học kèm', '2027-01-05', 0);");

        // 6. Chèn dữ liệu mẫu cho bảng CuocHen (7 cuộc hẹn)
        // Cuộc hẹn Đồ án 1
        db.execSQL("INSERT INTO CuocHen (id, do_an_id, tieu_de, thoi_gian, dia_diem, ghi_chu) VALUES (1, 1, 'Báo cáo đề cương chi tiết', '2026-09-15 09:00', 'Văn phòng Bộ môn KHMT - Phòng 402-A1', 'Thầy duyệt đề cương và phân công lịch báo cáo');");
        db.execSQL("INSERT INTO CuocHen (id, do_an_id, tieu_de, thoi_gian, dia_diem, ghi_chu) VALUES (2, 1, 'Góp ý thiết kế CSDL và giao diện', '2026-10-20 14:30', 'Phòng nghiên cứu Lab 501', 'Chuẩn bị bản vẽ kiến trúc ERD trước khi gặp');");
        db.execSQL("INSERT INTO CuocHen (id, do_an_id, tieu_de, thoi_gian, dia_diem, ghi_chu) VALUES (3, 1, 'Kiểm tra tiến độ hoàn thiện ứng dụng', '2026-11-28 08:30', 'Phòng 305-B1', 'Demo chạy thử tính năng SQLite và RecyclerView');");

        // Cuộc hẹn Đồ án 2
        db.execSQL("INSERT INTO CuocHen (id, do_an_id, tieu_de, thoi_gian, dia_diem, ghi_chu) VALUES (4, 2, 'Thống nhất yêu cầu thực tập và mô hình AI', '2026-08-25 10:00', 'Văn phòng Khoa CNTT - Bàn số 3', 'Gặp mặt cô hướng dẫn lần đầu');");
        db.execSQL("INSERT INTO CuocHen (id, do_an_id, tieu_de, thoi_gian, dia_diem, ghi_chu) VALUES (5, 2, 'Báo cáo kết quả huấn luyện mô hình', '2026-11-19 15:00', 'Google Meet (meet.google.com/xyz-abcd-efg)', 'Trình bày biểu đồ Loss/Accuracy và độ trễ');");

        // Cuộc hẹn Đồ án 3
        db.execSQL("INSERT INTO CuocHen (id, do_an_id, tieu_de, thoi_gian, dia_diem, ghi_chu) VALUES (6, 3, 'Báo cáo nghiệm thu sản phẩm website', '2026-10-14 16:00', 'Phòng máy 204-C4', 'Thầy đánh giá đạt điểm tối đa phần API và giao diện');");

        // Cuộc hẹn Đồ án 4
        db.execSQL("INSERT INTO CuocHen (id, do_an_id, tieu_de, thoi_gian, dia_diem, ghi_chu) VALUES (7, 4, 'Tư vấn cố vấn đề án khởi nghiệp', '2026-12-05 09:30', 'Phòng họp Tòa A', 'Gặp ban cố vấn để xin ý kiến định hướng');");
    }

    // Nâng cấp CSDL khi tăng DATABASE_VERSION
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS CuocHen");
        db.execSQL("DROP TABLE IF EXISTS NhiemVu");
        db.execSQL("DROP TABLE IF EXISTS DoAn");
        onCreate(db);
    }

    // ==========================================
    // CÁC HÀM XỬ LÝ DỮ LIỆU BẢNG ĐỒ ÁN (DoAn)
    // ==========================================

    // Lấy toàn bộ danh sách đồ án, sắp xếp mới nhất lên đầu
    public ArrayList<DoAn> getAllDoAn() {
        ArrayList<DoAn> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, ten, mo_ta, giang_vien, ngay_bat_dau, han_nop FROM DoAn ORDER BY id DESC", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String ten = cursor.getString(1);
            String moTa = cursor.getString(2);
            String giangVien = cursor.getString(3);
            String ngayBatDau = cursor.getString(4);
            String hanNop = cursor.getString(5);

            list.add(new DoAn(id, ten, moTa, giangVien, ngayBatDau, hanNop));
        }
        cursor.close();
        return list;
    }

    // Lấy thông tin chi tiết một đồ án theo ID
    public DoAn getDoAnById(int id) {
        DoAn doAn = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, ten, mo_ta, giang_vien, ngay_bat_dau, han_nop FROM DoAn WHERE id = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToNext()) {
            int doAnId = cursor.getInt(0);
            String ten = cursor.getString(1);
            String moTa = cursor.getString(2);
            String giangVien = cursor.getString(3);
            String ngayBatDau = cursor.getString(4);
            String hanNop = cursor.getString(5);

            doAn = new DoAn(doAnId, ten, moTa, giangVien, ngayBatDau, hanNop);
        }
        cursor.close();
        return doAn;
    }

    // Thêm một đồ án mới vào CSDL
    public void insertDoAn(String ten, String moTa, String giangVien, String ngayBatDau, String hanNop) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO DoAn (ten, mo_ta, giang_vien, ngay_bat_dau, han_nop) VALUES (?, ?, ?, ?, ?)";
        db.execSQL(sql, new Object[]{ten, moTa, giangVien, ngayBatDau, hanNop});
    }

    // Cập nhật thông tin một đồ án theo ID
    public void updateDoAn(int id, String ten, String moTa, String giangVien, String ngayBatDau, String hanNop) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE DoAn SET ten = ?, mo_ta = ?, giang_vien = ?, ngay_bat_dau = ?, han_nop = ? WHERE id = ?";
        db.execSQL(sql, new Object[]{ten, moTa, giangVien, ngayBatDau, hanNop, id});
    }

    // Xóa một đồ án theo ID (tự động xóa nhiệm vụ và cuộc hẹn nhờ ON DELETE CASCADE)
    public void deleteDoAn(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM DoAn WHERE id = ?";
        db.execSQL(sql, new Object[]{id});
    }

    // Tính tiến độ đồ án (số nhiệm vụ hoàn thành / tổng số nhiệm vụ)
    public String getTienDoDoAn(int doAnId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT COUNT(*), SUM(da_hoan_thanh) FROM NhiemVu WHERE do_an_id = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(doAnId)});

        int tong = 0;
        int daHoanThanh = 0;
        if (cursor.moveToNext()) {
            tong = cursor.getInt(0);
            daHoanThanh = cursor.getInt(1); // Nếu không có bản ghi nào thì trả về 0
        }
        cursor.close();

        // Xử lý trường hợp chưa có nhiệm vụ nào
        if (tong == 0) {
            return "0/0 (0%)";
        }

        int phanTram = (int) Math.round(((double) daHoanThanh * 100.0) / tong);
        return daHoanThanh + "/" + tong + " (" + phanTram + "%)";
    }

    // Lấy số liệu thống kê đồ án cho RecyclerView cuộn ngang (Mục C2: slide 185, 192)
    // Trả về mảng 4 số nguyên: [0]: Tổng nhiệm vụ, [1]: Đã xong, [2]: Chưa xong, [3]: Lịch hẹn sắp tới
    public int[] getThongKeDoAn(int doAnId) {
        int[] stats = new int[]{0, 0, 0, 0};
        SQLiteDatabase db = this.getReadableDatabase();

        // 1. Thống kê nhiệm vụ (tổng số, đã xong, chưa xong)
        Cursor c1 = db.rawQuery("SELECT COUNT(*), SUM(da_hoan_thanh) FROM NhiemVu WHERE do_an_id = ?", new String[]{String.valueOf(doAnId)});
        if (c1.moveToNext()) {
            stats[0] = c1.getInt(0);
            stats[1] = c1.getInt(1);
            stats[2] = stats[0] - stats[1];
        }
        c1.close();

        // 2. Thống kê cuộc hẹn sắp tới (thời gian >= hiện tại)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        Cursor c2 = db.rawQuery("SELECT COUNT(*) FROM CuocHen WHERE do_an_id = ? AND thoi_gian >= ?", new String[]{String.valueOf(doAnId), currentDateTime});
        if (c2.moveToNext()) {
            stats[3] = c2.getInt(0);
        }
        c2.close();

        return stats;
    }

    // ==========================================
    // CÁC HÀM XỬ LÝ DỮ LIỆU BẢNG NHIỆM VỤ (NhiemVu)
    // ==========================================

    // Lấy danh sách nhiệm vụ của một đồ án
    public ArrayList<NhiemVu> getNhiemVuByDoAnId(int doAnId) {
        ArrayList<NhiemVu> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh FROM NhiemVu WHERE do_an_id = ? ORDER BY id ASC";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(doAnId)});

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            int dId = cursor.getInt(1);
            String ten = cursor.getString(2);
            String ghiChu = cursor.getString(3);
            String hanHoanThanh = cursor.getString(4);
            int daHoanThanh = cursor.getInt(5);

            list.add(new NhiemVu(id, dId, ten, ghiChu, hanHoanThanh, daHoanThanh));
        }
        cursor.close();
        return list;
    }

    // Lấy thông tin một nhiệm vụ theo ID
    public NhiemVu getNhiemVuById(int id) {
        NhiemVu nv = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT id, do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh FROM NhiemVu WHERE id = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});

        if (cursor.moveToNext()) {
            int nvId = cursor.getInt(0);
            int dId = cursor.getInt(1);
            String ten = cursor.getString(2);
            String ghiChu = cursor.getString(3);
            String hanHoanThanh = cursor.getString(4);
            int daHoanThanh = cursor.getInt(5);

            nv = new NhiemVu(nvId, dId, ten, ghiChu, hanHoanThanh, daHoanThanh);
        }
        cursor.close();
        return nv;
    }

    // Thêm nhiệm vụ mới vào đồ án
    public void insertNhiemVu(int doAnId, String ten, String ghiChu, String hanHoanThanh) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO NhiemVu (do_an_id, ten, ghi_chu, han_hoan_thanh, da_hoan_thanh) VALUES (?, ?, ?, ?, 0)";
        db.execSQL(sql, new Object[]{doAnId, ten, ghiChu, hanHoanThanh});
    }

    // Cập nhật thông tin nhiệm vụ
    public void updateNhiemVu(int id, String ten, String ghiChu, String hanHoanThanh) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE NhiemVu SET ten = ?, ghi_chu = ?, han_hoan_thanh = ? WHERE id = ?";
        db.execSQL(sql, new Object[]{ten, ghiChu, hanHoanThanh, id});
    }

    // Cập nhật trạng thái hoàn thành (0 hoặc 1) của nhiệm vụ khi bấm vào CheckBox
    public void updateNhiemVuStatus(int id, int daHoanThanh) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE NhiemVu SET da_hoan_thanh = ? WHERE id = ?";
        db.execSQL(sql, new Object[]{daHoanThanh, id});
    }

    // Xóa nhiệm vụ theo ID
    public void deleteNhiemVu(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM NhiemVu WHERE id = ?";
        db.execSQL(sql, new Object[]{id});
    }

    // ==========================================
    // CÁC HÀM XỬ LÝ DỮ LIỆU BẢNG CUỘC HẸN (CuocHen)
    // ==========================================

    // Lấy danh sách cuộc hẹn của một đồ án, sắp xếp theo thời gian tăng dần
    public ArrayList<CuocHen> getCuocHenByDoAnId(int doAnId) {
        ArrayList<CuocHen> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT id, do_an_id, tieu_de, thoi_gian, dia_diem, ghi_chu FROM CuocHen WHERE do_an_id = ? ORDER BY thoi_gian ASC";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(doAnId)});

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            int dId = cursor.getInt(1);
            String tieuDe = cursor.getString(2);
            String thoiGian = cursor.getString(3);
            String diaDiem = cursor.getString(4);
            String ghiChu = cursor.getString(5);

            list.add(new CuocHen(id, dId, tieuDe, thoiGian, diaDiem, ghiChu));
        }
        cursor.close();
        return list;
    }

    // Lấy thông tin một cuộc hẹn theo ID
    public CuocHen getCuocHenById(int id) {
        CuocHen ch = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT id, do_an_id, tieu_de, thoi_gian, dia_diem, ghi_chu FROM CuocHen WHERE id = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});

        if (cursor.moveToNext()) {
            int chId = cursor.getInt(0);
            int dId = cursor.getInt(1);
            String tieuDe = cursor.getString(2);
            String thoiGian = cursor.getString(3);
            String diaDiem = cursor.getString(4);
            String ghiChu = cursor.getString(5);

            ch = new CuocHen(chId, dId, tieuDe, thoiGian, diaDiem, ghiChu);
        }
        cursor.close();
        return ch;
    }

    // Thêm cuộc hẹn mới vào đồ án
    public void insertCuocHen(int doAnId, String tieuDe, String thoiGian, String diaDiem, String ghiChu) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO CuocHen (do_an_id, tieu_de, thoi_gian, dia_diem, ghi_chu) VALUES (?, ?, ?, ?, ?)";
        db.execSQL(sql, new Object[]{doAnId, tieuDe, thoiGian, diaDiem, ghiChu});
    }

    // Cập nhật thông tin cuộc hẹn
    public void updateCuocHen(int id, String tieuDe, String thoiGian, String diaDiem, String ghiChu) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE CuocHen SET tieu_de = ?, thoi_gian = ?, dia_diem = ?, ghi_chu = ? WHERE id = ?";
        db.execSQL(sql, new Object[]{tieuDe, thoiGian, diaDiem, ghiChu, id});
    }

    // Xóa cuộc hẹn theo ID
    public void deleteCuocHen(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM CuocHen WHERE id = ?";
        db.execSQL(sql, new Object[]{id});
    }
}
