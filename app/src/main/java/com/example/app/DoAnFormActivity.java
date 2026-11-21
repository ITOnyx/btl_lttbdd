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

// Màn hình thêm mới hoặc chỉnh sửa thông tin đồ án
public class DoAnFormActivity extends AppCompatActivity {

    // Hằng số Intent nhận ID đồ án
    public static final String EXTRA_DO_AN_ID = "do_an_id";

    private EditText edtTen;
    private EditText edtGiangVien;
    private EditText edtMoTa;
    private EditText edtNgayBatDau;
    private EditText edtHanNop;
    private Button btnChonNgayBatDau;
    private Button btnChonHanNop;
    private Button btnLuu;
    private Button btnQuayLai;
    private Toolbar toolbar;

    private DBHelper dbHelper;
    private int doAnId = -1; // -1: Thêm mới, khác -1: Cập nhật

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_an_form);

        // Khởi tạo DBHelper
        dbHelper = new DBHelper(this);

        // Ánh xạ các thành phần giao diện bằng findViewById
        toolbar = findViewById(R.id.toolbar);
        edtTen = findViewById(R.id.edtTen);
        edtGiangVien = findViewById(R.id.edtGiangVien);
        edtMoTa = findViewById(R.id.edtMoTa);
        edtNgayBatDau = findViewById(R.id.edtNgayBatDau);
        edtHanNop = findViewById(R.id.edtHanNop);
        btnChonNgayBatDau = findViewById(R.id.btnChonNgayBatDau);
        btnChonHanNop = findViewById(R.id.btnChonHanNop);
        btnLuu = findViewById(R.id.btnLuu);
        btnQuayLai = findViewById(R.id.btnQuayLai);

        // Cấu hình nút Back trên Toolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Đọc ID đồ án truyền từ Intent (nếu có) bằng hằng số EXTRA_DO_AN_ID
        doAnId = getIntent().getIntExtra(EXTRA_DO_AN_ID, -1);

        if (doAnId != -1) {
            // Chế độ chỉnh sửa đồ án
            toolbar.setTitle("Cập nhật đồ án");
            btnLuu.setText("Lưu thay đổi");
            loadDoAnData(doAnId);
        } else {
            // Chế độ thêm mới đồ án
            toolbar.setTitle("Thêm đồ án mới");
            btnLuu.setText("Thêm đồ án");
        }

        // Bắt sự kiện chọn ngày bắt đầu
        btnChonNgayBatDau.setOnClickListener(v -> moHopThoaiChonNgay(edtNgayBatDau));
        edtNgayBatDau.setOnClickListener(v -> moHopThoaiChonNgay(edtNgayBatDau));

        // Bắt sự kiện chọn hạn nộp
        btnChonHanNop.setOnClickListener(v -> moHopThoaiChonNgay(edtHanNop));
        edtHanNop.setOnClickListener(v -> moHopThoaiChonNgay(edtHanNop));

        // Bắt sự kiện nút Lưu
        btnLuu.setOnClickListener(v -> luuDoAn());

        // Bắt sự kiện nút Quay lại
        btnQuayLai.setOnClickListener(v -> finish());
    }

    // Nạp dữ liệu cũ của đồ án vào các ô nhập liệu khi ở chế độ chỉnh sửa
    private void loadDoAnData(int id) {
        DoAn doAn = dbHelper.getDoAnById(id);
        if (doAn != null) {
            edtTen.setText(doAn.getTen());
            edtGiangVien.setText(doAn.getGiangVien() != null ? doAn.getGiangVien() : "");
            edtMoTa.setText(doAn.getMoTa() != null ? doAn.getMoTa() : "");
            edtNgayBatDau.setText(doAn.getNgayBatDau() != null ? doAn.getNgayBatDau() : "");
            edtHanNop.setText(doAn.getHanNop() != null ? doAn.getHanNop() : "");
        }
    }

    // Mở DatePickerDialog để người dùng chọn ngày và điền vào ô EditText theo định dạng yyyy-MM-dd
    private void moHopThoaiChonNgay(EditText targetEdt) {
        Calendar cal = Calendar.getInstance();
        int nam = cal.get(Calendar.YEAR);
        int thang = cal.get(Calendar.MONTH);
        int ngay = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            // Định dạng chuỗi ngày chuẩn yyyy-MM-dd
            String dateStr = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            targetEdt.setText(dateStr);
        }, nam, thang, ngay);

        dialog.show();
    }

    // Kiểm tra tính hợp lệ của dữ liệu và lưu vào CSDL SQLite
    private void luuDoAn() {
        String ten = edtTen.getText().toString().trim();
        String giangVien = edtGiangVien.getText().toString().trim();
        String moTa = edtMoTa.getText().toString().trim();
        String ngayBatDau = edtNgayBatDau.getText().toString().trim();
        String hanNop = edtHanNop.getText().toString().trim();

        // Kiểm tra tên đồ án không được để trống
        if (ten.isEmpty()) {
            edtTen.setError("Vui lòng nhập tên đồ án!");
            edtTen.requestFocus();
            return;
        }

        // Kiểm tra hạn nộp không được trước ngày bắt đầu (so sánh chuỗi yyyy-MM-dd)
        if (!ngayBatDau.isEmpty() && !hanNop.isEmpty()) {
            if (hanNop.compareTo(ngayBatDau) < 0) {
                Toast.makeText(this, "Hạn nộp không được trước ngày bắt đầu!", Toast.LENGTH_LONG).show();
                return;
            }
        }

        // Khi sửa đồ án: hạn nộp mới không được sớm hơn hạn của nhiệm vụ muộn nhất đã có
        if (doAnId != -1 && !hanNop.isEmpty()) {
            String hanNhiemVuMuonNhat = dbHelper.getHanNhiemVuMuonNhat(doAnId);
            if (hanNhiemVuMuonNhat != null && hanNop.compareTo(hanNhiemVuMuonNhat) < 0) {
                Toast.makeText(this, "Hạn nộp không được trước hạn của nhiệm vụ muộn nhất (" + hanNhiemVuMuonNhat + ")!", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (doAnId == -1) {
            // Thêm mới vào SQLite
            dbHelper.insertDoAn(ten, moTa, giangVien, ngayBatDau, hanNop);
            Toast.makeText(this, "Thêm đồ án thành công!", Toast.LENGTH_SHORT).show();
        } else {
            // Cập nhật đồ án vào SQLite
            dbHelper.updateDoAn(doAnId, ten, moTa, giangVien, ngayBatDau, hanNop);
            Toast.makeText(this, "Cập nhật đồ án thành công!", Toast.LENGTH_SHORT).show();
        }

        // Đóng Activity và quay về màn hình trước
        finish();
    }
}
