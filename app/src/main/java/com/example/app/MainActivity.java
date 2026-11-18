package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

// Màn hình chính quản lý danh sách đồ án (Áp dụng 5 bước RecyclerView theo slide 184–194)
public class MainActivity extends AppCompatActivity implements DoAnAdapter.OnDoAnClickListener {

    // Khai báo các thuộc tính thành viên với tiền tố m như bài giảng
    private RecyclerView mRvDoAn;
    private LinearLayout mLayoutEmpty;
    private FloatingActionButton mFabAddDoAn;

    private DBHelper mDbHelper;
    private ArrayList<DoAn> mListDoAn;
    private DoAnAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo DBHelper kết nối CSDL SQLite
        mDbHelper = new DBHelper(this);

        // Ánh xạ các view qua findViewById (Bước 2: rv_do_an)
        mRvDoAn = findViewById(R.id.rv_do_an);
        mLayoutEmpty = findViewById(R.id.layoutEmpty);
        mFabAddDoAn = findViewById(R.id.fabAddDoAn);

        // Bước 5: Khởi tạo danh sách và Adapter kinh điển
        mListDoAn = new ArrayList<>();
        mAdapter = new DoAnAdapter(this, mListDoAn, this);

        // Bước 5: Gắn Adapter và LayoutManager dạng cuộn dọc theo đúng bài giảng (slide 185, 192)
        mRvDoAn.setAdapter(mAdapter);
        mRvDoAn.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Bấm nút thêm mới đồ án -> chuyển màn hình nhập form
        mFabAddDoAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DoAnFormActivity.class);
                startActivity(intent);
            }
        });
    }

    // Bước 7: onResume nạp lại dữ liệu từ SQLite đảm bảo dữ liệu luôn mới nhất
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    // Bước 7: Đọc danh sách đồ án từ DBHelper (rawQuery + Cursor) và hiển thị
    private void loadData() {
        mListDoAn.clear();
        mListDoAn.addAll(mDbHelper.getAllDoAn());

        // Thông báo cho Adapter vẽ lại danh sách
        mAdapter.notifyDataSetChanged();

        // Cập nhật trạng thái hiển thị giao diện trống nếu danh sách rỗng
        if (mListDoAn.isEmpty()) {
            mLayoutEmpty.setVisibility(View.VISIBLE);
            mRvDoAn.setVisibility(View.GONE);
        } else {
            mLayoutEmpty.setVisibility(View.GONE);
            mRvDoAn.setVisibility(View.VISIBLE);
        }
    }

    // Bước 6: Xử lý sự kiện click item -> Mở màn hình chi tiết đồ án bằng Intent
    @Override
    public void onItemClick(DoAn doAn) {
        Intent intent = new Intent(MainActivity.this, DoAnDetailActivity.class);
        intent.putExtra("do_an_id", doAn.getId());
        startActivity(intent);
    }

    // Xử lý khi bấm nút Sửa trên dòng đồ án -> Mở màn hình form sửa
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
            // Xóa khỏi SQLite (CASCADE tự xóa nhiệm vụ và cuộc hẹn liên quan)
            mDbHelper.deleteDoAn(doAn.getId());
            loadData();
            Toast.makeText(MainActivity.this, "Đã xóa đồ án thành công!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
