package com.example.app.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.app.R;
import com.example.app.databinding.ActivityDoAnDetailBinding;
import com.example.app.db.DoAnDao;
import com.example.app.model.DoAn;
import com.example.app.model.TienDoInfo;
import com.google.android.material.tabs.TabLayoutMediator;

public class DoAnDetailActivity extends AppCompatActivity {

    public static final String EXTRA_DO_AN_ID = "extra_do_an_id";
    private static final String KEY_SELECTED_TAB = "key_selected_tab";

    private ActivityDoAnDetailBinding binding;
    private DoAnDao doAnDao;
    private int doAnId = -1;
    private DoAn currentDoAn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDoAnDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainDetail, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        doAnDao = new DoAnDao(this);

        // Nhan id do an tu Intent
        doAnId = getIntent().getIntExtra(EXTRA_DO_AN_ID, -1);
        if (doAnId == -1) {
            Toast.makeText(this, R.string.error_project_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentDoAn = doAnDao.getById(doAnId);
        if (currentDoAn == null) {
            Toast.makeText(this, R.string.error_project_deleted, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar();
        setupHeaderInfo();
        setupViewPagerAndTabs(savedInstanceState);
        updateHeaderProgress();
    }

    private void setupToolbar() {
        binding.toolbarDetail.setTitle(currentDoAn.getTen());
        binding.toolbarDetail.setNavigationOnClickListener(v -> finish());
    }

    private void setupHeaderInfo() {
        binding.tvDetailTen.setText(currentDoAn.getTen());

        String gv = (currentDoAn.getGiangVien() != null && !currentDoAn.getGiangVien().trim().isEmpty())
                ? String.format(getString(R.string.supervisor_format), currentDoAn.getGiangVien().trim())
                : String.format(getString(R.string.supervisor_format), getString(R.string.supervisor_unassigned));
        binding.tvDetailGiangVien.setText(gv);

        String start = currentDoAn.getNgayBatDau();
        String end = currentDoAn.getHanNop();
        if (end != null && !end.trim().isEmpty()) {
            if (start != null && !start.trim().isEmpty()) {
                binding.tvDetailThoiGian.setText(String.format(getString(R.string.detail_date_range_format), start.trim(), end.trim()));
            } else {
                binding.tvDetailThoiGian.setText(String.format(getString(R.string.due_date_format), end.trim()));
            }
        } else {
            binding.tvDetailThoiGian.setText(getString(R.string.no_due_date));
        }
    }

    private void setupViewPagerAndTabs(Bundle savedInstanceState) {
        binding.viewPagerDetail.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) {
                    return NhiemVuFragment.newInstance(doAnId);
                } else {
                    return CuocHenFragment.newInstance(doAnId);
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        new TabLayoutMediator(binding.tabLayoutDetail, binding.viewPagerDetail, (tab, position) -> {
            if (position == 0) {
                tab.setText(R.string.tab_tasks);
            } else {
                tab.setText(R.string.tab_meetings);
            }
        }).attach();

        if (savedInstanceState != null) {
            int selectedTab = savedInstanceState.getInt(KEY_SELECTED_TAB, 0);
            binding.viewPagerDetail.setCurrentItem(selectedTab, false);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (binding != null && binding.tabLayoutDetail != null) {
            outState.putInt(KEY_SELECTED_TAB, binding.tabLayoutDetail.getSelectedTabPosition());
        }
    }

    /**
     * Cap nhat tien do header ("X/Y (Z%)") khi tick/bo tick hoac them/sua/xoa nhiem vu
     */
    public void updateHeaderProgress() {
        if (doAnDao == null || doAnId == -1) return;
        TienDoInfo info = doAnDao.getTienDo(doAnId);
        binding.progressDetail.setProgress(info.getPercent());
        binding.tvDetailTienDoValue.setText(info.getFormattedText());
    }
}
