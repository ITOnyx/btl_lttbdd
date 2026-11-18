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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Màn hình xem thông tin chi tiết một đồ án và điều hướng sang nhiệm vụ / cuộc hẹn
// Minh họa LinearLayoutManager cuộn ngang theo bài giảng (slide 185, 192)
public class DoAnDetailActivity extends AppCompatActivity {

    // Hằng số chung truyền ID đồ án qua Intent (Yêu cầu 6)
    public static final String EXTRA_DO_AN_ID = "do_an_id";

    private Toolbar mToolbarDetail;
    private TextView mTvDetailTen;
    private TextView mTvDetailMoTa;
    private TextView mTvDetailGiangVien;
    private TextView mTvDetailThoiGian;
    private TextView mTvDetailTienDo;
    private ProgressBar mProgressBarDetail;

    // Mục C2: RecyclerView cuộn ngang hiển thị các thẻ thống kê nhỏ (slide 185, 192)
    private RecyclerView mRvThongKe;
    private ThongKeAdapter mThongKeAdapter;
    private ArrayList<ThongKeItem> mListThongKe;

    private Button mBtnXemNhiemVu;
    private Button mBtnXemCuocHen;
    private Button mBtnXemDongThoiGian;
    private Button mBtnSuaDoAn;
    private Button mBtnXoaDoAn;
    private Button mBtnQuayLaiDetail;

    private DBHelper mDbHelper;
    private int mDoAnId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_an_detail);

        mDbHelper = new DBHelper(this);

        // Đọc ID đồ án truyền từ màn hình danh sách bằng hằng số EXTRA_DO_AN_ID
        mDoAnId = getIntent().getIntExtra(EXTRA_DO_AN_ID, -1);
        if (mDoAnId == -1) {
            Toast.makeText(this, "Không tìm thấy đồ án!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ các View bằng findViewById
        mToolbarDetail = findViewById(R.id.toolbarDetail);
        mTvDetailTen = findViewById(R.id.tvDetailTen);
        mTvDetailMoTa = findViewById(R.id.tvDetailMoTa);
        mTvDetailGiangVien = findViewById(R.id.tvDetailGiangVien);
        mTvDetailThoiGian = findViewById(R.id.tvDetailThoiGian);
        mTvDetailTienDo = findViewById(R.id.tvDetailTienDo);
        mProgressBarDetail = findViewById(R.id.progressBarDetail);

        // Mục C2: Khởi tạo RecyclerView cuộn ngang
        mRvThongKe = findViewById(R.id.rv_thong_ke);
        mListThongKe = new ArrayList<>();
        mThongKeAdapter = new ThongKeAdapter(this, mListThongKe);
        mRvThongKe.setAdapter(mThongKeAdapter);

        // Cài đặt LinearLayoutManager dạng cuộn NGANG (slide 185, 192)
        mRvThongKe.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mBtnXemNhiemVu = findViewById(R.id.btnXemNhiemVu);
        mBtnXemCuocHen = findViewById(R.id.btnXemCuocHen);
        mBtnXemDongThoiGian = findViewById(R.id.btnXemDongThoiGian);
        mBtnSuaDoAn = findViewById(R.id.btnSuaDoAn);
        mBtnXoaDoAn = findViewById(R.id.btnXoaDoAn);
        mBtnQuayLaiDetail = findViewById(R.id.btnQuayLaiDetail);

        // Cấu hình nút Back trên Toolbar
        mToolbarDetail.setNavigationOnClickListener(v -> finish());

        // Bấm nút xem danh sách Nhiệm vụ
        mBtnXemNhiemVu.setOnClickListener(v -> {
            Intent intent = new Intent(DoAnDetailActivity.this, NhiemVuListActivity.class);
            intent.putExtra(NhiemVuListActivity.EXTRA_DO_AN_ID, mDoAnId);
            startActivity(intent);
        });

        // Bấm nút xem danh sách Cuộc hẹn
        mBtnXemCuocHen.setOnClickListener(v -> {
            Intent intent = new Intent(DoAnDetailActivity.this, CuocHenListActivity.class);
            intent.putExtra(CuocHenListActivity.EXTRA_DO_AN_ID, mDoAnId);
            startActivity(intent);
        });

        // Bấm nút xem Dòng thời gian đồ án (Mục D1: Multiple View Type)
        mBtnXemDongThoiGian.setOnClickListener(v -> {
            Intent intent = new Intent(DoAnDetailActivity.this, TimelineActivity.class);
            intent.putExtra(TimelineActivity.EXTRA_DO_AN_ID, mDoAnId);
            startActivity(intent);
        });

        // Bấm nút chỉnh sửa đồ án
        mBtnSuaDoAn.setOnClickListener(v -> {
            Intent intent = new Intent(DoAnDetailActivity.this, DoAnFormActivity.class);
            intent.putExtra(DoAnFormActivity.EXTRA_DO_AN_ID, mDoAnId);
            startActivity(intent);
        });

        // Bấm nút xóa đồ án
        mBtnXoaDoAn.setOnClickListener(v -> hienThiHopThoaiXacNhanXoa());

        // Bấm nút quay lại
        mBtnQuayLaiDetail.setOnClickListener(v -> finish());
    }

    // Khi Activity hiển thị lại, tải lại dữ liệu mới nhất
    @Override
    protected void onResume() {
        super.onResume();
        loadDoAnData();
    }

    // Đọc thông tin đồ án và tiến độ từ SQLite hiển thị lên giao diện
    private void loadDoAnData() {
        DoAn doAn = mDbHelper.getDoAnById(mDoAnId);
        if (doAn == null) {
            finish();
            return;
        }

        // Gán thông tin lên các TextView
        mTvDetailTen.setText(doAn.getTen());

        if (doAn.getMoTa() != null && !doAn.getMoTa().trim().isEmpty()) {
            mTvDetailMoTa.setVisibility(View.VISIBLE);
            mTvDetailMoTa.setText(doAn.getMoTa());
        } else {
            mTvDetailMoTa.setVisibility(View.GONE);
        }

        mTvDetailGiangVien.setText("GVHD: " + (doAn.getGiangVien() != null ? doAn.getGiangVien() : "Chưa có"));

        String tg = "Từ: " + (doAn.getNgayBatDau() != null ? doAn.getNgayBatDau() : "---")
                + "  đến  " + (doAn.getHanNop() != null ? doAn.getHanNop() : "---");
        mTvDetailThoiGian.setText(tg);

        // Hiển thị tiến độ từ DBHelper
        String tienDoStr = mDbHelper.getTienDoDoAn(mDoAnId);
        mTvDetailTienDo.setText(tienDoStr);

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
        mProgressBarDetail.setProgress(percent);

        // Mục C2: Nạp dữ liệu 4 thẻ thống kê nhanh vào RecyclerView cuộn ngang
        int[] stats = mDbHelper.getThongKeDoAn(mDoAnId);
        mListThongKe.clear();
        mListThongKe.add(new ThongKeItem("Tổng nhiệm vụ", String.valueOf(stats[0]), R.drawable.ic_clock));
        mListThongKe.add(new ThongKeItem("Đã xong", String.valueOf(stats[1]), R.drawable.ic_calendar));
        mListThongKe.add(new ThongKeItem("Chưa xong", String.valueOf(stats[2]), R.drawable.ic_clock));
        mListThongKe.add(new ThongKeItem("Lịch hẹn sắp tới", String.valueOf(stats[3]), R.drawable.ic_calendar));
        mThongKeAdapter.notifyDataSetChanged();
    }

    // Hiển thị hộp thoại AlertDialog xác nhận trước khi xóa đồ án
    private void hienThiHopThoaiXacNhanXoa() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa đồ án");
        builder.setMessage("Bạn có chắc chắn muốn xóa đồ án này không?\n\nToàn bộ nhiệm vụ và lịch hẹn liên quan sẽ tự động bị xóa.");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            // Xóa đồ án khỏi CSDL (Nhiệm vụ và Cuộc hẹn sẽ tự động bị xóa nhờ ON DELETE CASCADE)
            mDbHelper.deleteDoAn(mDoAnId);
            Toast.makeText(DoAnDetailActivity.this, "Đã xóa đồ án thành công!", Toast.LENGTH_SHORT).show();
            finish();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
