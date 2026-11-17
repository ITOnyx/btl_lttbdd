package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

// Màn hình xem thông tin chi tiết một đồ án và điều hướng sang nhiệm vụ / cuộc hẹn
public class DoAnDetailActivity extends AppCompatActivity {

    private Toolbar toolbarDetail;
    private TextView tvDetailTen;
    private TextView tvDetailMoTa;
    private TextView tvDetailGiangVien;
    private TextView tvDetailThoiGian;
    private TextView tvDetailTienDo;
    private ProgressBar progressBarDetail;

    private Button btnXemNhiemVu;
    private Button btnXemCuocHen;
    private Button btnSuaDoAn;
    private Button btnXoaDoAn;
    private Button btnQuayLaiDetail;

    private DBHelper dbHelper;
    private int doAnId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_an_detail);

        dbHelper = new DBHelper(this);

        // Đọc ID đồ án truyền từ màn hình danh sách
        doAnId = getIntent().getIntExtra("do_an_id", -1);
        if (doAnId == -1) {
            Toast.makeText(this, "Không tìm thấy đồ án!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ các View bằng findViewById
        toolbarDetail = findViewById(R.id.toolbarDetail);
        tvDetailTen = findViewById(R.id.tvDetailTen);
        tvDetailMoTa = findViewById(R.id.tvDetailMoTa);
        tvDetailGiangVien = findViewById(R.id.tvDetailGiangVien);
        tvDetailThoiGian = findViewById(R.id.tvDetailThoiGian);
        tvDetailTienDo = findViewById(R.id.tvDetailTienDo);
        progressBarDetail = findViewById(R.id.progressBarDetail);

        btnXemNhiemVu = findViewById(R.id.btnXemNhiemVu);
        btnXemCuocHen = findViewById(R.id.btnXemCuocHen);
        btnSuaDoAn = findViewById(R.id.btnSuaDoAn);
        btnXoaDoAn = findViewById(R.id.btnXoaDoAn);
        btnQuayLaiDetail = findViewById(R.id.btnQuayLaiDetail);

        // Cấu hình nút Back trên Toolbar
        toolbarDetail.setNavigationOnClickListener(v -> finish());

        // Bấm nút xem danh sách Nhiệm vụ
        btnXemNhiemVu.setOnClickListener(v -> {
            Intent intent = new Intent(DoAnDetailActivity.this, NhiemVuListActivity.class);
            intent.putExtra("do_an_id", doAnId);
            startActivity(intent);
        });

        // Bấm nút xem danh sách Cuộc hẹn
        btnXemCuocHen.setOnClickListener(v -> {
            Intent intent = new Intent(DoAnDetailActivity.this, CuocHenListActivity.class);
            intent.putExtra("do_an_id", doAnId);
            startActivity(intent);
        });

        // Bấm nút chỉnh sửa đồ án
        btnSuaDoAn.setOnClickListener(v -> {
            Intent intent = new Intent(DoAnDetailActivity.this, DoAnFormActivity.class);
            intent.putExtra("do_an_id", doAnId);
            startActivity(intent);
        });

        // Bấm nút xóa đồ án
        btnXoaDoAn.setOnClickListener(v -> hienThiHopThoaiXacNhanXoa());

        // Bấm nút quay lại
        btnQuayLaiDetail.setOnClickListener(v -> finish());
    }

    // Khi Activity hiển thị lại (ví dụ sau khi sửa hoặc thay đổi nhiệm vụ), tải lại dữ liệu mới nhất
    @Override
    protected void onResume() {
        super.onResume();
        loadDoAnData();
    }

    // Đọc thông tin đồ án và tiến độ từ SQLite hiển thị lên giao diện
    private void loadDoAnData() {
        DoAn doAn = dbHelper.getDoAnById(doAnId);
        if (doAn == null) {
            finish();
            return;
        }

        // Gán thông tin lên các TextView
        tvDetailTen.setText(doAn.getTen());

        if (doAn.getMoTa() != null && !doAn.getMoTa().trim().isEmpty()) {
            tvDetailMoTa.setVisibility(View.VISIBLE);
            tvDetailMoTa.setText(doAn.getMoTa());
        } else {
            tvDetailMoTa.setVisibility(View.GONE);
        }

        tvDetailGiangVien.setText("GVHD: " + (doAn.getGiangVien() != null ? doAn.getGiangVien() : "Chưa có"));

        String tg = "Từ: " + (doAn.getNgayBatDau() != null ? doAn.getNgayBatDau() : "---")
                + "  đến  " + (doAn.getHanNop() != null ? doAn.getHanNop() : "---");
        tvDetailThoiGian.setText(tg);

        // Hiển thị tiến độ từ DBHelper
        String tienDoStr = dbHelper.getTienDoDoAn(doAnId);
        tvDetailTienDo.setText(tienDoStr);

        int percent = 0;
        try {
            if (tienDoStr.contains("(") && tienDoStr.contains("%")) {
                int start = tienDoStr.indexOf("(") + 1;
                int end = tienDoStr.indexOf("%");
                percent = Integer.parseInt(tienDoStr.substring(start, end).trim());
            }
        } catch (Exception ignored) {
            percent = 0;
        }
        progressBarDetail.setProgress(percent);
    }

    // Hiển thị hộp thoại AlertDialog xác nhận trước khi xóa đồ án
    private void hienThiHopThoaiXacNhanXoa() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa đồ án");
        builder.setMessage("Bạn có chắc chắn muốn xóa đồ án này không?\n\nToàn bộ nhiệm vụ và lịch hẹn liên quan sẽ tự động bị xóa.");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            // Xóa đồ án khỏi CSDL (Nhiệm vụ và Cuộc hẹn sẽ tự động bị xóa nhờ ON DELETE CASCADE)
            dbHelper.deleteDoAn(doAnId);
            Toast.makeText(DoAnDetailActivity.this, "Đã xóa đồ án thành công!", Toast.LENGTH_SHORT).show();
            finish();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
