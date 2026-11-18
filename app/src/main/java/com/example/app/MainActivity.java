package com.example.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

// Màn hình chính quản lý danh sách đồ án (Áp dụng 5 bước RecyclerView, Animation và LayoutManager)
public class MainActivity extends AppCompatActivity implements DoAnAdapter.OnDoAnClickListener {

    // Khai báo các thuộc tính thành viên với tiền tố m như bài giảng
    private Toolbar mToolbar;
    private RecyclerView mRvDoAn;
    private LinearLayout mLayoutEmpty;
    private FloatingActionButton mFabAddDoAn;

    private DBHelper mDbHelper;
    private ArrayList<DoAn> mListDoAn;
    private DoAnAdapter mAdapter;

    // Mục C1: Trạng thái hiển thị danh sách (false: LinearLayoutManager dọc, true: GridLayoutManager 2 cột)
    private boolean mIsGridMode = false;
    private SharedPreferences mPrefs;
    private MenuItem mMenuToggleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo DBHelper kết nối CSDL SQLite
        mDbHelper = new DBHelper(this);

        // Ánh xạ các view qua findViewById
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mRvDoAn = findViewById(R.id.rv_do_an);
        mLayoutEmpty = findViewById(R.id.layoutEmpty);
        mFabAddDoAn = findViewById(R.id.fabAddDoAn);

        // Đọc cấu hình hiển thị đã lưu từ SharedPreferences (mục C1)
        mPrefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        mIsGridMode = mPrefs.getBoolean("is_grid_mode", false);

        // Bước 5: Khởi tạo danh sách và Adapter kinh điển
        mListDoAn = new ArrayList<>();
        mAdapter = new DoAnAdapter(this, mListDoAn, this);
        mRvDoAn.setAdapter(mAdapter);

        // Cài đặt LayoutManager ban đầu dựa trên trạng thái đã lưu
        applyLayoutManager();

        // Mục B2: Cài đặt ItemAnimator theo DefaultItemAnimator (slide 211–223)
        // Cơ chế DefaultItemAnimator:
        // - Thêm (insert): mờ dần sang rõ (alpha 0 -> 1)
        // - Xóa (remove): rõ dần sang mờ rồi biến mất (alpha 1 -> 0)
        // - Thay đổi (change): mờ dần (1 -> 0) rồi hiện lại (0 -> 1)
        mRvDoAn.setItemAnimator(new DefaultItemAnimator());

        // Bấm nút thêm mới đồ án -> chuyển màn hình nhập form
        mFabAddDoAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DoAnFormActivity.class);
                startActivity(intent);
            }
        });
    }

    // Mục C1: Áp dụng LayoutManager (LinearLayoutManager dọc hoặc GridLayoutManager 2 cột theo slide 185, 192)
    private void applyLayoutManager() {
        if (mIsGridMode) {
            // Chế độ lưới: GridLayoutManager 2 cột (slide 185, 192)
            mRvDoAn.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            // Chế độ danh sách: LinearLayoutManager cuộn dọc (slide 185, 192)
            mRvDoAn.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }
    }

    // Mục C1: Khởi tạo menu trên toolbar để chuyển đổi chế độ xem
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenuToggleLayout = menu.findItem(R.id.action_toggle_layout);
        updateMenuIcon();
        return true;
    }

    // Mục C1: Bắt sự kiện chọn item trên menu toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_layout) {
            // Đảo chế độ hiển thị
            mIsGridMode = !mIsGridMode;

            // Lưu lựa chọn vào SharedPreferences
            mPrefs.edit().putBoolean("is_grid_mode", mIsGridMode).apply();

            // Chuyển LayoutManager bằng rv.setLayoutManager(...) (slide 185, 192)
            applyLayoutManager();
            updateMenuIcon();

            // Thông báo Adapter vẽ lại theo kích thước mới
            mAdapter.notifyDataSetChanged();

            Toast.makeText(this, mIsGridMode ? "Chuyển sang chế độ lưới 2 cột" : "Chuyển sang chế độ danh sách", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Cập nhật icon trên toolbar tương ứng với chế độ đang bật
    private void updateMenuIcon() {
        if (mMenuToggleLayout != null) {
            if (mIsGridMode) {
                mMenuToggleLayout.setIcon(R.drawable.ic_list);
                mMenuToggleLayout.setTitle("Chế độ danh sách");
            } else {
                mMenuToggleLayout.setIcon(R.drawable.ic_grid);
                mMenuToggleLayout.setTitle("Chế độ lưới");
            }
        }
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

        // Mục B4: Nạp lại toàn bộ danh sách từ CSDL - dùng notifyDataSetChanged() không có animation (slide 212)
        mAdapter.notifyDataSetChanged();

        // Cập nhật trạng thái empty state
        checkEmptyState();
    }

    // Mục B6: Cập nhật trạng thái hiển thị giao diện trống (empty state) tại chỗ
    private void checkEmptyState() {
        if (mAdapter.getItemCount() == 0) {
            mLayoutEmpty.setVisibility(View.VISIBLE);
            mRvDoAn.setVisibility(View.GONE);
        } else {
            mLayoutEmpty.setVisibility(View.GONE);
            mRvDoAn.setVisibility(View.VISIBLE);
        }
    }

    // Bước 6: Xử lý sự kiện click item -> Mở màn hình chi tiết đồ án bằng Intent
    @Override
    public void onItemClick(DoAn doAn, int position) {
        Intent intent = new Intent(MainActivity.this, DoAnDetailActivity.class);
        intent.putExtra("do_an_id", doAn.getId());
        startActivity(intent);
    }

    // Xử lý khi bấm nút Sửa trên dòng đồ án -> Mở màn hình form sửa
    @Override
    public void onEditClick(DoAn doAn, int position) {
        Intent intent = new Intent(MainActivity.this, DoAnFormActivity.class);
        intent.putExtra("do_an_id", doAn.getId());
        startActivity(intent);
    }

    // Mục B3: Xử lý khi bấm nút Xóa hoặc nhấn giữ đồ án -> Hiện AlertDialog xác nhận
    @Override
    public void onDeleteClick(DoAn doAn, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa đồ án");
        builder.setMessage("Bạn có chắc chắn muốn xóa đồ án: \"" + doAn.getTen() + "\" không?\n\nCác nhiệm vụ và cuộc hẹn liên quan sẽ tự động bị xóa.");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            // 1. Xóa khỏi SQLite (CASCADE tự xóa nhiệm vụ và cuộc hẹn liên quan)
            mDbHelper.deleteDoAn(doAn.getId());

            // 2. Xóa khỏi Adapter tại vị trí position với hoạt ảnh removeItem (slide 216)
            mAdapter.removeItem(position);

            // 3. Cập nhật Empty state tại chỗ sau khi xóa
            checkEmptyState();

            Toast.makeText(MainActivity.this, "Đã xóa đồ án thành công!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
