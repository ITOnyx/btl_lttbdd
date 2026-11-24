package com.example.app;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;
import java.util.Locale;

// Màn hình thêm mới hoặc cập nhật thông tin cuộc hẹn trao đổi với giảng viên
public class CuocHenFormActivity extends AppCompatActivity {

    // Hằng số Intent nhận ID đồ án và ID cuộc hẹn
    public static final String EXTRA_DO_AN_ID = "do_an_id";
    public static final String EXTRA_CUOC_HEN_ID = "cuoc_hen_id";

    private Toolbar toolbarCuocHenForm;
    private EditText edtTieuDe;
    private EditText edtThoiGian;
    private EditText edtDiaDiem;
    private EditText edtGhiChu;
    private Button btnChonThoiGian;
    private Button btnLuuCuocHen;
    private Button btnQuayLaiCuocHen;

    private DBHelper dbHelper;
    private int doAnId = -1;
    private int cuocHenId = -1; // -1: Thêm mới, khác -1: Cập nhật

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuoc_hen_form);

        dbHelper = new DBHelper(this);

        // Đọc ID đồ án và ID cuộc hẹn từ Intent bằng hằng số
        doAnId = getIntent().getIntExtra(EXTRA_DO_AN_ID, -1);
        cuocHenId = getIntent().getIntExtra(EXTRA_CUOC_HEN_ID, -1);

        // Ánh xạ các thành phần giao diện
        toolbarCuocHenForm = findViewById(R.id.toolbarCuocHenForm);
        edtTieuDe = findViewById(R.id.edtTieuDe);
        edtThoiGian = findViewById(R.id.edtThoiGian);
        edtDiaDiem = findViewById(R.id.edtDiaDiem);
        edtGhiChu = findViewById(R.id.edtGhiChu);
        btnChonThoiGian = findViewById(R.id.btnChonThoiGian);
        btnLuuCuocHen = findViewById(R.id.btnLuuCuocHen);
        btnQuayLaiCuocHen = findViewById(R.id.btnQuayLaiCuocHen);

        // Cấu hình nút Back trên Toolbar
        toolbarCuocHenForm.setNavigationOnClickListener(v -> finish());

        if (cuocHenId != -1) {
            // Chế độ chỉnh sửa cuộc hẹn
            toolbarCuocHenForm.setTitle("Cập nhật cuộc hẹn");
            btnLuuCuocHen.setText("Lưu thay đổi");
            loadCuocHenData(cuocHenId);
        } else {
            // Chế độ thêm cuộc hẹn mới
            toolbarCuocHenForm.setTitle("Thêm cuộc hẹn mới");
            btnLuuCuocHen.setText("Thêm cuộc hẹn");
        }

        // Bắt sự kiện chọn ngày & giờ
        btnChonThoiGian.setOnClickListener(v -> moHopThoaiChonNgayGio());
        edtThoiGian.setOnClickListener(v -> moHopThoaiChonNgayGio());

        // Bắt sự kiện lưu cuộc hẹn
        btnLuuCuocHen.setOnClickListener(v -> luuCuocHen());

        // Bắt sự kiện quay lại
        btnQuayLaiCuocHen.setOnClickListener(v -> finish());
    }

    // Đổ dữ liệu cũ của cuộc hẹn lên form khi ở chế độ chỉnh sửa
    private void loadCuocHenData(int id) {
        CuocHen ch = dbHelper.getCuocHenById(id);
        if (ch != null) {
            edtTieuDe.setText(ch.getTieuDe());
            edtThoiGian.setText(ch.getThoiGian() != null ? ch.getThoiGian() : "");
            edtDiaDiem.setText(ch.getDiaDiem() != null ? ch.getDiaDiem() : "");
            edtGhiChu.setText(ch.getGhiChu() != null ? ch.getGhiChu() : "");
        }
    }

    // Mở liên tiếp DatePickerDialog và TimePickerDialog để chọn ngày giờ định dạng yyyy-MM-dd HH:mm
    private void moHopThoaiChonNgayGio() {
        // Mở sẵn đúng ngày giờ đang có trong ô (khi sửa); ô trống thì mở thời điểm hiện tại
        Calendar cal = DBHelper.chuoiSangLich(edtThoiGian.getText().toString());
        int nam = cal.get(Calendar.YEAR);
        int thang = cal.get(Calendar.MONTH);
        int ngay = cal.get(Calendar.DAY_OF_MONTH);
        int gio = cal.get(Calendar.HOUR_OF_DAY);
        int phut = cal.get(Calendar.MINUTE);

        // 1. Mở DatePickerDialog chọn ngày
        DatePickerDialog dateDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String selectedDate = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);

            // 2. Mở tiếp TimePickerDialog chọn giờ và phút
            TimePickerDialog timeDialog = new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                String fullDateTime = String.format(Locale.US, "%s %02d:%02d", selectedDate, hourOfDay, minute);
                edtThoiGian.setText(fullDateTime);
            }, gio, phut, true);

            timeDialog.show();
        }, nam, thang, ngay);

        dateDialog.show();
    }

    // Kiểm tra tính hợp lệ và lưu cuộc hẹn vào CSDL SQLite
    private void luuCuocHen() {
        String tieuDe = edtTieuDe.getText().toString().trim();
        String thoiGian = edtThoiGian.getText().toString().trim();
        String diaDiem = edtDiaDiem.getText().toString().trim();
        String ghiChu = edtGhiChu.getText().toString().trim();

        if (tieuDe.isEmpty()) {
            edtTieuDe.setError("Vui lòng nhập tiêu đề cuộc hẹn!");
            edtTieuDe.requestFocus();
            return;
        }

        if (thoiGian.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn thời gian cuộc hẹn!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cuocHenId == -1) {
            // Thêm mới cuộc hẹn
            dbHelper.insertCuocHen(doAnId, tieuDe, thoiGian, diaDiem, ghiChu);
            Toast.makeText(this, "Thêm cuộc hẹn thành công!", Toast.LENGTH_SHORT).show();
        } else {
            // Cập nhật cuộc hẹn
            dbHelper.updateCuocHen(cuocHenId, tieuDe, thoiGian, diaDiem, ghiChu);
            Toast.makeText(this, "Cập nhật cuộc hẹn thành công!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
