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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

// Màn hình hiển thị và quản lý danh sách nhiệm vụ của đồ án (Áp dụng 5 bước RecyclerView và Animation)
public class NhiemVuListActivity extends AppCompatActivity implements NhiemVuAdapter.OnNhiemVuClickListener {

    // Khai báo các thuộc tính thành viên với tiền tố m như bài giảng
    private Toolbar mToolbarNhiemVu;
    private RecyclerView mRvNhiemVu;
    private LinearLayout mLayoutEmptyNhiemVu;
    private FloatingActionButton mFabAddNhiemVu;

    private DBHelper mDbHelper;
    private int mDoAnId = -1;
    private ArrayList<NhiemVu> mListNhiemVu;
    private NhiemVuAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhiem_vu_list);

        mDbHelper = new DBHelper(this);

        // Đọc ID đồ án từ Intent
        mDoAnId = getIntent().getIntExtra("do_an_id", -1);
        if (mDoAnId == -1) {
            Toast.makeText(this, "Không tìm thấy đồ án!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ các View qua findViewById (Bước 2: rv_nhiem_vu)
        mToolbarNhiemVu = findViewById(R.id.toolbarNhiemVu);
        mRvNhiemVu = findViewById(R.id.rv_nhiem_vu);
        mLayoutEmptyNhiemVu = findViewById(R.id.layoutEmptyNhiemVu);
        mFabAddNhiemVu = findViewById(R.id.fabAddNhiemVu);

        // Nút Back trên Toolbar
        mToolbarNhiemVu.setNavigationOnClickListener(v -> finish());

        // Đặt tiêu đề Toolbar theo tên đồ án
        DoAn doAn = mDbHelper.getDoAnById(mDoAnId);
        if (doAn != null) {
            mToolbarNhiemVu.setTitle("Nhiệm vụ: " + doAn.getTen());
        }

        // Bước 5: Khởi tạo danh sách và RecyclerView Adapter
        mListNhiemVu = new ArrayList<>();
        mAdapter = new NhiemVuAdapter(this, mListNhiemVu, this);

        // Bước 5: Gắn Adapter và LayoutManager dạng cuộn dọc (slide 185, 192)
        mRvNhiemVu.setAdapter(mAdapter);
        mRvNhiemVu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Mục B2: Cài đặt DefaultItemAnimator (thêm: alpha 0->1, xóa: 1->0, đổi: 1->0 rồi 0->1)
        mRvNhiemVu.setItemAnimator(new DefaultItemAnimator());

        // Mục B5: Tắt hiệu ứng nháy khi notifyItemChanged bằng setSupportsChangeAnimations(false)
        // theo đúng hướng dẫn bài giảng (slide 219)
        if (mRvNhiemVu.getItemAnimator() instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) mRvNhiemVu.getItemAnimator()).setSupportsChangeAnimations(false);
        }

        // Bấm nút thêm mới nhiệm vụ
        mFabAddNhiemVu.setOnClickListener(v -> {
            Intent intent = new Intent(NhiemVuListActivity.this, NhiemVuFormActivity.class);
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

    // Bước 7: Đọc danh sách nhiệm vụ từ DBHelper (rawQuery + Cursor)
    private void loadData() {
        mListNhiemVu.clear();
        mListNhiemVu.addAll(mDbHelper.getNhiemVuByDoAnId(mDoAnId));

        // Mục B4: Nạp lại toàn bộ danh sách từ CSDL - dùng notifyDataSetChanged() không có animation (slide 212)
        mAdapter.notifyDataSetChanged();

        checkEmptyState();
    }

    // Mục B6: Cập nhật giao diện trống tại chỗ
    private void checkEmptyState() {
        if (mAdapter.getItemCount() == 0) {
            mLayoutEmptyNhiemVu.setVisibility(View.VISIBLE);
            mRvNhiemVu.setVisibility(View.GONE);
        } else {
            mLayoutEmptyNhiemVu.setVisibility(View.GONE);
            mRvNhiemVu.setVisibility(View.VISIBLE);
        }
    }

    // Mục B5: Cập nhật trạng thái hoàn thành ngay khi tick CheckBox tại vị trí position
    // Cập nhật DB + list, dùng notifyItemChanged; không bị nháy do đã tắt ChangeAnimations
    @Override
    public void onToggleStatus(NhiemVu nv, boolean isCompleted, int position) {
        // 1. Cập nhật SQLite
        mDbHelper.updateNhiemVuStatus(nv.getId(), isCompleted ? 1 : 0);

        // 2. Cập nhật đối tượng trong list
        nv.setDaHoanThanh(isCompleted ? 1 : 0);

        // 3. Thông báo ItemChanged tại position để cập nhật lại dòng mà không load lại cả danh sách (slide 219)
        mAdapter.notifyItemChanged(position);

        Toast.makeText(this, isCompleted ? "Đã hoàn thành: " + nv.getTen() : "Đã hủy hoàn thành", Toast.LENGTH_SHORT).show();
    }

    // Chuyển sang màn hình sửa nhiệm vụ
    @Override
    public void onEdit(NhiemVu nv, int position) {
        Intent intent = new Intent(NhiemVuListActivity.this, NhiemVuFormActivity.class);
        intent.putExtra("do_an_id", mDoAnId);
        intent.putExtra("nhiem_vu_id", nv.getId());
        startActivity(intent);
    }

    // Mục B3: Hiển thị hộp thoại AlertDialog xác nhận xóa nhiệm vụ
    @Override
    public void onDelete(NhiemVu nv, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa nhiệm vụ");
        builder.setMessage("Bạn có chắc chắn muốn xóa nhiệm vụ: \"" + nv.getTen() + "\" không?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            // 1. Xóa khỏi CSDL SQLite
            mDbHelper.deleteNhiemVu(nv.getId());

            // 2. Xóa khỏi Adapter với hoạt ảnh removeItem (slide 216)
            mAdapter.removeItem(position);

            // 3. Cập nhật trạng thái empty state tại chỗ
            checkEmptyState();

            Toast.makeText(NhiemVuListActivity.this, "Đã xóa nhiệm vụ thành công!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
