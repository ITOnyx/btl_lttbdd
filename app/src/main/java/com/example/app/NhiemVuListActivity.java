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

// Màn hình hiển thị và quản lý danh sách nhiệm vụ của một đồ án
public class NhiemVuListActivity extends AppCompatActivity implements NhiemVuAdapter.OnNhiemVuClickListener {

    private Toolbar toolbarNhiemVu;
    private RecyclerView rvNhiemVu;
    private LinearLayout layoutEmptyNhiemVu;
    private FloatingActionButton fabAddNhiemVu;

    private DBHelper dbHelper;
    private int doAnId = -1;
    private ArrayList<NhiemVu> listNhiemVu;
    private NhiemVuAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhiem_vu_list);

        dbHelper = new DBHelper(this);

        // Đọc ID đồ án từ Intent
        doAnId = getIntent().getIntExtra("do_an_id", -1);
        if (doAnId == -1) {
            Toast.makeText(this, "Không tìm thấy đồ án!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ các View
        toolbarNhiemVu = findViewById(R.id.toolbarNhiemVu);
        rvNhiemVu = findViewById(R.id.rvNhiemVu);
        layoutEmptyNhiemVu = findViewById(R.id.layoutEmptyNhiemVu);
        fabAddNhiemVu = findViewById(R.id.fabAddNhiemVu);

        // Nút Back trên Toolbar
        toolbarNhiemVu.setNavigationOnClickListener(v -> finish());

        // Đặt tiêu đề Toolbar theo tên đồ án
        DoAn doAn = dbHelper.getDoAnById(doAnId);
        if (doAn != null) {
            toolbarNhiemVu.setTitle("Nhiệm vụ: " + doAn.getTen());
        }

        // Khởi tạo danh sách và RecyclerView Adapter
        listNhiemVu = new ArrayList<>();
        adapter = new NhiemVuAdapter(this, listNhiemVu, this);
        rvNhiemVu.setLayoutManager(new LinearLayoutManager(this));
        rvNhiemVu.setAdapter(adapter);

        // Bấm nút thêm mới nhiệm vụ
        fabAddNhiemVu.setOnClickListener(v -> {
            Intent intent = new Intent(NhiemVuListActivity.this, NhiemVuFormActivity.class);
            intent.putExtra("do_an_id", doAnId);
            startActivity(intent);
        });
    }

    // Tải lại dữ liệu khi quay về màn hình
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    // Đọc danh sách nhiệm vụ của đồ án từ SQLite
    private void loadData() {
        listNhiemVu.clear();
        listNhiemVu.addAll(dbHelper.getNhiemVuByDoAnId(doAnId));
        adapter.notifyDataSetChanged();

        if (listNhiemVu.isEmpty()) {
            layoutEmptyNhiemVu.setVisibility(View.VISIBLE);
            rvNhiemVu.setVisibility(View.GONE);
        } else {
            layoutEmptyNhiemVu.setVisibility(View.GONE);
            rvNhiemVu.setVisibility(View.VISIBLE);
        }
    }

    // Cập nhật trạng thái hoàn thành ngay khi người dùng tick/bỏ tick CheckBox
    @Override
    public void onToggleStatus(NhiemVu nv, boolean isCompleted) {
        // Cập nhật giá trị vào SQLite (1: đã xong, 0: chưa xong)
        dbHelper.updateNhiemVuStatus(nv.getId(), isCompleted ? 1 : 0);
        loadData();
        Toast.makeText(this, isCompleted ? "Đã hoàn thành: " + nv.getTen() : "Đã hủy hoàn thành", Toast.LENGTH_SHORT).show();
    }

    // Chuyển sang màn hình sửa nhiệm vụ
    @Override
    public void onEdit(NhiemVu nv) {
        Intent intent = new Intent(NhiemVuListActivity.this, NhiemVuFormActivity.class);
        intent.putExtra("do_an_id", doAnId);
        intent.putExtra("nhiem_vu_id", nv.getId());
        startActivity(intent);
    }

    // Hiển thị hộp thoại AlertDialog xác nhận xóa nhiệm vụ
    @Override
    public void onDelete(NhiemVu nv) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa nhiệm vụ");
        builder.setMessage("Bạn có chắc chắn muốn xóa nhiệm vụ: \"" + nv.getTen() + "\" không?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            dbHelper.deleteNhiemVu(nv.getId());
            loadData();
            Toast.makeText(NhiemVuListActivity.this, "Đã xóa nhiệm vụ thành công!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
