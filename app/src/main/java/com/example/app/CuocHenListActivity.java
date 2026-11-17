package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

// Màn hình hiển thị và quản lý danh sách cuộc hẹn trao đổi với giảng viên
public class CuocHenListActivity extends AppCompatActivity implements CuocHenAdapter.OnCuocHenClickListener {

    private Toolbar toolbarCuocHen;
    private RecyclerView rvCuocHen;
    private LinearLayout layoutEmptyCuocHen;
    private FloatingActionButton fabAddCuocHen;

    private DBHelper dbHelper;
    private int doAnId = -1;
    private ArrayList<CuocHen> listCuocHen;
    private CuocHenAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuoc_hen_list);

        dbHelper = new DBHelper(this);

        // Đọc ID đồ án từ Intent
        doAnId = getIntent().getIntExtra("do_an_id", -1);
        if (doAnId == -1) {
            Toast.makeText(this, "Không tìm thấy đồ án!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ các View
        toolbarCuocHen = findViewById(R.id.toolbarCuocHen);
        rvCuocHen = findViewById(R.id.rvCuocHen);
        layoutEmptyCuocHen = findViewById(R.id.layoutEmptyCuocHen);
        fabAddCuocHen = findViewById(R.id.fabAddCuocHen);

        // Cấu hình nút Back trên Toolbar
        toolbarCuocHen.setNavigationOnClickListener(v -> finish());

        // Đặt tiêu đề Toolbar theo tên đồ án
        DoAn doAn = dbHelper.getDoAnById(doAnId);
        if (doAn != null) {
            toolbarCuocHen.setTitle("Lịch hẹn: " + doAn.getTen());
        }

        // Khởi tạo danh sách và RecyclerView Adapter
        listCuocHen = new ArrayList<>();
        adapter = new CuocHenAdapter(this, listCuocHen, this);
        rvCuocHen.setLayoutManager(new LinearLayoutManager(this));
        rvCuocHen.setAdapter(adapter);

        // Bắt sự kiện bấm nút thêm cuộc hẹn mới
        fabAddCuocHen.setOnClickListener(v -> {
            Intent intent = new Intent(CuocHenListActivity.this, CuocHenFormActivity.class);
            intent.putExtra("do_an_id", doAnId);
            startActivity(intent);
        });
    }

    // Tải lại danh sách cuộc hẹn khi quay trở lại màn hình
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    // Đọc danh sách cuộc hẹn từ SQLite (sắp xếp tăng dần theo thời gian)
    private void loadData() {
        listCuocHen.clear();
        listCuocHen.addAll(dbHelper.getCuocHenByDoAnId(doAnId));
        adapter.notifyDataSetChanged();

        if (listCuocHen.isEmpty()) {
            layoutEmptyCuocHen.setVisibility(View.VISIBLE);
            rvCuocHen.setVisibility(View.GONE);
        } else {
            layoutEmptyCuocHen.setVisibility(View.GONE);
            rvCuocHen.setVisibility(View.VISIBLE);
        }
    }

    // Chuyển sang màn hình chỉnh sửa cuộc hẹn
    @Override
    public void onEdit(CuocHen ch) {
        Intent intent = new Intent(CuocHenListActivity.this, CuocHenFormActivity.class);
        intent.putExtra("do_an_id", doAnId);
        intent.putExtra("cuoc_hen_id", ch.getId());
        startActivity(intent);
    }

    // Hiển thị AlertDialog xác nhận xóa cuộc hẹn
    @Override
    public void onDelete(CuocHen ch) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa cuộc hẹn");
        builder.setMessage("Bạn có chắc chắn muốn xóa lịch hẹn: \"" + ch.getTieuDe() + "\" không?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            dbHelper.deleteCuocHen(ch.getId());
            loadData();
            Toast.makeText(CuocHenListActivity.this, "Đã xóa cuộc hẹn thành công!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
