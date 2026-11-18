package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

// Màn hình danh sách Nhiệm vụ (Khớp đúng khung mẫu slide 193)
public class NhiemVuListActivity extends AppCompatActivity implements NhiemVuAdapter.OnNhiemVuClickListener {

    // Khai báo các thuộc tính thành viên theo đúng mẫu slide 193
    private RecyclerView rvNhiemVu;
    private NhiemVuAdapter mNhiemVuAdapter;
    private List<NhiemVu> mNhiemVus;

    private Toolbar mToolbarNhiemVu;
    private LinearLayout mLayoutEmptyNhiemVu;
    private FloatingActionButton mFabAddNhiemVu;

    private DBHelper mDbHelper;
    private int mDoAnId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Thứ tự onCreate chuẩn bài giảng slide 193:
        // 1. setContentView
        setContentView(R.layout.activity_nhiem_vu_list);

        mDbHelper = new DBHelper(this);

        mDoAnId = getIntent().getIntExtra("do_an_id", -1);
        if (mDoAnId == -1) {
            Toast.makeText(this, "Không tìm thấy đồ án!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mToolbarNhiemVu = findViewById(R.id.toolbarNhiemVu);
        mToolbarNhiemVu.setNavigationOnClickListener(v -> finish());
        DoAn doAn = mDbHelper.getDoAnById(mDoAnId);
        if (doAn != null) {
            mToolbarNhiemVu.setTitle("Nhiệm vụ: " + doAn.getTen());
        }

        // 2. Ánh xạ RecyclerView
        rvNhiemVu = (RecyclerView) findViewById(R.id.rv_nhiem_vu);
        mLayoutEmptyNhiemVu = findViewById(R.id.layoutEmptyNhiemVu);
        mFabAddNhiemVu = findViewById(R.id.fabAddNhiemVu);

        // 3. Khởi tạo danh sách từ DBHelper
        mNhiemVus = new ArrayList<>();
        mNhiemVus.addAll(mDbHelper.getNhiemVuByDoAnId(mDoAnId));

        // 4. Khởi tạo Adapter
        mNhiemVuAdapter = new NhiemVuAdapter(this, mNhiemVus, this);

        // 5. Gắn Adapter vào RecyclerView
        rvNhiemVu.setAdapter(mNhiemVuAdapter);

        // 6. Cài đặt LayoutManager theo mẫu slide 193
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvNhiemVu.setLayoutManager(linearLayoutManager);

        // 7. Cài đặt ItemAnimator theo MainAnimationActivity (slide 223)
        rvNhiemVu.setItemAnimator(new DefaultItemAnimator());

        // Tắt nháy CheckBox khi notifyItemChanged (slide 219)
        if (rvNhiemVu.getItemAnimator() instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) rvNhiemVu.getItemAnimator()).setSupportsChangeAnimations(false);
        }

        // Thêm đường kẻ phân cách DividerItemDecoration
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvNhiemVu.addItemDecoration(divider);

        // Bấm nút thêm mới nhiệm vụ -> mở form
        mFabAddNhiemVu.setOnClickListener(v -> {
            Intent intent = new Intent(NhiemVuListActivity.this, NhiemVuFormActivity.class);
            intent.putExtra("do_an_id", mDoAnId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        mNhiemVus.clear();
        mNhiemVus.addAll(mDbHelper.getNhiemVuByDoAnId(mDoAnId));
        mNhiemVuAdapter.notifyDataSetChanged();
        checkEmptyState();
    }

    private void checkEmptyState() {
        if (mNhiemVuAdapter.getItemCount() == 0) {
            mLayoutEmptyNhiemVu.setVisibility(View.VISIBLE);
            rvNhiemVu.setVisibility(View.GONE);
        } else {
            mLayoutEmptyNhiemVu.setVisibility(View.GONE);
            rvNhiemVu.setVisibility(View.VISIBLE);
        }
    }

    // Tick CheckBox: cập nhật SQLite + cập nhật đối tượng + notifyItemChanged(position)
    @Override
    public void onToggleStatus(NhiemVu nv, boolean isCompleted, int position) {
        mDbHelper.updateNhiemVuStatus(nv.getId(), isCompleted ? 1 : 0);
        nv.setDaHoanThanh(isCompleted ? 1 : 0);
        mNhiemVuAdapter.notifyItemChanged(position);
        Toast.makeText(this, isCompleted ? "Đã hoàn thành: " + nv.getTen() : "Đã hủy hoàn thành", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEdit(NhiemVu nv, int position) {
        Intent intent = new Intent(NhiemVuListActivity.this, NhiemVuFormActivity.class);
        intent.putExtra("do_an_id", mDoAnId);
        intent.putExtra("nhiem_vu_id", nv.getId());
        startActivity(intent);
    }

    // Xóa nhiệm vụ bằng removeItem(position) (mẫu slide 221, MainAnimationActivity slide 223)
    @Override
    public void onDelete(NhiemVu nv, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa nhiệm vụ");
        builder.setMessage("Bạn có chắc chắn muốn xóa nhiệm vụ: \"" + nv.getTen() + "\" không?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            mDbHelper.deleteNhiemVu(nv.getId());
            mNhiemVuAdapter.removeItem(position);
            checkEmptyState();
            Toast.makeText(NhiemVuListActivity.this, "Đã xóa nhiệm vụ thành công!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
