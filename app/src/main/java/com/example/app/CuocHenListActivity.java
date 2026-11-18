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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

// Màn hình danh sách Cuộc hẹn (Khớp đúng khung mẫu slide 193)
public class CuocHenListActivity extends AppCompatActivity implements CuocHenAdapter.OnCuocHenClickListener {

    // Hằng số Intent nhận ID đồ án
    public static final String EXTRA_DO_AN_ID = "do_an_id";

    // Khai báo các thuộc tính thành viên theo đúng mẫu slide 193
    private RecyclerView rvCuocHen;
    private CuocHenAdapter mCuocHenAdapter;
    private List<CuocHen> mCuocHens;

    private Toolbar mToolbarCuocHen;
    private LinearLayout mLayoutEmptyCuocHen;
    private FloatingActionButton mFabAddCuocHen;

    private DBHelper mDbHelper;
    private int mDoAnId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Thứ tự onCreate chuẩn bài giảng slide 193:
        // 1. setContentView
        setContentView(R.layout.activity_cuoc_hen_list);

        mDbHelper = new DBHelper(this);

        mDoAnId = getIntent().getIntExtra(EXTRA_DO_AN_ID, -1);
        if (mDoAnId == -1) {
            Toast.makeText(this, "Không tìm thấy đồ án!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mToolbarCuocHen = findViewById(R.id.toolbarCuocHen);
        mToolbarCuocHen.setNavigationOnClickListener(v -> finish());
        DoAn doAn = mDbHelper.getDoAnById(mDoAnId);
        if (doAn != null) {
            mToolbarCuocHen.setTitle("Lịch hẹn: " + doAn.getTen());
        }

        // 2. Ánh xạ RecyclerView
        rvCuocHen = (RecyclerView) findViewById(R.id.rv_cuoc_hen);
        mLayoutEmptyCuocHen = findViewById(R.id.layoutEmptyCuocHen);
        mFabAddCuocHen = findViewById(R.id.fabAddCuocHen);

        // 3. Khởi tạo danh sách từ DBHelper
        mCuocHens = new ArrayList<>();
        mCuocHens.addAll(mDbHelper.getCuocHenByDoAnId(mDoAnId));

        // 4. Khởi tạo Adapter
        mCuocHenAdapter = new CuocHenAdapter(this, mCuocHens, this);

        // 5. Gắn Adapter vào RecyclerView
        rvCuocHen.setAdapter(mCuocHenAdapter);

        // 6. Cài đặt LayoutManager theo mẫu slide 193
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvCuocHen.setLayoutManager(linearLayoutManager);

        // 7. Cài đặt ItemAnimator theo MainAnimationActivity (slide 223)
        rvCuocHen.setItemAnimator(new DefaultItemAnimator());

        // Bấm nút thêm cuộc hẹn mới -> mở form bằng hằng số
        mFabAddCuocHen.setOnClickListener(v -> {
            Intent intent = new Intent(CuocHenListActivity.this, CuocHenFormActivity.class);
            intent.putExtra(CuocHenFormActivity.EXTRA_DO_AN_ID, mDoAnId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        mCuocHens.clear();
        mCuocHens.addAll(mDbHelper.getCuocHenByDoAnId(mDoAnId));
        mCuocHenAdapter.notifyDataSetChanged();
        checkEmptyState();
    }

    private void checkEmptyState() {
        if (mCuocHenAdapter.getItemCount() == 0) {
            mLayoutEmptyCuocHen.setVisibility(View.VISIBLE);
            rvCuocHen.setVisibility(View.GONE);
        } else {
            mLayoutEmptyCuocHen.setVisibility(View.GONE);
            rvCuocHen.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEdit(CuocHen ch, int position) {
        Intent intent = new Intent(CuocHenListActivity.this, CuocHenFormActivity.class);
        intent.putExtra(CuocHenFormActivity.EXTRA_DO_AN_ID, mDoAnId);
        intent.putExtra(CuocHenFormActivity.EXTRA_CUOC_HEN_ID, ch.getId());
        startActivity(intent);
    }

    // Xóa cuộc hẹn bằng removeItem(position) (mẫu slide 221, MainAnimationActivity slide 223)
    @Override
    public void onDelete(CuocHen ch, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa cuộc hẹn");
        builder.setMessage("Bạn có chắc chắn muốn xóa lịch hẹn: \"" + ch.getTieuDe() + "\" không?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            mDbHelper.deleteCuocHen(ch.getId());
            mCuocHenAdapter.removeItem(position);
            checkEmptyState();
            Toast.makeText(CuocHenListActivity.this, "Đã xóa cuộc hẹn thành công!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
