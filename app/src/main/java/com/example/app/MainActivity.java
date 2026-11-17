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

// Màn hình chính quản lý danh sách đồ án
public class MainActivity extends AppCompatActivity implements DoAnAdapter.OnDoAnClickListener {

    private RecyclerView rvDoAn;
    private LinearLayout layoutEmpty;
    private FloatingActionButton fabAddDoAn;

    private DBHelper dbHelper;
    private ArrayList<DoAn> listDoAn;
    private DoAnAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo DBHelper
        dbHelper = new DBHelper(this);

        // Ánh xạ các view qua findViewById
        rvDoAn = findViewById(R.id.rvDoAn);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        fabAddDoAn = findViewById(R.id.fabAddDoAn);

        // Khởi tạo danh sách và Adapter kinh điển
        listDoAn = new ArrayList<>();
        adapter = new DoAnAdapter(this, listDoAn, this);

        // Cài đặt bố cục danh sách dạng cuộn dọc
        rvDoAn.setLayoutManager(new LinearLayoutManager(this));
        rvDoAn.setAdapter(adapter);

        // Bấm nút thêm mới đồ án
        fabAddDoAn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DoAnFormActivity.class);
            startActivity(intent);
        });
    }

    // Tải lại dữ liệu mỗi khi Activity quay trở lại foreground
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    // Đọc danh sách đồ án từ SQLite và cập nhật giao diện
    private void loadData() {
        listDoAn.clear();
        listDoAn.addAll(dbHelper.getAllDoAn());

        // Thông báo cho Adapter vẽ lại danh sách
        adapter.notifyDataSetChanged();

        // Ẩn/hiện màn hình trống tùy theo số lượng đồ án
        if (listDoAn.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvDoAn.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvDoAn.setVisibility(View.VISIBLE);
        }
    }

    // Xử lý khi bấm vào một đồ án -> Mở màn hình chi tiết
    @Override
    public void onItemClick(DoAn doAn) {
        Intent intent = new Intent(MainActivity.this, DoAnDetailActivity.class);
        intent.putExtra("do_an_id", doAn.getId());
        startActivity(intent);
    }

    // Xử lý khi bấm nút Sửa trên dòng đồ án -> Mở màn hình form
    @Override
    public void onEditClick(DoAn doAn) {
        Intent intent = new Intent(MainActivity.this, DoAnFormActivity.class);
        intent.putExtra("do_an_id", doAn.getId());
        startActivity(intent);
    }

    // Xử lý khi bấm nút Xóa hoặc nhấn giữ đồ án -> Hiện AlertDialog xác nhận
    @Override
    public void onDeleteClick(DoAn doAn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa đồ án");
        builder.setMessage("Bạn có chắc chắn muốn xóa đồ án: \"" + doAn.getTen() + "\" không?\n\nCác nhiệm vụ và cuộc hẹn liên quan sẽ tự động bị xóa.");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            // Xóa khỏi SQLite (CASCADE sẽ tự xóa nhiệm vụ và cuộc hẹn)
            dbHelper.deleteDoAn(doAn.getId());
            loadData();
            Toast.makeText(MainActivity.this, "Đã xóa đồ án thành công!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
