package com.example.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app.adapter.DoAnAdapter;
import com.example.app.databinding.ActivityMainBinding;
import com.example.app.databinding.DialogAddEditDoAnBinding;
import com.example.app.db.DoAnDao;
import com.example.app.model.DoAn;
import com.example.app.model.DoAnWithTienDo;
import com.example.app.util.DateUtils;
import com.example.app.util.SampleDataLoader;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity implements DoAnAdapter.OnDoAnActionListener {

    private ActivityMainBinding binding;
    private DoAnDao doAnDao;
    private DoAnAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        doAnDao = new DoAnDao(this);

        getSupportFragmentManager().setFragmentResultListener(
                com.example.app.ui.ConfirmDeleteDialogFragment.REQUEST_KEY_DELETE,
                this,
                (requestKey, bundle) -> {
                    boolean confirmed = bundle.getBoolean(com.example.app.ui.ConfirmDeleteDialogFragment.RESULT_KEY_CONFIRMED, false);
                    if (confirmed) {
                        long id = bundle.getLong(com.example.app.ui.ConfirmDeleteDialogFragment.RESULT_KEY_ITEM_ID);
                        int rows = doAnDao.delete((int) id);
                        if (rows > 0) {
                            Toast.makeText(this, R.string.action_success, Toast.LENGTH_SHORT).show();
                            loadData();
                        } else {
                            Toast.makeText(this, R.string.action_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        initViews();
    }

    private void initViews() {
        setSupportActionBar(binding.toolbar);
        adapter = new DoAnAdapter(this);
        binding.rvDoAn.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDoAn.setAdapter(adapter);

        binding.fabAddDoAn.setOnClickListener(v -> showAddEditDialog(null));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_load_sample_data) {
            onLoadSampleDataRequested();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onLoadSampleDataRequested() {
        boolean hasData = doAnDao.hasData();
        if (hasData) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.dialog_sample_data_exists_title)
                    .setMessage(R.string.dialog_sample_data_exists_message)
                    .setPositiveButton(R.string.action_add_more, (dialog, which) -> importSampleData())
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        } else {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.dialog_sample_data_title)
                    .setMessage(R.string.dialog_sample_data_message)
                    .setPositiveButton(R.string.confirm, (dialog, which) -> importSampleData())
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }
    }

    private void importSampleData() {
        SampleDataLoader.ImportResult result = SampleDataLoader.importFromAssets(this);
        if (result.isSuccess()) {
            loadData();
            String msg = getString(
                    R.string.sample_data_success_format,
                    result.getProjectsCount(),
                    result.getTasksCount(),
                    result.getMeetingsCount()
            );
            Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(binding.getRoot(), R.string.sample_data_failed, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<DoAnWithTienDo> list = doAnDao.getAllWithTienDo();
        adapter.submitList(list);

        if (list.isEmpty()) {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.rvDoAn.setVisibility(View.GONE);
        } else {
            binding.layoutEmpty.setVisibility(View.GONE);
            binding.rvDoAn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(DoAnWithTienDo item) {
        android.content.Intent intent = new android.content.Intent(this, com.example.app.ui.DoAnDetailActivity.class);
        intent.putExtra(com.example.app.ui.DoAnDetailActivity.EXTRA_DO_AN_ID, item.getDoAn().getId());
        startActivity(intent);
    }

    @Override
    public void onEditClick(DoAnWithTienDo item) {
        showAddEditDialog(item.getDoAn());
    }

    @Override
    public void onDeleteClick(DoAnWithTienDo item) {
        DoAn doAn = item.getDoAn();
        String message = String.format(getString(R.string.delete_project_confirm), doAn.getTen());

        com.example.app.ui.ConfirmDeleteDialogFragment dialog =
                com.example.app.ui.ConfirmDeleteDialogFragment.newInstance(
                        com.example.app.ui.ConfirmDeleteDialogFragment.REQUEST_KEY_DELETE,
                        getString(R.string.delete_project_title),
                        message,
                        com.example.app.ui.ConfirmDeleteDialogFragment.TYPE_DO_AN,
                        doAn.getId()
                );
        dialog.show(getSupportFragmentManager(), com.example.app.ui.ConfirmDeleteDialogFragment.TAG);
    }

    private void showAddEditDialog(DoAn existing) {
        com.example.app.ui.AddEditDoAnDialogFragment dialog = com.example.app.ui.AddEditDoAnDialogFragment.newInstance(existing);
        dialog.setOnDoAnSavedListener(this::loadData);
        dialog.show(getSupportFragmentManager(), "AddEditDoAnDialogFragment");
    }
}
