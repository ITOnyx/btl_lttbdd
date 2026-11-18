package com.example.app;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Màn hình Dòng thời gian của đồ án (Mục D: Áp dụng Multiple View Type theo slide 209–210)
public class TimelineActivity extends AppCompatActivity {

    // Hằng số Intent nhận ID đồ án
    public static final String EXTRA_DO_AN_ID = "do_an_id";

    private Toolbar mToolbarTimeline;
    private RecyclerView mRvTimeline;
    private LinearLayout mLayoutEmptyTimeline;

    private DBHelper mDbHelper;
    private int mDoAnId = -1;
    private ArrayList<Object> mListObjects;
    private TimelineAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        mDbHelper = new DBHelper(this);

        // Đọc ID đồ án truyền từ màn hình chi tiết bằng hằng số
        mDoAnId = getIntent().getIntExtra(EXTRA_DO_AN_ID, -1);
        if (mDoAnId == -1) {
            Toast.makeText(this, "Không tìm thấy đồ án!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ các View
        mToolbarTimeline = findViewById(R.id.toolbar_timeline);
        mRvTimeline = findViewById(R.id.rv_timeline);
        mLayoutEmptyTimeline = findViewById(R.id.layout_empty_timeline);

        // Nút Back trên Toolbar
        mToolbarTimeline.setNavigationOnClickListener(v -> finish());

        // Đặt tiêu đề Toolbar theo tên đồ án
        DoAn doAn = mDbHelper.getDoAnById(mDoAnId);
        if (doAn != null) {
            mToolbarTimeline.setTitle("Timeline: " + doAn.getTen());
        }

        // Khởi tạo danh sách hỗn hợp và Adapter Multiple View Type
        mListObjects = new ArrayList<>();
        mAdapter = new TimelineAdapter(this, mListObjects);

        // Cài đặt RecyclerView dạng cuộn dọc
        mRvTimeline.setAdapter(mAdapter);
        mRvTimeline.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRvTimeline.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTimelineData();
    }

    // Mục D7: Nạp dữ liệu gộp cả Header, Nhiệm vụ và Lịch hẹn vào cùng một danh sách mListObjects (slide 199)
    private void loadTimelineData() {
        mListObjects.clear();

        // 1. Lấy danh sách nhiệm vụ từ SQLite
        ArrayList<NhiemVu> tasks = mDbHelper.getNhiemVuByDoAnId(mDoAnId);

        // 2. Lấy danh sách cuộc hẹn từ SQLite (sắp xếp tăng dần theo thời gian)
        ArrayList<CuocHen> meetings = mDbHelper.getCuocHenByDoAnId(mDoAnId);

        // Nạp nhóm Nhiệm vụ nếu có
        if (!tasks.isEmpty()) {
            mListObjects.add("Danh sách Nhiệm vụ (" + tasks.size() + ")"); // String -> Header
            mListObjects.addAll(tasks);                                   // NhiemVu -> Task
        }

        // Nạp nhóm Lịch hẹn nếu có
        if (!meetings.isEmpty()) {
            mListObjects.add("Lịch hẹn trao đổi với GVHD (" + meetings.size() + ")"); // String -> Header
            mListObjects.addAll(meetings);                                           // CuocHen -> Meeting
        }

        // Cập nhật giao diện
        mAdapter.notifyDataSetChanged();

        if (mListObjects.isEmpty()) {
            mLayoutEmptyTimeline.setVisibility(View.VISIBLE);
            mRvTimeline.setVisibility(View.GONE);
        } else {
            mLayoutEmptyTimeline.setVisibility(View.GONE);
            mRvTimeline.setVisibility(View.VISIBLE);
        }
    }
}
