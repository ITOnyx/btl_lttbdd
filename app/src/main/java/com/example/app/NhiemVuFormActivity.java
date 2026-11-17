package com.example.app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;
import java.util.Locale;

// Màn hình thêm mới hoặc cập nhật thông tin nhiệm vụ của đồ án
public class NhiemVuFormActivity extends AppCompatActivity {

    private Toolbar toolbarNhiemVuForm;
    private EditText edtTenNhiemVu;
    private EditText edtGhiChu;
    private EditText edtHanHoanThanh;
    private Button btnChonHanHoanThanh;
    private Button btnLuuNhiemVu;
    private Button btnQuayLaiNhiemVu;

    private DBHelper dbHelper;
    private int doAnId = -1;
    private int nhiemVuId = -1; // -1: Thêm mới, khác -1: Cập nhật

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhiem_vu_form);

        dbHelper = new DBHelper(this);

        // Đọc ID đồ án và ID nhiệm vụ từ Intent
        doAnId = getIntent().getIntExtra("do_an_id", -1);
        nhiemVuId = getIntent().getIntExtra("nhiem_vu_id", -1);

        // Ánh xạ các View
        toolbarNhiemVuForm = findViewById(R.id.toolbarNhiemVuForm);
        edtTenNhiemVu = findViewById(R.id.edtTenNhiemVu);
        edtGhiChu = findViewById(R.id.edtGhiChu);
        edtHanHoanThanh = findViewById(R.id.edtHanHoanThanh);
        btnChonHanHoanThanh = findViewById(R.id.btnChonHanHoanThanh);
        btnLuuNhiemVu = findViewById(R.id.btnLuuNhiemVu);
        btnQuayLaiNhiemVu = findViewById(R.id.btnQuayLaiNhiemVu);

        // Cấu hình nút Back trên Toolbar
        toolbarNhiemVuForm.setNavigationOnClickListener(v -> finish());

        if (nhiemVuId != -1) {
            // Chế độ sửa nhiệm vụ
            toolbarNhiemVuForm.setTitle("Cập nhật nhiệm vụ");
            btnLuuNhiemVu.setText("Lưu thay đổi");
            loadNhiemVuData(nhiemVuId);
        } else {
            // Chế độ thêm nhiệm vụ mới
            toolbarNhiemVuForm.setTitle("Thêm nhiệm vụ mới");
            btnLuuNhiemVu.setText("Thêm nhiệm vụ");
        }

        // Bắt sự kiện chọn hạn hoàn thành
        btnChonHanHoanThanh.setOnClickListener(v -> moHopThoaiChonNgay());
        edtHanHoanThanh.setOnClickListener(v -> moHopThoaiChonNgay());

        // Bắt sự kiện lưu nhiệm vụ
        btnLuuNhiemVu.setOnClickListener(v -> luuNhiemVu());

        // Bắt sự kiện quay lại
        btnQuayLaiNhiemVu.setOnClickListener(v -> finish());
    }

    // Đổ dữ liệu cũ của nhiệm vụ lên form khi chỉnh sửa
    private void loadNhiemVuData(int id) {
        NhiemVu nv = dbHelper.getNhiemVuById(id);
        if (nv != null) {
            edtTenNhiemVu.setText(nv.getTen());
            edtGhiChu.setText(nv.getGhiChu() != null ? nv.getGhiChu() : "");
            edtHanHoanThanh.setText(nv.getHanHoanThanh() != null ? nv.getHanHoanThanh() : "");
        }
    }

    // Mở DatePickerDialog để chọn ngày hạn nộp chuẩn yyyy-MM-dd
    private void moHopThoaiChonNgay() {
        Calendar cal = Calendar.getInstance();
        int nam = cal.get(Calendar.YEAR);
        int thang = cal.get(Calendar.MONTH);
        int ngay = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            edtHanHoanThanh.setText(dateStr);
        }, nam, thang, ngay);

        dialog.show();
    }

    // Kiểm tra dữ liệu và lưu vào SQLite
    private void luuNhiemVu() {
        String ten = edtTenNhiemVu.getText().toString().trim();
        String ghiChu = edtGhiChu.getText().toString().trim();
        String hanHoanThanh = edtHanHoanThanh.getText().toString().trim();

        if (ten.isEmpty()) {
            edtTenNhiemVu.setError("Vui lòng nhập tên nhiệm vụ!");
            edtTenNhiemVu.requestFocus();
            return;
        }

        if (nhiemVuId == -1) {
            // Thêm mới nhiệm vụ vào CSDL
            dbHelper.insertNhiemVu(doAnId, ten, ghiChu, hanHoanThanh);
            Toast.makeText(this, "Thêm nhiệm vụ thành công!", Toast.LENGTH_SHORT).show();
        } else {
            // Cập nhật nhiệm vụ vào CSDL
            dbHelper.updateNhiemVu(nhiemVuId, ten, ghiChu, hanHoanThanh);
            Toast.makeText(this, "Cập nhật nhiệm vụ thành công!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
