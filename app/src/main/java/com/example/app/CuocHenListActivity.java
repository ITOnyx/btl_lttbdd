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

// Màn hình hiển thị và quản lý danh sách cuộc hẹn của đồ án (Áp dụng 5 bước RecyclerView)
public class CuocHenListActivity extends AppCompatActivity implements CuocHenAdapter.OnCuocHenClickListener {

    // Khai báo các thuộc tính thành viên với tiền tố m như bài giảng
    private Toolbar mToolbarCuocHen;
    private RecyclerView mRvCuocHen;
    private LinearLayout mLayoutEmptyCuocHen;
    private FloatingActionButton mFabAddCuocHen;

    private DBHelper mDbHelper;
    private int mDoAnId = -1;
    private ArrayList<CuocHen> mListCuocHen;
    private CuocHenAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuoc_hen_list);

        mDbHelper = new DBHelper(this);

        // Đọc ID đồ án từ Intent
        mDoAnId = getIntent().getIntExtra("do_an_id", -1);
        if (mDoAnId == -1) {
            Toast.makeText(this, "Không tìm thấy đồ án!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ các View qua findViewById (Bước 2: rv_cuoc_hen)
        mToolbarCuocHen = findViewById(R.id.toolbarCuocHen);
        mRvCuocHen = findViewById(R.id.rv_cuoc_hen);
        mLayoutEmptyCuocHen = findViewById(R.id.layoutEmptyCuocHen);
        mFabAddCuocHen = findViewById(R.id.fabAddCuocHen);

        // Nút Back trên Toolbar
        mToolbarCuocHen.setNavigationOnClickListener(v -> finish());

        // Đặt tiêu đề Toolbar theo tên đồ án
        DoAn doAn = mDbHelper.getDoAnById(mDoAnId);
        if (doAn != null) {
            mToolbarCuocHen.setTitle("Lịch hẹn: " + doAn.getTen());
        }

        // Bước 5: Khởi tạo danh sách và RecyclerView Adapter
        mListCuocHen = new ArrayList<>();
        mAdapter = new CuocHenAdapter(this, mListCuocHen, this);

        // Bước 5: Gắn Adapter và LayoutManager dạng cuộn dọc (slide 185, 192)
        mRvCuocHen.setAdapter(mAdapter);
        mRvCuocHen.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Bấm nút thêm mới cuộc hẹn
        mFabAddCuocHen.setOnClickListener(v -> {
            Intent intent = new Intent(CuocHenListActivity.this, CuocHenFormActivity.class);
            intent.putExtra("do_an_id", mDoAnId);
            startActivity(intent);
        });
    }

    // Bước 7: onResume nạp lại dữ liệu từ SQLite
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    // Bước 7: Đọc danh sách cuộc hẹn từ DBHelper (rawQuery + Cursor)
    private void loadData() {
        mListCuocHen.clear();
        mListCuocHen.addAll(mDbHelper.getCuocHenByDoAnId(mDoAnId));
        mAdapter.notifyDataSetChanged();

        if (mListCuocHen.isEmpty()) {
            mLayoutEmptyCuocHen.setVisibility(View.VISIBLE);
            mRvCuocHen.setVisibility(View.GONE);
        } else {
            mLayoutEmptyCuocHen.setVisibility(View.GONE);
            mRvCuocHen.setVisibility(View.VISIBLE);
        }
    }

    // Chuyển sang màn hình sửa cuộc hẹn
    @Override
    public void onEdit(CuocHen ch) {
        Intent intent = new Intent(CuocHenListActivity.this, CuocHenFormActivity.class);
        intent.putExtra("do_an_id", mDoAnId);
        intent.putExtra("cuoc_hen_id", ch.getId());
        startActivity(intent);
    }

    // Hiển thị hộp thoại AlertDialog xác nhận xóa cuộc hẹn
    @Override
    public void onDelete(CuocHen ch) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa cuộc hẹn");
        builder.setMessage("Bạn có chắc chắn muốn xóa lịch hẹn: \"" + ch.getTieuDe() + "\" không?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            mDbHelper.deleteCuocHen(ch.getId());
            loadData();
            Toast.makeText(CuocHenListActivity.this, "Đã xóa cuộc hẹn thành công!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
