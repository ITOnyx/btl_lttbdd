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
import java.util.List;

// Màn hình danh sách Đồ án (Khớp đúng khung mẫu slide 193)
public class MainActivity extends AppCompatActivity implements DoAnAdapter.OnDoAnClickListener {

    // Khai báo các thuộc tính thành viên theo đúng mẫu slide 193
    private RecyclerView rvDoAn;
    private DoAnAdapter mDoAnAdapter;
    private List<DoAn> mDoAns;

    private Toolbar mToolbar;
    private LinearLayout mLayoutEmpty;
    private FloatingActionButton mFabAddDoAn;
    private DBHelper mDbHelper;

    // Chuyển đổi chế độ xem Danh sách / Lưới
    private boolean mIsGridMode = false;
    private SharedPreferences mPrefs;
    private MenuItem mMenuToggleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Thứ tự onCreate chuẩn bài giảng slide 193:
        // 1. setContentView
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // 2. Ánh xạ RecyclerView
        rvDoAn = (RecyclerView) findViewById(R.id.rv_do_an);
        mLayoutEmpty = findViewById(R.id.layoutEmpty);
        mFabAddDoAn = findViewById(R.id.fabAddDoAn);

        // 3. Khởi tạo DBHelper và nạp dữ liệu danh sách
        mDbHelper = new DBHelper(this);
        mDoAns = new ArrayList<>();
        mDoAns.addAll(mDbHelper.getAllDoAn());

        // 4. Khởi tạo Adapter
        mDoAnAdapter = new DoAnAdapter(this, mDoAns, this);

        // 5. Gắn Adapter vào RecyclerView
        rvDoAn.setAdapter(mDoAnAdapter);

        // 6. Cài đặt LayoutManager theo mẫu slide 193
        mPrefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        mIsGridMode = mPrefs.getBoolean("is_grid_mode", false);
        if (mIsGridMode) {
            rvDoAn.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            rvDoAn.setLayoutManager(linearLayoutManager);
        }

        // 7. Cài đặt ItemAnimator theo MainAnimationActivity (slide 223)
        rvDoAn.setItemAnimator(new DefaultItemAnimator());

        // Bấm nút thêm mới đồ án -> mở form
        mFabAddDoAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DoAnFormActivity.class);
                startActivity(intent);
            }
        });
    }

    // Menu chuyển đổi chế độ xem danh sách / lưới
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenuToggleLayout = menu.findItem(R.id.action_toggle_layout);
        updateMenuIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_layout) {
            mIsGridMode = !mIsGridMode;
            mPrefs.edit().putBoolean("is_grid_mode", mIsGridMode).apply();

            if (mIsGridMode) {
                rvDoAn.setLayoutManager(new GridLayoutManager(this, 2));
            } else {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                rvDoAn.setLayoutManager(linearLayoutManager);
            }
            updateMenuIcon();
            mDoAnAdapter.notifyDataSetChanged();
            Toast.makeText(this, mIsGridMode ? "Chế độ lưới 2 cột" : "Chế độ danh sách", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    // Nạp lại dữ liệu từ SQLite
    private void loadData() {
        mDoAns.clear();
        mDoAns.addAll(mDbHelper.getAllDoAn());

        // Dùng notifyDataSetChanged() khi tải lại toàn bộ từ CSDL - không có animation (slide 212)
        mDoAnAdapter.notifyDataSetChanged();

        checkEmptyState();
    }

    private void checkEmptyState() {
        if (mDoAnAdapter.getItemCount() == 0) {
            mLayoutEmpty.setVisibility(View.VISIBLE);
            rvDoAn.setVisibility(View.GONE);
        } else {
            mLayoutEmpty.setVisibility(View.GONE);
            rvDoAn.setVisibility(View.VISIBLE);
        }
    }

    // Mở chi tiết đồ án dùng hằng số EXTRA_DO_AN_ID (Yêu cầu 6)
    @Override
    public void onItemClick(DoAn doAn, int position) {
        Intent intent = new Intent(MainActivity.this, DoAnDetailActivity.class);
        intent.putExtra(DoAnDetailActivity.EXTRA_DO_AN_ID, doAn.getId());
        startActivity(intent);
    }

    // Mở form sửa
    @Override
    public void onEditClick(DoAn doAn, int position) {
        Intent intent = new Intent(MainActivity.this, DoAnFormActivity.class);
        intent.putExtra(DoAnDetailActivity.EXTRA_DO_AN_ID, doAn.getId());
        startActivity(intent);
    }

    // Xóa đồ án bằng removeItem(position) (mẫu slide 221, MainAnimationActivity slide 223)
    @Override
    public void onDeleteClick(DoAn doAn, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa đồ án");
        builder.setMessage("Bạn có chắc chắn muốn xóa đồ án: \"" + doAn.getTen() + "\" không?\n\nCác nhiệm vụ và cuộc hẹn liên quan sẽ tự động bị xóa.");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            mDbHelper.deleteDoAn(doAn.getId());
            mDoAnAdapter.removeItem(position);
            checkEmptyState();
            Toast.makeText(MainActivity.this, "Đã xóa đồ án thành công!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
